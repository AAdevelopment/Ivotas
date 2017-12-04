package Server_RMI;




import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import mesa_voto.Mesa_voto;

/**
 *
 * @author gustavo
 */

public class Server_RMI  extends UnicastRemoteObject implements Comunication_server,Runnable,Serializable {
    static Comunication_client c;
    private static final long serialVersionUID = 1L;
    static Thread t;
    static DatagramSocket  aSocket;
    static Server_RMI server;
    Integer qtd_voters;
    
    /*BUFFERS DE DADOS PARA ARMAZENAR NOS DOIS SERVIDORES*/
     ArrayList <ListaCandidatos> buffercandidatos= new ArrayList();
     ArrayList <Pessoa> bufferPessoas= new ArrayList();
     ArrayList<Eleicao> bufferEleicao= new ArrayList();
     ArrayList<Faculdade> bufferFaculdade= new ArrayList();
     Set <Mesa_voto> bufferMesas= new LinkedHashSet<Mesa_voto>();
    
    public Server_RMI() throws RemoteException{
        super();
    }
    
    //INTERFACE REMOTE METHODS;
    @Override
    public String Test_connection() throws RemoteException {
		return "Server: Running!";
    }
    @Override
    public void subscribe(String name, Comunication_client c) throws RemoteException {
        System.out.println("Subscribing "+name);
        this.c = c;
    }
    
    
    @Override
    public void CriarLista( ArrayList<String> array,String nome,String tipo){
      
 
                ListaCandidatos l = new ListaCandidatos(nome,tipo);
        
                try {
                    FileWriter file = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/listas",true);
                    //FileWriter file = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\listas.txt",true);
                    BufferedWriter out = new BufferedWriter(file);
                    l.setList(array);
                    out.write(l.nome+"|"+l.tipo+"|");
                    for(int i=0;i<l.candidatos.size();i++)
                        out.write(l.candidatos.get(i)+"|");
                    out.newLine();
                    out.close();
                    this.buffercandidatos.add(l);
                    c.reply_list_on_client(l);
                    System.out.println(l);
                } catch (IOException ex) {
                    ex.getMessage();
            }
    }
    
    public void LoadList() throws FileNotFoundException, IOException{
          String s;
          ListaCandidatos lista;
          FileReader read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/listas");
          //FileReader read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\listas.txt");
          BufferedReader in = new BufferedReader(read);
          while((s=in.readLine())!=null){
              String a[];
              a=s.split("\\|");
              lista = new ListaCandidatos(a[0],a[1]);
              for(int i=2;i<a.length;i++)
                lista.candidatos.add(a[i]);
               
              this.buffercandidatos.add(lista);
              System.out.println(lista);
           }
          
    }
        
