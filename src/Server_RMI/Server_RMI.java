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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gustavo
 */

public class Server_RMI  extends UnicastRemoteObject implements Comunication_server {
    static Comunication_client c;
    ArrayList <Eleicao> ArrayEleicoes;
    
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
            FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/pessoas",true);
            
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    *
    *Author Andre Santos
    *
    */
    
    //salva a lista de candidatos de uma eleicao
    public void saveEleicao (Eleicao eleicao){
        
        try {
                String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\"+eleicao.titulo+".txt";
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
                String path="C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\"+eleicao_titulo+".txt";
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
    public synchronized  void criarEleicao(){
       try {
           String v[]={"Defina o tipo de eleicao","nome da eleicao","Data ex:yyyy-mm-dd"};
           String saida[]= new String [v.length];
           for(int i=0;i<v.length;i++){
               saida[i]=JOptionPane.showInputDialog(v[i]);
            }
           Eleicao  el = new Eleicao(saida[0],saida[1],saida[2]);
           el.StartEleicao();
           System.out.println(el);
        } catch (ParseException ex) {
          ex.getMessage();
        }
    }
    
     public synchronized  void alterar_eleicao(){//falta terminar
        String nome="";
        
        nome=JOptionPane.showInputDialog("Digite o nome da eleicao que dejesa alterar:");
        FileReader read;
        boolean exists = (new File("/home/gustavo/NetBeansProjects/Ivotas/"+nome)).exists();
        if (exists) {
            try {
                read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/"+nome);
                BufferedReader in = new BufferedReader(read);
                String s="";
                String a[] = null;
                while((s=in.readLine())!=null){
                    a=s.split(";");
                }
                JOptionPane.showInputDialog(null,"Deseja alterar o tipo de eleicao ?",a[0]);
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
    
    @Override
     public synchronized   void CadastrarPessoa(){
        String tipo_pessoa=""; 
        String name ="";
        Long cartao = null;
        String Password = "";
        String Dpto_facul="";
        Date card_valid=null;
        String tel="";
        String morada="";
        
        String s[]={"Cadastrar tipo pessoa","Cadastrar nome:","Cadastrar Cartao do cidadao:","Cadastrar Password","Cadastrar DPto","Cadastrar Card_valid MM/yyyy",
            "Cadastrar telefone","Cadastrar Moradia"};
        String o[] = new String[s.length];
        
        for(int i=0;i<o.length;i++){
           o[i]=JOptionPane.showInputDialog(s[i]); 
        }
        
        DateFormat formatter = new SimpleDateFormat("MM/yyyy");
        try {
            FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/pessoas",true);
            
            Pessoa p = new Pessoa(tipo_pessoa=o[0],name=o[1],cartao=Long.parseLong(o[2]),Password=o[3],Dpto_facul=o[4],
            card_valid=(java.util.Date)formatter.parse(o[5]),tel=o[6],morada=o[7]);
            String saida="";
            
            saida=p.getTipoPessoa()+";"+p.getName()+";"+p.getPassword()+";"+p.getDpto()+";"+
            p.getCard_valid()+";"+p.getTel()+";"+p.getMorada();
            out.write(saida+"\n");
            out.close();            
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
    @Override
    public  boolean autenticate(String campo, String dados){
        FileReader read;
        try {
            read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Mesa_voto\\src\\Pessoas.txt");
        
                BufferedReader in = new BufferedReader(read);
                String s="";
                String[] a=null;
                int index_campo=-1;
                s=in.readLine();
                a=s.split(";");
                for(int i=0;i< a.length ;i++){
                    if(campo.equals(a[i]))
                        index_campo=i;
                }
                if(index_campo==-1) return false; //caso em que nao encontra o campo de informacao
                
                while((s=in.readLine())!=null){
                    a=s.split(";");
                    for(int i=0;i< a.length ;i++){
                        if(i==index_campo){
                            if(a[i].equals(dados)){
                                return true;
                            }
                                
                        }
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
    
    //server runnig;
    public static void main(String args[])throws RemoteException, MalformedURLException {
        
         try{
           
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);
          // System.getProperties().put("java.security.policy","/home/gustavo/NetBeansProjects/ivotas/Ivotas/src/Server_RMI/policy.all");
           //System.setSecurityManager(new RMISecurityManager());
            
            Server_RMI server = new Server_RMI();
            Registry r = LocateRegistry.createRegistry(6500);
            //Registry r = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
            r.rebind("connection_RMI",server);
            String a="";
            System.out.println("Server RMI ready...");
            /*while(true){
               a=reader.readLine();
               c.reply_on_client(a);
            } */   
            Eleicao ivotas;
            ivotas=server.loadEleicao("Eleicao");
            ivotas.setDescricao("alterei a descricao");
            server.saveEleicao(ivotas);
             System.out.println(ivotas.data);
        }catch(RemoteException re){
            System.out.println(re.getMessage());
        
     
        }
    } 

    
    
}
