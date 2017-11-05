/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;



import Server_RMI.Comunication_server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.rmi.server.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
//import java.util.HashMap;
//import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import mesa_voto.Mesa_voto;

/**
 *
 * @author gustavo
 */

public class Server_RMI  extends UnicastRemoteObject implements Comunication_server {
    static Comunication_client c;
    private static final long serialVersionUID = 1L;
    Thread t;
    static DatagramSocket  aSocket;
    static Server_RMI server;
    
    /*BUFFERS DE DADOS PARA ARMAZENAR NOS DOIS SERVIDORES*/
    ArrayList <ListaCandidatos> buffercandidatos= new ArrayList();
    ArrayList <Pessoa> bufferPessoas= new ArrayList();
    ArrayList<Eleicao> bufferEleicao= new ArrayList();
    ArrayList<Faculdade> bufferFaculdade= new ArrayList();
    ArrayList<Mesa_voto> bufferMesas= new ArrayList();
    
    public Server_RMI() throws RemoteException{
        super();
    }
    
    //interface methods;
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
    public   void CriarLista(){
        String nome="";
        String tipo="";
        nome=JOptionPane.showInputDialog("Digite o nome da lista:");
        tipo=JOptionPane.showInputDialog("Digite o tipo da lista:");
        ListaCandidatos l = new ListaCandidatos(nome,tipo);
        try {
            FileWriter out = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\listas.txt",true);
            int n=0;
            String saida="";
            boolean verifica =true;
            while(verifica==true){
                saida=JOptionPane.showInputDialog("digite o nome do candidato, clique em cancel para sair:");
                if(saida==null){
                    verifica =false;   
                    break;
                }
                else{
                    l.setList(saida);    
                }
            
            }
            out.write(l.toString()+"qtd=0"+"\n");
            out.close();
            this.buffercandidatos.add(l);
            c.reply_list_on_client(l);
            System.out.println(l);
          } catch (IOException ex) {
            ex.getMessage();
        }
    }
  