   @Override
    public synchronized void CriarFaculdade_Dpto(String nome,ArrayList<String> array) throws RemoteException{
        FileWriter file = null;
        try {
            Faculdade f = new Faculdade(nome);
            file = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/Faculdade_dpto",true);
            //file = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Faculdade_dpto.txt",true);
            BufferedWriter out = new BufferedWriter(file);
            f.criarDPTO(array);
            out.write(f.nome+"|");
            for (int i = 0; i <f.dpto.size(); i++) {
                out.write(f.dpto.get(i)+"|");
            }
            out.newLine();
            out.close();
            this.bufferFaculdade.add(f);
            c.reply_FacultyDptolist_on_client(f);
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void LoadFaculdade_Dpto()throws FileNotFoundException, IOException{
         String s;
          Faculdade f ;
          FileReader read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/Faculdade_dpto");
          //FileReader read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Faculdade_dpto.txt");
          BufferedReader in = new BufferedReader(read);
          while((s=in.readLine())!=null){
              String a[];
              a=s.split("\\|");
              f = new Faculdade(a[0]);
              for(int i=1;i<a.length;i++)
                f.dpto.add(a[i]);
               
              this.bufferFaculdade.add(f);
              System.out.println(f.toString());
           }
    }
    
    /*
    *
    * A FUNCAO VOTE TEM DE RETORNAR TRUE SE O VOTO FOI BEM SUCEDIDO 
    *
    */
     
    @Override
    public synchronized boolean vote(String lista, Eleicao eleicao, Pessoa pessoa, Mesa_voto mesa, Calendar data)throws RemoteException{
        boolean voted=false;
        Voto vote=new Voto (data, eleicao,mesa);
        for(int i=0; i<this.bufferPessoas.size();i++){
            if(Objects.equals(this.bufferPessoas.get(i).cartao, pessoa.cartao)){
                this.bufferPessoas.get(i).votos.add(vote);
            }
        }
        for (int i=0; i<this.bufferEleicao.size();i++){
            if(this.bufferEleicao.get(i).titulo.equalsIgnoreCase(eleicao.titulo)){
                for(int j=0;j<this.bufferEleicao.get(i).listas_candidatas.size();j++){
                    if(this.bufferEleicao.get(i).listas_candidatas.get(j).nome.equalsIgnoreCase(lista)){
                        this.bufferEleicao.get(i).listas_candidatas.get(j).votos.add(vote);
                        voted=true;
                    }
                }
            }
        }
        
        this.printBufferEleicao(bufferEleicao);
        this.printBufferPessoas(bufferPessoas);
        this.savePessoas();
        this.saveArrayEleicao();
        return voted;
        
    }
    

    
     public synchronized  void criarEleicao(String saida[]) throws RemoteException{
        SortedSet<Mesa_voto>sorter= new TreeSet<Mesa_voto>(Comparator.comparing(Mesa_voto::getID)); 
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Calendar data_inicio= Calendar.getInstance();
        Calendar data_fim= Calendar.getInstance();
        Set<String> tables = new  LinkedHashSet<String>();
        Mesa_voto mesa;
        Eleicao  el;
        Set<Mesa_voto>mesas_passagem = new HashSet<Mesa_voto>();
        try{    
            data_inicio.setTime(format.parse(saida[3]));
            data_fim.setTime(format.parse(saida[4]));
            el = new Eleicao(saida[0],saida[1],saida[2],data_inicio,data_fim);
            tables.addAll(c.Add_table_to_election(this.bufferMesas));
            for(String m:tables){
                mesa = new Mesa_voto(m);
                mesa.ID=this.bufferMesas.size()+1;
                el.mesas.add(mesa);
                int qtd=0;
                if(this.bufferMesas.isEmpty()){
                    this.bufferMesas.add(mesa);
                    mesas_passagem.add(mesa);
                }
                else{
                    for(Mesa_voto i:this.bufferMesas){
                        if(!i.departamento.equals(mesa.departamento)){
                            qtd++;
                            if(qtd==this.bufferMesas.size()){
                                this.bufferMesas.add(mesa);
                                mesas_passagem.add(mesa);
                            }
                        } 
                    }
                }
            }
            System.out.println("\nTotal Mesas:");
            sorter.addAll(this.bufferMesas);
            for(Mesa_voto m:sorter){
                System.out.println(m.toSring());
            }
            saveMesa(mesas_passagem);
            el.ID=this.bufferEleicao.size()+1;
            el.StartEleicao();
            System.out.println(el.toString());  
            el.listas_candidatas.addAll(c.Add_lists_toElection(this.buffercandidatos, el));
            c.replyElection(el);
            ArrayList<ListaCandidatos>list=el.getListas_candidatas();
            for (int i = 0; i <list.size(); i++) {
                System.out.println(list.get(i));
            }
            
            this.bufferEleicao.add(el);
            this.saveEleicao(el);
            
            } catch (ParseException ex) {
                Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    public void saveMesa(Set<Mesa_voto>mesa){
        FileWriter file = null;
        SortedSet<Mesa_voto>sorter= new TreeSet<Mesa_voto>(Comparator.comparing(Mesa_voto::getID));
                                                                                                    
        try {
            
            file = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/src/mesas.txt",true);
            //file = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\mesas.txt",true);
            BufferedWriter out = new BufferedWriter(file);
            int qtd=0;
            sorter.addAll(mesa);
            for(Mesa_voto m:sorter){
                out.write("id|;"+m.ID+"|"+m.departamento);
                out.newLine();
            }
            
            out.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public Set<Mesa_voto> loadMesas() throws FileNotFoundException, IOException{
          String s;
          FileReader read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/src/mesas.txt");
          //FileReader read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Faculdade_dpto.txt");
          BufferedReader in = new BufferedReader(read);
          while((s=in.readLine())!=null){
              String a[];
              a=s.split("\\|");
              Mesa_voto m = new Mesa_voto(a[2]);
              String b[];
              b=a[1].split(";");
              m.ID=Integer.parseInt(b[1]);
              this.bufferMesas.add(m);
              
           }
        return this.bufferMesas;    
    }    
    @Override
     public synchronized  void alterar_eleicao(String nome,String v[]){  
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Calendar day= Calendar.getInstance();
        for(int i=0;i<this.bufferEleicao.size();i++){
            if(this.bufferEleicao.get(i).titulo.equalsIgnoreCase(nome)){
                try {
                    if(v[0]!=null)
                        this.bufferEleicao.get(i).setTipo(v[0]);
                    if(v[1]!=null)
                        this.bufferEleicao.get(i).setTitulo(v[1]);
                    if(v[2]!=null)
                        this.bufferEleicao.get(i).setDescricao(v[2]);
                    if(v[3]!=null){
                        day.setTime(format.parse(v[3]));
                        this.bufferEleicao.get(i).setData_inicio(day);
                    }
                    if(v[4]!=null){
                        day.setTime(format.parse(v[4]));
                        this.bufferEleicao.get(i).setData_fim(day);
                    }
                    
                    FileReader read;
                    File arquivo = new File("/home/gustavo/NetBeansProjects/Ivotas/"+nome);
                    //File arquivo = new File("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\"+nome);
                    if(arquivo.exists()){
                        try {
                            read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/"+nome);
                            //read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\"+nome);
                            BufferedReader in = new BufferedReader(read);
                            String linha="";
                            String arquivo_todo;
                            linha=in.readLine();
                            arquivo.renameTo(new File("/home/gustavo/NetBeansProjects/Ivotas/"+this.bufferEleicao.get(i).getTitulo()));
                            //arquivo.renameTo(new File("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\"+this.bufferEleicao.get(i).getTitulo()));
                            FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/"+this.bufferEleicao.get(i).getTitulo());
                            //FileWriter out = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\"+this.bufferEleicao.get(i).getTitulo());
                            out.write(this.bufferEleicao.get(i).toString()+"\n");
                            while((arquivo_todo=in.readLine())!=null){
                                if(arquivo_todo.equals(linha)){
                                    continue;
                                }                           
                                out.write(arquivo_todo+"\n");
                            }
                            out.close();
                            read.close();
                            in.close();
                            
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
                        }   catch (IOException ex) {
                            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                } catch (ParseException ex) {           
                    Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
        }
        
            
        }
    @Override
     public synchronized  void CadastrarPessoa(String o[]) throws RemoteException{
  
        try {
            
            Pessoa p = new Pessoa(o[0],o[1],Long.parseLong(o[2]),o[3],o[4],o[5],o[6],o[7]);
            this.bufferPessoas.add(p);
            this.savePessoas();
            c.replyPeople(p);
        } catch (ParseException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
      
   public synchronized void Count_voters(Eleicao e,Mesa_voto mesa) throws RemoteException{ 
        String state="Eleicao: "+e.titulo+"|"+"Tipo: "+e.tipo+"|"+"Mesa: "+mesa.ID+"|"+mesa.departamento+"|"+"Numero de eleitores: "+mesa.Nr_Voters+"\n";
        c.replyNrVoters(state);
    }
    
    //public synchronized void Count_voters(Mesa_voto mesa) throws RemoteException{ 
    //    String state= mesa.toSring()+"|"+mesa.Nr_Voters++;
    //    c.replyNrVoters(state);
   // }
    
    
     
     /************************************************************************************************************************
     *
     *Author: Andre Santos
     *
     **/
     
      //salva a lista de candidatos de uma eleicao
     public void addMesaVoto(Mesa_voto mesa){
        this.bufferMesas.add(mesa);
    }
    public void saveEleicao (Eleicao eleicao){
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

        try {
            //String path="/home/gustavo/NetBeansProjects/Ivotas/"+eleicao.titulo;
            String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Eleicoes\\"+eleicao.titulo+".txt";
            FileWriter file = new FileWriter(path);
            BufferedWriter out = new BufferedWriter(file);
            int i=0;
            int j=0;
            out.write("titulo|tipo|descricao|data|departamentos");
            out.newLine();
            out.write(eleicao.ID+"|"+eleicao.titulo+"|"+eleicao.tipo+"|"+eleicao.descricao+"|"+format.format(eleicao.data_inicio.getTime())+"|"+format.format(eleicao.data_fim.getTime())+"|");
            if(eleicao.dptos.size()!=0){
                for(i=0; i<eleicao.dptos.size()-1;i++){
                    out.write(eleicao.dptos.get(i)+",");
                }
                 out.write(eleicao.dptos.get(i));
            }
            out.newLine();


            for(i=0; i<eleicao.listas_candidatas.size();i++){
                out.write(eleicao.listas_candidatas.get(i).nome+"|"+eleicao.listas_candidatas.get(i).tipo+"|"+eleicao.listas_candidatas.get(i).votos.size());
                out.newLine();
                for(j=0;j<eleicao.listas_candidatas.get(i).candidatos.size();j++){
                    out.write(eleicao.listas_candidatas.get(i).candidatos.get(j)+"|");   
                }
                 out.newLine();
                for(int k=0;k<eleicao.listas_candidatas.get(i).votos.size();k++){
                    out.write(eleicao.listas_candidatas.get(i).votos.get(k).toString());
                    out.newLine();
                }
            }

            out.close();
              
        } catch (FileNotFoundException ex) {
            System.out.println("FIle not found!");
        } catch (IOException ex) { 
            System.out.println("IOEXCEPITON SaveEleicao");
        }
    }
    
    
    public Eleicao loadEleicao(String eleicao_titulo){
        ArrayList<String> dptos=null;
        Eleicao eleicao=null;
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Calendar data_inicio= Calendar.getInstance();
        Calendar data_fim= Calendar.getInstance();
        
        try {   
                String path="/home/gustavo/NetBeansProjects/Ivotas/src/Eleicoes/"+eleicao_titulo+".txt";
                //String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Eleicoes\\"+eleicao_titulo+".txt";
                FileReader read = new FileReader(path);
                BufferedReader in = new BufferedReader(read);
                
                int ID;
                String tipo, titulo, descricao;
                String s="";
                int votos=0;
                String array[];
                String deps[];
                in.readLine();  //ignora a primeira linha
                s=in.readLine();    //le eleicao
                array=s.split("\\|");
                //System.out.println(Arrays.toString(array));
                if(array.length==7){
                    deps=array[6].split(",");   // guarda os departamentos
                
                //System.out.println(Arrays.toString(deps));
                    dptos=new ArrayList<>(Arrays.asList(deps));
                }
                else{
                    dptos=new ArrayList<>();
                }
                ID=Integer.parseInt(array[0]);
                tipo=array[2];
                titulo=array[1];
                descricao=array[3];
                data_inicio.setTime(format.parse(array[4]));
                data_fim.setTime(format.parse(array[5]));
                
                eleicao=new Eleicao(ID,tipo,titulo,descricao,data_inicio, data_fim, dptos);

                while((s=in.readLine())!=null){
                    array=s.split("\\|");
                    //System.out.println(Arrays.toString(array));
                    ListaCandidatos aux=new ListaCandidatos(array[0],array[1]);
                    votos=Integer.parseInt(array[2]);
                    s=in.readLine();
                    array=s.split("\\|");
                    for(int i=0;i<array.length;i++){    //adiciona as pessoas a lista de candidatos
                        aux.candidatos.add(array[i]);
                    }
                    for(int k=0;k<votos;k++){
                        s=in.readLine();
                        array=s.split(";");
                        Mesa_voto mesa=procuraMesa(array[1]);
                        SimpleDateFormat dt=new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                        Calendar date= Calendar.getInstance();
                        date.setTime(dt.parse(array[2]));
                        Voto voto=new Voto(date,eleicao,mesa);
                        aux.votos.add(voto);
                    }
                    eleicao.setLista(aux);
                }
                  
        } catch (FileNotFoundException ex) {
            ex.getMessage();
        } catch (IOException ex) { 
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return eleicao;
    }
    
    public void saveArrayEleicao(){
       
        for(int i=0; i<this.bufferEleicao.size();i++){
            Eleicao eleicao= this.bufferEleicao.get(i);
            this.saveEleicao(eleicao);
        }
    }
     public void loadArrayEleicao(){
        String path="/home/gustavo/NetBeansProjects/Ivotas/src/Eleicoes/"; 
        //String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Eleicoes\\";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

            
            try {
                for (int i = 0; i < listOfFiles.length; i++) {
                    FileReader file= new FileReader( path+listOfFiles[i].getName());
                    BufferedReader in = new BufferedReader(file);
                    Eleicao eleicao=loadEleicao(listOfFiles[i].getName().replace(".txt", ""));
                    this.bufferEleicao.add(eleicao);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
            }
                
    } 
   
    
    
    @Override
    public ArrayList<ListaCandidatos> get_Listas(Eleicao eleicao){
        for(int i=0; i<this.bufferEleicao.size();i++){
            if(this.bufferEleicao.get(i).titulo.equalsIgnoreCase(eleicao.titulo))
                return this.bufferEleicao.get(i).listas_candidatas;
        }
        
        return null;
    }
     
    @Override
    public  Pessoa autenticate(String campo, String dados){
        for (int i=0; i<this.bufferPessoas.size();i++){
            switch (campo){
                case "nome":{
                    if(this.bufferPessoas.get(i).name.equalsIgnoreCase(dados))
                        return bufferPessoas.get(i);
                    break;
                }
                case "cartao":{
                    if(this.bufferPessoas.get(i).cartao.toString().equalsIgnoreCase(dados))
                        return bufferPessoas.get(i);
                    break;
                }
                case "password":{
                    if(this.bufferPessoas.get(i).Password.equalsIgnoreCase(dados))
                        return bufferPessoas.get(i);
                    break;
                }
                case "morada":{
                    if(this.bufferPessoas.get(i).morada.equalsIgnoreCase(dados))
                        return bufferPessoas.get(i);
                    break;
                }
                case "telefone":{
                    if(this.bufferPessoas.get(i).tel.equalsIgnoreCase(dados))
                        return bufferPessoas.get(i);
                    break;
                }
            }
        }
        return null;

    }
    
    @Override
    public  Resposta unlock_terminal(Pessoa pessoa,String CC, String Password){
        Resposta resposta=new Resposta(0,"");
        if(pessoa.Password.equals(Password) ){
            if(pessoa.cartao.toString().equals(CC)){
                if(pessoa.card_valid.before(new Date())){
                    resposta.mensagem="Terminal Operacional";
                    resposta.valor=1;
                }
                else {
                    resposta.mensagem="O seu CC encontra-se invalido.";
                    resposta.valor=-1;
                }
            }else {
                resposta.mensagem="Numero de CC invalido.";
                resposta.valor=-1;
            }
        }else{
            resposta.mensagem="Password incompativel.";
            resposta.valor=-1;
        }
        return resposta;
 }

    
    @Override
    public ArrayList<Eleicao> get_Eleicoes(){
        return this.bufferEleicao;
    }
     @Override
    public Eleicao getEleicao(String titulo){
        for(int i=0; i<this.bufferEleicao.size();i++){
            if(this.bufferEleicao.get(i).titulo.equals(titulo))
                return this.bufferEleicao.get(i);
        }
        return null;                
    }
    
    // SERVER METHODS
    
    
    public  void CarregaPessoas() throws FileNotFoundException, IOException, ParseException{
        boolean exists = (new File("/home/gustavo/NetBeansProjects/Ivotas/src/Pessoas.txt")).exists();
        //boolean exists = (new File("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Pessoas.txt")).exists();
        if (exists) {
            FileReader read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/src/Pessoas.txt");
            //FileReader read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Pessoas.txt");
            BufferedReader in = new BufferedReader(read);
            String s="";
            String[] a= new String[9];
            String[] b;
            in.readLine(); // ignora cabeçalho
            while((s=in.readLine())!=null){
                a=s.split(";");
                 // System.out.println(Arrays.toString(a));
                Pessoa p = new Pessoa(a[0],a[1],Long.parseLong(a[2]),a[3],a[4],a[5],a[6],a[7]);
                if(a.length>8){
                  for(int i=0;i<Integer.parseInt(a[8]);i++){
                      s=in.readLine();
                      b=s.split(";");
                      Eleicao eleicao=procuraEleicao(b[0]);
                      Mesa_voto mesa=procuraMesa(b[1]);
                      Calendar data = Calendar.getInstance(); 
                      data.setTime(new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").parse(b[2]));
                      Voto voto=new Voto(data,eleicao,mesa);
                      p.votos.add(voto);
                  }
              }
              this.bufferPessoas.add(p);
              System.out.println(p.toString());
            }
            in.close();
        }
    }
    public Eleicao procuraEleicao(String titulo){
        for(int i=0;i<this.bufferEleicao.size();i++){
            if(this.bufferEleicao.get(i).titulo.equalsIgnoreCase(titulo))
                return this.bufferEleicao.get(i);
        }
        return null;
    }
    /*public Mesa_voto procuraMesa(String departamento){
        for(int i=0;i<this.bufferMesas.size();i++){
            if(this.bufferMesas.get(i).departamento.equalsIgnoreCase(departamento))
                return this.bufferMesas.get(i);
        }
        return null;
    }*/
    
    public Mesa_voto procuraMesa(String departamento){
        for(Mesa_voto m:this.bufferMesas){
            if(m.departamento.equalsIgnoreCase(departamento))
                return m;
        }
        return null;
    }
    public void savePessoas(){
        
            FileWriter file= null;
            try {
                //file = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/pessoas");
                file = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto_meta2\\Ivotas\\src\\Pessoas.txt");
                BufferedWriter out = new BufferedWriter(file);
                SimpleDateFormat dt = new SimpleDateFormat("dd-mm-yyyy");
                String s="";
                String[] a=null;
                out.write("Tipo ; Nome ; Cartao ; Password ; Dpto ; Card_valid ; Telefone ; Morada");
                out.newLine();
                for(int i=0;i<this.bufferPessoas.size();i++){
                    out.write(this.bufferPessoas.get(i).tipo_pessoa+';'+this.bufferPessoas.get(i).name+';'+this.bufferPessoas.get(i).cartao+';'+this.bufferPessoas.get(i).Password+';'+this.bufferPessoas.get(i).Dpto+';'+
                            dt.format(this.bufferPessoas.get(i).card_valid)+';'+this.bufferPessoas.get(i).tel+';'+this.bufferPessoas.get(i).morada+';'+this.bufferPessoas.get(i).votos.size());
                    out.newLine();
                    for(int j=0;j<bufferPessoas.get(i).votos.size();j++){
                        out.write(bufferPessoas.get(i).votos.get(j).toString());
                        out.newLine();
                    }
                }
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    public void printBufferPessoas(ArrayList <Pessoa> lista){
        for (int i=0;i<lista.size();i++){
            System.out.println(lista.get(i).toString());
            if(lista.get(i).votos.size()>0){
                for(int j=0;j<lista.get(i).votos.size();j++)
                    System.out.println(lista.get(i).votos.get(j).toString());
            }
        }
    }
    public void printBufferEleicao(ArrayList <Eleicao> lista){
        for (int i=0;i<lista.size();i++){
            System.out.print(lista.get(i).toString());
            System.out.print("|Dptos: ");

            for(int j=0;j<lista.get(i).dptos.size();j++){
                System.out.print(lista.get(i).dptos.get(j)+"; ");
            }
            System.out.println();
            System.out.println("[Listas]: ");
            for(int j=0;j<lista.get(i).listas_candidatas.size();j++){
                lista.get(i).listas_candidatas.get(j).printListaCandidatos();
            }
            System.out.println();
        }
    }
    public boolean isToStart(Eleicao eleicao){
        Date today= new Date();
        SimpleDateFormat dt = new SimpleDateFormat("hh:mm");
        if(eleicao.data_inicio.before(today) || eleicao.data_fim.after(today))
            return false;
        else{
            return true;
        }
    }
    @Override
    public void run() {
        for (int i = 0; i < this.bufferEleicao.size(); i++) {
            if(isToStart(this.bufferEleicao.get(i))==true)
                this.bufferEleicao.get(i).StartEleicao();
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      
    }
    /*@Override
    public void run(){
        try {
             String teste="Mensagem";
             String Response;
             while(true){
                System.out.println("Teste");
                byte[] bufferReceive = new byte[1000];
                DatagramPacket request_receive = new DatagramPacket(bufferReceive,bufferReceive.length);
                aSocket.receive(request_receive);
                Response=new String(request_receive.getData(), 0, request_receive.getLength());	
	        System.out.println("Server Recebeu: " + Response);
                
                byte[] bufferRequest =teste.getBytes();
                
                InetAddress aHost = InetAddress.getByName("6502");
                int serverPort = 6504;
                DatagramPacket request = new DatagramPacket(bufferRequest,bufferRequest.length,aHost,serverPort); 
                aSocket.send(request);
               
              
                Thread.sleep(2000);
            }
        } catch (InterruptedException | UnknownHostException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
           // Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void StartUDPConnection(){
        t = new Thread(this,"Thread1");
        t.start();
    }*/
    
    
    public static void main(String args[])throws RemoteException, MalformedURLException, SocketException, IOException, FileNotFoundException,ParseException {
        
         try{
            
            
          // System.getProperties().put("java.security.policy","/home/gustavo/NetBeansProjects/ivotas/Ivotas/src/Server_RMI/policy.all");
           //System.setSecurityManager(new RMISecurityManager());
            
            Server_RMI server = new Server_RMI();
           
            Registry r = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
            r.rebind("connection_RMI",server);
            server.LoadList();
            server.LoadFaculdade_Dpto();
            Set<Mesa_voto> mesas =new LinkedHashSet();
            mesas=server.loadMesas();
            for (Mesa_voto m:mesas) {
                 System.out.println(m.toSring());
            }
            Mesa_voto mesa=new Mesa_voto("DOG");
            Mesa_voto mesa_dem=new Mesa_voto("DEEC");
            server.addMesaVoto(mesa);
            server.addMesaVoto(mesa_dem);
            server.loadArrayEleicao();
            server.CarregaPessoas();    // as pessoas tem de ser carregadas depois das eleicoes

            
            ArrayList<String> faculdades= new ArrayList(Arrays.asList("DEI", "DEEC", "DEM"));
            ListaCandidatos A= new ListaCandidatos("Snow","alunos");
            ArrayList<String> lista1= new ArrayList(Arrays.asList("Rhaegar Targarien","Jaime Lannister")); 
            ListaCandidatos B= new ListaCandidatos("Stark","alunos");
            ArrayList<String> lista2= new ArrayList(Arrays.asList("Daenherys Targarien","Tyrion Lannister"));
            SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            Calendar inicio= Calendar.getInstance();
            inicio.setTime(formatter.parse("15:35:46 20/11/1995"));
            Calendar fim= Calendar.getInstance();
            fim.setTime(formatter.parse("16:35:50 20/11/1995"));
            Eleicao eleicao=new Eleicao(100,"nucleo","Eleicao15","eleicao nucleo DEM", inicio, fim, faculdades);
            A.setLista(lista1);
            B.setLista(lista2);
            eleicao.mesas.add(mesa_dem);
            eleicao.listas_candidatas.add(A);
            eleicao.listas_candidatas.add(B);
            Calendar today= Calendar.getInstance();
            today.setTime(today.getTime());
            Voto vote= new Voto (today,eleicao,mesa_dem);
            eleicao.listas_candidatas.get(0).votos.add(vote);
           
            server.bufferEleicao.add(eleicao);
            server.buffercandidatos.add(A);
            server.buffercandidatos.add(B);
            server.printBufferEleicao(server.bufferEleicao);
            server.printBufferPessoas(server.bufferPessoas);
            
//            aSocket = new DatagramSocket(Integer.parseInt(args[1]));
           // System.out.println("Socket Datagram à escuta no porto "+args[1]);
           
            server.savePessoas();
            server.saveArrayEleicao();
                  
            
        }catch(RemoteException re){
            System.out.println(re.getMessage());
        }
    } 

}

