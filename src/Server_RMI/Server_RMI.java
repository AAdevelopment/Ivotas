/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;



import Server_RMI.Comunication_server;
import java.io.BufferedReader;
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
    String vetCand[]={"paulo","pedro","eduardo","tiago","felipe","joao"}; 
    String vetCand2[]={"pedrino","Janio"};

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
    public ArrayList<String> returnList(Integer type)throws RemoteException{
       String lista="";
       ArrayList<String> ListasCandidato1 = new ArrayList();
       
      /*  if (type == 1){
           ListasCandidatos listc = new ListasCandidatos(vetCand);
           ListasCandidato1 = listc.Getlista(vetCand);
        
        }*/
        return ListasCandidato1;
    }
    
    @Override
    public void CriarLista(){
        String nome="";
        nome=JOptionPane.showInputDialog("Digite o nome da lista");
        ListaCandidatos l = new ListaCandidatos(nome);
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
    public void CriarFaculdade_Dpto() throws RemoteException{
        String nome="";
        nome=JOptionPane.showInputDialog("Digite o nome da faculdade:");
        Faculdade f = new Faculdade(nome);
        String saida="";
        boolean verifica =true;
        while(verifica==true){
            saida=JOptionPane.showInputDialog("digite o nome do Departamento, clique em cancel para sair:");
            if(saida==null){
                verifica =false;   
                break;
            }
            else{
                f.criarDPTO(nome);    
            }
        }
    }
    /*
    *
    * A FUNCAO VOTE TEM DE RETORNAR TRUE SE O VOTO FOI BEM SUCEDIDO 
    *
    */
    
    
    @Override
    public boolean vote(String list)throws RemoteException{
      
        Integer qtd=null;
        try {
                FileWriter out = new FileWriter("/home/gustavo/NetBeansProjects/Ivotas/listas");
                FileReader read = new FileReader("/home/gustavo/NetBeansProjects/Ivotas/listas");
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
    
    public synchronized HashMap listaEleicao(String nrtitulo){
        HashMap h = new HashMap();
        h.put(1, vetCand);
        h.put(2,vetCand);
        return h;
    }
    
    /*
    *
    *Author Andre Santos
    *
    */
    
    @Override
    public ArrayList<ListaCandidatos> get_Listas(String eleicao){
        ArrayList<ListaCandidatos> listas=new ArrayList();
        /*
        *
        *
        *    FALTA LER LISTAS DOS FICHEIROS
        *
        */
        
        return listas;
    }
    
   @Override
    public void criarEleicao(){
       try {
           Eleicao  el = new Eleicao("Nucleo estudantes","DEI","2017-09-21");
           System.out.println(el);
        } catch (ParseException ex) {
          ex.getMessage();
        }
    }
    
    @Override
     public  void CadastrarPessoa(){
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
            read = new FileReader("C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Mesa_voto\\src\\AdminConsole\\Pessoas.txt");
        
                BufferedReader in = new BufferedReader(read);
                String s="";
                String[] a=null;
                s=in.readLine();
                a=s.split(";");
                
                
                while((s=in.readLine())!=null){
                    a=s.split(";");
                    
                    
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
            /*System.getProperties().put("java.security.policy", "/home/gustavo/NetBeansProjects/Ivotas/src/Server_RMI/policy.all");
            System.setSecurityManager(new RMISecurityManager());*/
            
            Server_RMI server = new Server_RMI();
            Registry r = LocateRegistry.createRegistry(6500);
            r.rebind("connection_RMI",server);
            String a="";
            System.out.println("Server RMI ready...");
            while(true){
               a=reader.readLine();
               c.reply_on_client(a);
            }    
        }catch(RemoteException re){
            System.out.println(re.getMessage());
        
        } catch (IOException ex) {
            Logger.getLogger(Server_RMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    
    
}