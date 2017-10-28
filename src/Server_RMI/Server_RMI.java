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
    //ArrayList <ListaCandidatos> ListasCandidatas;
    ArrayList <Eleicao> ArrayEleicoes; 
    ArrayList <Pessoa> Pessoas;
    Thread t;
    static DatagramSocket  aSocket;
    static Server_RMI server;
    
    /*BUFFERS DE DADOS PARA ARMAZENAR NOS DOIS SERVIDORES*/
    ArrayList <ListaCandidatos> buffercandidatos;
    ArrayList<Pessoa> bufferPessoas;
    ArrayList<Eleicao> bufferEleicao;
    ArrayList<Faculdade> bufferFaculdade;
    ArrayList<Mesa_voto> buffermesa;
    
    public Server_RMI() throws RemoteException{
        super();
        buffercandidatos = new ArrayList();
        bufferPessoas = new ArrayList();
        bufferEleicao = new ArrayList();
        bufferFaculdade = new ArrayList();
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
            FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/listas",true);
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
            FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/Faculdade_dpto",true);
       
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
    public synchronized boolean vote(String list, String eleicao, int id_mesa, String depto, Date data)throws RemoteException{
        Integer qtd=null;
        try {
                String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\"+eleicao+".txt";
                FileWriter out = new FileWriter(path);
                FileReader read = new FileReader(path);
                BufferedReader in = new BufferedReader(read);
                String s="";
                while((s=in.readLine())!=null){
                    String[] a;
                    a=s.split("=");
                    qtd=Integer.parseInt(a[1]);
                    qtd++;
                    a[1]=Integer.toString(qtd);
                    out.write(a[1]);
                    out.close();
                }
              
        } catch (FileNotFoundException ex) {
            ex.getMessage();
        } catch (IOException ex) { 
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return true;
    }
    
    public synchronized void Add_ELectionlocal(String local,Pessoa p){
        try {
            String nome =p.name;
            FileReader read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/pessoas");
            BufferedReader in = new BufferedReader(read);
            FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/pessoas",true);
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
    public synchronized  void criarEleicao(){
         
           int id;
           String departamento;
           String v1[]={"Digite o id da mesa:","digite o departamento:"};
           String saida1[]= new String [v1.length]; 
           String v[]={"Defina o tipo de eleicao","nome da eleicao","Data ex:yyyy-mm-dd"};
           String saida[]= new String [v.length];
           for(int i=0;i<v.length;i++){
               saida[i]=JOptionPane.showInputDialog(v[i]);
           }
            Eleicao  el;
            try {
                    el = new Eleicao(saida[0],saida[1],saida[2]);
                    el.StartEleicao();
                    this.bufferEleicao.add(el);
                    System.out.println(el); 
                  } catch (ParseException ex) {
                      Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
                  }
               
                
            }
            
        
           
     public synchronized  void alterar_eleicao(Eleicao e){//falta terminar
        String nome="";
        e.t.isAlive();
        if(e.t.isAlive()==false){
            nome=JOptionPane.showInputDialog("Digite o nome da eleicao que dejesa alterar:");
            FileReader read;
            boolean exists = (new File("/home/gustavo/NetBeansProjects/ivotas/Ivotas/"+nome)).exists();
            if (exists) {
                try {
               
                    read = new FileReader("/home/gustavo/NetBeansProjects/ivotas/Ivotas/"+nome);
                    BufferedReader in = new BufferedReader(read);
                    String s="";
                    String a[] = null;
                    String vet[]={"Deseja alterar o tipo?","Deseja alterar o titulo?","Deseja alterar a data?" };
                    String o[] = new String[vet.length];
                    while((s=in.readLine())!=null){
                        a=s.split(";");
                    }
                    FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/ivotas/Ivotas/"+nome);
                    for (int i = 0; i <a.length; i++) {
                        o[i]=JOptionPane.showInputDialog(null,vet[i],a[i]);
                    }
                    out.write(o[0]+";");
                    out.write(o[1]+";");
                    out.write(o[2]);
                    out.close();
                    
                File arquivo;
                arquivo = new File("/home/gustavo/NetBeansProjects/ivotas/Ivotas/"+nome);
                arquivo.renameTo(new File("/home/gustavo/NetBeansProjects/ivotas/Ivotas/"+o[1]));
                //this.bufferEleicao.add(a);
                System.out.println(s);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (IOException ex) {
                Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        else{
            JOptionPane.showMessageDialog(null,"O arquivo especificado nao existe !","Atencao",1);
        }
        }
    }
    
    @Override
     public synchronized   void CadastrarPessoa(){
        String tipo_pessoa=""; 
        String name ="";
        Long cartao = null;
        String Password = "";
        String Dpto_facul="";
        String card_valid="";
        String tel="";
        String morada="";
        
        String s[]={"Cadastrar tipo pessoa","Cadastrar nome:","Cadastrar Cartao do cidadao:","Cadastrar Password","Cadastrar DPto","Cadastrar Card_valid MM/yyyy",
            "Cadastrar telefone","Cadastrar Moradia"};
        String o[] = new String[s.length];
        
        for(int i=0;i<o.length;i++){
           o[i]=JOptionPane.showInputDialog(s[i]); 
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
        try {
            FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/ivotas/Ivotas/pessoas",true);
            Pessoa p = new Pessoa(o[0],o[1],Long.parseLong(o[2]),o[3],o[4],o[5],o[6],o[7]);
            String saida="";
            System.out.println(card_valid);
            saida=p.getTipoPessoa()+";"+p.getName()+";"+p.getCartao()+";"+p.getPassword()+";"+
            p.getDpto()+";"+formatter.format(p.card_valid)+";"+p.getTel()+";"+p.getMorada();
            out.write(saida+"\n");
            out.close();
            this.bufferPessoas.add(p);
        } catch (ParseException ex) {
           // Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
                String s="";
                int i=0;
                int j=0;
                out.write("titulo|tipo|descricao|data|departamentos");
                out.newLine();
                out.write(eleicao.titulo+"|"+eleicao.tipo+"|"+eleicao.descricao+"|"+formatter.format(eleicao.data)+"|");
                for(i=0; i<eleicao.dptos.size()-1;i++)
                    out.write(eleicao.dptos.get(i)+",");
                 out.write(eleicao.dptos.get(i));
                 out.newLine();
                
                out.write("NomeLista|Nome1|Nome2|Nome3|NomeN...");
                out.newLine();
                
                for(i=0; i<eleicao.listas.size();i++){
                    out.write(eleicao.listas.get(i).nome+"|");
                    for(j=0;j<eleicao.listas.get(i).Lista.size()-1;j++){
                        out.write(eleicao.listas.get(i).Lista.get(j)+"|");
                    }
                    out.write(eleicao.listas.get(i).Lista.get(j));
                    out.newLine();
                }
                
                out.close();
              
        } catch (FileNotFoundException ex) {
            ex.getMessage();
        } catch (IOException ex) { 
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public Eleicao loadEleicao(String eleicao_titulo){
        ArrayList<String> dptos=null;
        Eleicao eleicao=null;
        try {
                String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Eleicoes\\"+eleicao_titulo+".txt";
                FileReader read = new FileReader(path);
                BufferedReader in = new BufferedReader(read);
                String s="";
                String array[];
                String deps[];
                int i=0;
                int j=0;
                
                in.readLine();  //ignora a primeira linha
                s=in.readLine();    //le eleicao
                array=s.split("\\|");
                System.out.println(Arrays.toString(array));
                deps=array[4].split(",");   // guarda os departamentos
                System.out.println(Arrays.toString(deps));
                dptos=new ArrayList<>(Arrays.asList(deps));
                eleicao=new Eleicao(array[1],array[0],array[2], array[3],dptos);
                
                in.readLine(); //ignora cabecalho da informacao das listas
                while((s=in.readLine())!=null){
                    array=s.split("\\|");
                    System.out.println(Arrays.toString(array));
                    ListaCandidatos aux=new ListaCandidatos(array[0]);
                    for(i=1;i<array.length;i++){
                        aux.Lista.add(array[i]);
                    }
                    eleicao.listas.add(aux);
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
       
            for(int i=0; i<bufferEleicao.size();i++){
                Eleicao eleicao= bufferEleicao.get(i);
                String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\"+eleicao.titulo+".txt";
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
                    bufferEleicao.add(eleicao);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
            }
                
    } 
   
    
    
    @Override
    public ArrayList<ListaCandidatos> get_Listas(String eleicao){
        ArrayList<ListaCandidatos> Listas=new ArrayList();
        for(int i=0; i<ArrayEleicoes.size();i++){
            if(ArrayEleicoes.get(i).titulo.equals(eleicao))
                Listas=ArrayEleicoes.get(i).listas;
        }
        
        return Listas;
    }
     
    @Override
    public  Pessoa autenticate(String campo, String dados){
        for (int i=0; i<this.Pessoas.size();i++){
            switch (campo){
                case "nome":{
                    if(this.Pessoas.get(i).name.equalsIgnoreCase(dados))
                        return Pessoas.get(i);
                    break;
                }
                case "CC":{
                    if(this.Pessoas.get(i).cartao.toString().equalsIgnoreCase(dados))
                        return Pessoas.get(i);
                    break;
                }
                case "password":{
                    if(this.Pessoas.get(i).Password.equalsIgnoreCase(dados))
                        return Pessoas.get(i);
                    break;
                }
                case "morada":{
                    if(this.Pessoas.get(i).morada.equalsIgnoreCase(dados))
                        return Pessoas.get(i);
                    break;
                }
                case "telefone":{
                    if(this.Pessoas.get(i).tel.equalsIgnoreCase(dados))
                        return Pessoas.get(i);
                    break;
                }
            }
        }
        return null;

    }
    
    @Override
    public  boolean unlock_terminal(String cartao, String pass){
        FileReader read;
        try {
            read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Mesa_voto\\src\\Pessoas.txt");
        
                BufferedReader in = new BufferedReader(read);
                String s="";
                String[] a=null;
                s=in.readLine();
                a=s.split(";");
                
                if(!a[1].equals("cartao") || !a[2].equals("password")) return false;

                while((s=in.readLine())!=null){
                    a=s.split(";");
                    if(a[1].equals(cartao) && a[2].equals(pass)){
                        return true;
                    }
                    
                }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    
    @Override
    public ArrayList<String> get_Eleicoes(){
        FileReader read;
        ArrayList<String> cenas=new ArrayList();
        try {
            read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\Eleicao.txt");
        
                BufferedReader in = new BufferedReader(read);
                String s="";
                String[] a=null;
                s=in.readLine();
                a=s.split("\\|");
                
                while((s=in.readLine())!=null){
                    a=s.split("\\|");
                    cenas.add(a[0]);
                    System.out.println(a[0]);
                    
                }
                
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cenas;
    }
    //servers methods
    
    //server runnig;
    public  void CarregaPessoas() throws FileNotFoundException, IOException, ParseException{
        FileReader read = new FileReader("/home/gustavo/NetBeansProjects/ivotas/Ivotas/pessoas");
        BufferedReader in = new BufferedReader(read);
        String s="";
        String[] a=null;
        //s=in.readLine();
        while((s=in.readLine())!=null){
          a=s.split(";");
          Pessoa p = new Pessoa(a[0],a[1],Long.parseLong(a[2]),a[3],a[4],(a[5]),a[6],a[7]);
          this.bufferPessoas.add(p);
          System.out.println(p.toString());
        }
        in.close();
        
    }
    public static void main(String args[])throws RemoteException, MalformedURLException, SocketException, IOException, FileNotFoundException,ParseException {
        
         try{
            
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);
          // System.getProperties().put("java.security.policy","/home/gustavo/NetBeansProjects/ivotas/Ivotas/src/Server_RMI/policy.all");
           //System.setSecurityManager(new RMISecurityManager());
            
            Server_RMI server = new Server_RMI();
            Server_RMI server2 = new Server_RMI();
            
            //Registry r = LocateRegistry.createRegistry(6500);
            Registry r = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
            r.rebind("connection_RMI",server);
            String a="";
            System.out.println("Server RMI ready...");
            server2.CarregaPessoas();
            aSocket = new DatagramSocket(Integer.parseInt(args[0]));
            System.out.println("Socket Datagram à escuta no porto "+args[0]);
            
            server.loadArrayEleicao();
            ArrayList<String> dptos= new ArrayList <String> ();
            dptos.add("DEI");
            dptos.add("DEEC");
            Eleicao eleicao=new Eleicao("geral", "Eleicao_28-10-2017","minahd descricao","28-10-2017",dptos);
            server.bufferEleicao.add(eleicao);
            server.saveArrayEleicao();
            
        }catch(RemoteException re){
            System.out.println(re.getMessage());
        
     
        } catch (SocketException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    
    
}