   @Override
    public synchronized void CriarFaculdade_Dpto() throws RemoteException{
        String nome="";
        nome=JOptionPane.showInputDialog("Digite o nome da faculdade:");
        Faculdade f = new Faculdade(nome);
        String saida="";
        try {
            FileWriter out = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Faculdade_dpto",true);
       
        boolean verifica =true;
        while(verifica==true){
            saida=JOptionPane.showInputDialog("digite o nome do Departamento, clique em cancel para sair:");
            if(saida==null){
                verifica =false;   
                break;
            }
            else{
                f.criarDPTO(saida);    
            }
        }
        out.write(f.toString()+"\n");
        out.close();
        this.bufferFaculdade.add(f);
        c.reply_FacultyDptolist_on_client(f);

        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    *
    * A FUNCAO VOTE TEM DE RETORNAR TRUE SE O VOTO FOI BEM SUCEDIDO 
    *
    */
     
    @Override
    public synchronized void vote(String lista, Eleicao eleicao, Pessoa pessoa, Mesa_voto mesa, Date data)throws RemoteException{
        Voto vote=new Voto (data, eleicao,mesa);
        for(int i=0; i<this.bufferPessoas.size();i++){
            if(Objects.equals(this.bufferPessoas.get(i).cartao, pessoa.cartao)){
                this.bufferPessoas.get(i).votos.add(vote);
            }
        }
        for (int i=0; i<this.bufferEleicao.size();i++){
            if(this.bufferEleicao.get(i).equals(eleicao))
                for(int j=0;j<this.bufferEleicao.get(i).listas.size();j++){
                    if(this.bufferEleicao.get(i).listas.get(j).nome.equalsIgnoreCase(lista)){
                        this.bufferEleicao.get(i).listas.get(j).votos.add(vote);
                    }
                }
        }
        this.saveArrayEleicao();
        this.savePessoas();
        
    }
    
    public synchronized void Add_ELectionlocal(String local,Pessoa p){
        try {
            String nome =p.name;
            FileReader read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Pessoas.txt");
            BufferedReader in = new BufferedReader(read);
            FileWriter out = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Pessoas.txt",true);
            String s="";
            while((s=in.readLine())!=null){
                String a[];
                a=s.split(";");
                if(nome.equals(a[0])){
                    out.write(local);
                    out.close();
                }
                
            }
            this.bufferPessoas.add(p);
            
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   @Override
    public synchronized  void criarEleicao(String saida[]) throws RemoteException{ 
          
            Eleicao  el;
            try{    
                    File file = new File("/home/gustavo/NetBeansProjects/Ivotas/"+saida[1]);
                    FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/"+saida[1],true);
                    el = new Eleicao(saida[0],saida[1],saida[2],saida[3],saida[4]);
                    el.StartEleicao();
                    this.bufferEleicao.add(el);
                    out.write(el.toString());
                    out.close();
                    ///this.saveEleicao(el);
                    System.out.println(el);
                    c.replyElection(el);
                    printBufferEleicao(this.bufferEleicao);
                  } catch (ParseException ex) {
                      Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
                  } catch (IOException ex) {
                   Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
               }    
            }
            
        
           
    @Override
     public synchronized  void alterar_eleicao(String nome,String v[]){ 
        for(int i=0;i<this.bufferEleicao.size();i++){
            if(this.bufferEleicao.get(i).titulo.equalsIgnoreCase(nome)){
                try {
                    if(v[0]!=null)
                        this.bufferEleicao.get(i).setTipo(v[0]);
                    if(v[1]!=null)
                        this.bufferEleicao.get(i).setTitulo(v[1]);
                    if(v[2]!=null)
                        this.bufferEleicao.get(i).setData(v[2]);
                    if(v[3]!=null)
                        this.bufferEleicao.get(i).setHoraini(v[3]);
                    if(v[4]!=null)
                        this.bufferEleicao.get(i).setHorafim(v[4]);
                    
                    FileReader read;
                    File arquivo = new File("/home/gustavo/NetBeansProjects/Ivotas/"+nome);
                    
                    if(arquivo.exists()){
                        try {
                            read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/"+nome);
                            BufferedReader in = new BufferedReader(read);
                            String linha="";
                            String arquivo_todo;
                            linha=in.readLine();
                            arquivo.renameTo(new File("/home/gustavo/NetBeansProjects/Ivotas/"+this.bufferEleicao.get(i).getTitulo()));
                            FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/"+this.bufferEleicao.get(i).getTitulo());
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
     public synchronized  void CadastrarPessoa(){
        String tipo_pessoa=""; 
        String name ="";
        Long cartao = null;
        String Password = "";
        String Dpto_facul="";
        String card_valid="";
        String tel="";
        String morada="";
        
        String s[]={"Cadastrar tipo pessoa","Cadastrar nome:","Cadastrar Cartao do cidadao:","Cadastrar Password","Cadastrar DPto","Cadastrar Card_valid dd-mm-yyyy",
            "Cadastrar telefone","Cadastrar Moradia"};
        String o[] = new String[s.length];
        
        for(int i=0;i<o.length;i++){
           o[i]=JOptionPane.showInputDialog(s[i]); 
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);
        try {
            FileWriter out = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Pessoas.txt",true);
            Pessoa p = new Pessoa(o[0],o[1],Long.parseLong(o[2]),o[3],o[4],o[5],o[6],o[7]);
            String saida="";
            saida=p.getTipoPessoa()+";"+p.getName()+";"+p.getCartao()+";"+p.getPassword()+";"+
            p.getDpto()+";"+formatter.parse(o[5])+";"+p.getTel()+";"+p.getMorada();
            out.write(saida+"\n");
            out.close();
            this.bufferPessoas.add(p);
        } catch (ParseException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void addMesaVoto(Mesa_voto mesa){
        this.bufferMesas.add(mesa);
    }

    
     
     /************************************************************************************************************************
     *
     *Author: Andre Santos
     *
     **/
     
      //salva a lista de candidatos de uma eleicao
    public void saveEleicao (Eleicao eleicao){
        
        try {
                String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Eleicoes\\"+eleicao.titulo+".txt";
                FileWriter file = new FileWriter(path);
                BufferedWriter out = new BufferedWriter(file);
                DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
                int i=0;
                int j=0;
                out.write("titulo|tipo|descricao|data|departamentos");
                out.newLine();
                out.write(eleicao.titulo+"|"+eleicao.tipo+"|"+eleicao.descricao+"|"+formatter.format(eleicao.data)+"|");
                for(i=0; i<eleicao.dptos.size()-1;i++)
                    out.write(eleicao.dptos.get(i)+",");
                 out.write(eleicao.dptos.get(i));
                 out.newLine();
                
                
                for(i=0; i<eleicao.listas.size();i++){
                    out.write(eleicao.listas.get(i).nome+"|"+eleicao.listas.get(i).tipo+"|"+eleicao.listas.get(i).votos.size());
                    out.newLine();
                    for(j=0;j<eleicao.listas.get(i).Lista.size();j++){
                        out.write(eleicao.listas.get(i).Lista.get(j)+"|");   
                    }
                     out.newLine();
                    for(int k=0;k<eleicao.listas.get(i).votos.size();k++){
                        out.write(eleicao.listas.get(i).votos.get(k).toString());
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
        ArrayList<ListaCandidatos> candidatos=new ArrayList();
        Eleicao eleicao=null;
        try {
                String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Eleicoes\\"+eleicao_titulo+".txt";
                FileReader read = new FileReader(path);
                BufferedReader in = new BufferedReader(read);
                
                String tipo, titulo, data, descricao;
                String s="";
                int votos=0;
                String array[];
                String deps[];
                
                in.readLine();  //ignora a primeira linha
                s=in.readLine();    //le eleicao
                array=s.split("\\|");
                //System.out.println(Arrays.toString(array));
                deps=array[4].split(",");   // guarda os departamentos
                //System.out.println(Arrays.toString(deps));
                dptos=new ArrayList<>(Arrays.asList(deps));
                tipo=array[1];
                titulo=array[0];
                data=array[3];
                descricao=array[2];
                eleicao=new Eleicao(tipo,titulo,descricao,data ,dptos);

                while((s=in.readLine())!=null){
                    array=s.split("\\|");
                    //System.out.println(Arrays.toString(array));
                    ListaCandidatos aux=new ListaCandidatos(array[0],array[1]);
                    votos=Integer.parseInt(array[2]);
                    s=in.readLine();
                    array=s.split("\\|");
                    for(int i=0;i<array.length;i++){    //adiciona as pessoas a lista de candidatos
                        aux.Lista.add(array[i]);
                    }
                    for(int k=0;k<votos;k++){
                        s=in.readLine();
                        array=s.split(";");
                        Mesa_voto mesa=procuraMesa(array[1]);
                        Date date = new SimpleDateFormat("hh:mm dd-mm-yyyy").parse(array[2]);
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
        String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Eleicoes\\";
        File folder = new File("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Eleicoes");
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
                return this.bufferEleicao.get(i).listas;
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
    //servers methods
    
    //server runnig;
    public  void CarregaPessoas() throws FileNotFoundException, IOException, ParseException{
        boolean exists = (new File("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Pessoas.txt")).exists();
        if (exists) {
           
            FileReader read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Pessoas.txt");
            BufferedReader in = new BufferedReader(read);
            String s="";
            String[] a= new String[9];
            String[] b;
            in.readLine(); // ignora cabeçalho
            while((s=in.readLine())!=null){
                a=s.split(";");
                 // System.out.println(Arrays.toString(a));
                Pessoa p = new Pessoa(a[0],a[1],Long.parseLong(a[2]),a[3],a[4],(a[5]),a[6],a[7]);
                if(a.length>8){
                  for(int i=0;i<Integer.parseInt(a[8]);i++){
                      s=in.readLine();
                      b=s.split(";");
                      Eleicao eleicao=procuraEleicao(b[0]);
                      Mesa_voto mesa=procuraMesa(b[1]);
                      Date data = new SimpleDateFormat("hh:mm dd-mm-yyyy").parse(b[2]);
                      Voto voto=new Voto(data,eleicao,mesa);
                      p.votos.add(voto);
                  }
              }
              this.bufferPessoas.add(p);
              //System.out.println(p.toString());
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
    public Mesa_voto procuraMesa(String departamento){
        for(int i=0;i<this.bufferMesas.size();i++){
            if(this.bufferMesas.get(i).departamento.equalsIgnoreCase(departamento))
                return this.bufferMesas.get(i);
        }
        return null;
    }
    public void savePessoas(){
        
            FileWriter file= null;
            try {
                file = new FileWriter("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Pessoas.txt");
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
            System.out.println(lista.get(i).toString());
            System.out.print("DPTOS: ");

            for(int j=0;j<lista.get(i).dptos.size();j++){
                System.out.print(lista.get(i).dptos.get(j)+" ; ");
            }
            System.out.println();
            for(int j=0;j<lista.get(i).listas.size();j++){
                lista.get(i).listas.get(j).printListaCandidatos();
            }
        }
    }
    public static void main(String args[])throws RemoteException, MalformedURLException, SocketException, IOException, FileNotFoundException,ParseException {
        
         try{
            
            
          // System.getProperties().put("java.security.policy","/home/gustavo/NetBeansProjects/ivotas/Ivotas/src/Server_RMI/policy.all");
           //System.setSecurityManager(new RMISecurityManager());
            
            Server_RMI server = new Server_RMI();
           
            Registry r = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
            r.rebind("connection_RMI",server);
            Mesa_voto mesa=new Mesa_voto(123,"DEI");
            server.addMesaVoto(mesa);
            server.loadArrayEleicao();
            server.CarregaPessoas();
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
    
    @Override
    public Eleicao getEleicao(String titulo){
        for(int i=0; i<this.bufferEleicao.size();i++){
            if(this.bufferEleicao.get(i).titulo.equals(titulo))
                return this.bufferEleicao.get(i);
        }
        return null;                
    }
    
}

