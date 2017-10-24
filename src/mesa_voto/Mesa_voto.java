/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesa_voto;


// TCPServer2.java: Multithreaded server
import Server_RMI.Comunication_server;
import java.net.*;
import java.io.*;
import java.util.*;
import java.rmi.*;
import Server_RMI.ListaCandidatos;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Admin
 */
public class Mesa_voto {
    
    private String ID;

    public static void main(String args[]){
        int numero=0;

         try{
            
            //System.getProperties().put("java.security.policy","C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Mesa_voto\\src\\mesa_voto\\policy.all");
            //System.setSecurityManager(new RMISecurityManager());

            
            String serverIP="10.16.0.186";
            String url="rmi://" + serverIP  + ":6500/connection_RMI";
            //String serverIP="localhost";
                         System.out.println(url);

            Comunication_server Rmi_server= (Comunication_server) Naming.lookup(url);
             /*System.out.println(Rmi_server.unlock_terminal("2017199598", "abc123"));*/
            //Mesa_voto c = new Mesa_voto();
           //h.subscribe("stub",(Comunication_client)  c);
          String reply=Rmi_server.Test_connection();
           System.out.println(reply);
           System.out.flush();
           /*Integer i=1;
           System.out.println(Rmi_server.returnList(i).toString());
        
           int serverPort = 6003;
           System.out.println("A Escuta no Porto 6000");
           ServerSocket listenSocket = new ServerSocket(serverPort);
           System.out.println("LISTEN SOCKET = "+listenSocket);
           while(true) {
               Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
               System.out.println("CLIENT_SOCKET (created at accept()) = "+ clientSocket);
               numero ++;
               new Connection(clientSocket, numero, Rmi_server);
           }*/
       }catch(IOException e){
           System.out.println("Listen:" + e.getMessage());
       }catch (NotBoundException ex) {
            Logger.getLogger(Mesa_voto.class.getName()).log(Level.SEVERE, null, ex);
        }
        /* catch (RemoteException ex) {
        Logger.getLogger(Mesa_voto.class.getName()).log(Level.SEVERE, null, ex);
        }*/ /* catch (RemoteException ex) {
            Logger.getLogger(Mesa_voto.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    /*public Comunication_server getRmiServer(){
        return Rmi_server;
    }*/
    
    
}
//= Thread para tratar de cada canal de comunicação com um cliente
class Connection extends Thread {
    PrintWriter outToClient;
    BufferedReader inFromClient = null;
    Socket clientSocket;
    Comunication_server Rmi_server;
    int thread_number;
    
    public Connection (Socket aClientSocket, int numero, Comunication_server Rmi_server) {
        thread_number = numero;
        try{
            clientSocket = aClientSocket;
             // create streams for writing to and reading from the socket
            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToClient = new PrintWriter(clientSocket.getOutputStream());
            this.Rmi_server=Rmi_server;
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
        

        /*try{
        
            //=============================
          // create a thread for reading from the keyboard and writing to the server
          new Thread() {
            public void run() {
              Scanner keyboardScanner = new Scanner(System.in);
              while(!clientSocket.isClosed()) {
                String readKeyboard = keyboardScanner.nextLine();
                outToClient.println(readKeyboard);
                outToClient.flush();
              }
            }
          }.start();

          // the main thread loops reading from the client and answering back
          
            //vote();
          
        } catch (IOException e) {
          if(inFromClient == null)
            System.out.println("\nErro no reader!");
          System.out.println(e.getMessage());
        } finally {
          try { inFromClient.close(); } catch (Exception e) {}
        }*/
    }
    

    public boolean validate_client() throws IOException{
        String resp;
        String[] message=le_consola();
         // procurar a pessoa na base de dados
        //Type|atributo_pessoa;chave1|valor
        if(Rmi_server.unlock_terminal(message[1],message[3])){
            resp="type|validate;" + message[3] + "|OK";
            outToClient.println(resp);
            outToClient.println("Efectue login para poder votar");
            outToClient.flush();
            return true;
        }
        else{
            resp="type|validate;" + message[3] + "|DENIED";
            outToClient.println(resp);
            outToClient.flush();
            return false;
        }
    }
    public boolean login() throws IOException{
         String resp;
         String[] message=le_consola();
        // desbloquear o terminal de voto
        //Type|login;username|valor;password|valor
        if(Rmi_server.autenticate(message[3],message[5])){
            resp="type|login; logged:on; msg: Welcome to Ivotas";
            outToClient.println(resp);
            outToClient.println("[INFO] Terminal de voto desbloqueado");
            outToClient.flush();
            return true;
        }
        else{
            resp="type|validate;" + message[3] + "|DENIED";
            outToClient.println(resp);
            outToClient.flush();
            return false;
        }
    }

    public String[] le_consola() throws IOException{
        String message;
        Boolean bad_input=true;
        String[] data= null;
        //le mensagem para validar cliente na mesa
        while(bad_input){
            message = inFromClient.readLine();

            data=message.split("[|;]");

            int i=0;
            while(i< data.length){
                System.out.println(data[i]);
                i++;
            }
            if(data[0]!="type"){
                outToClient.println("[ERROR]Primeiro campo obrigatorio \"type\" nao encontrado");
                outToClient.println("Evite colocar espacos entre campos!");
                outToClient.println("Exemplo: Type|valor1;chave2|valor2;chave3|valor3");
                outToClient.flush();
                bad_input=true;
            }
            else 
                bad_input=false;
        }
        return data;
    }
    public String select_elections() throws IOException{
        String resp;
         ArrayList<String> Elections;
        Elections=Rmi_server.get_Eleicoes();
        resp="type|item_list;item_count|"+ Elections.size()+';';
        for(int i=0;i<Elections.size();i++)
            outToClient.print("item_"+i+'|'+Elections.get(i)+';');
        outToClient.flush();
        
        //input esperado "type|item_list;option|num"
        String[] message=le_consola();
        if(message[1]=="item_list"){
            String option=message[3];
            if(Integer.parseInt(option)<Elections.size() && Integer.parseInt(option)>=0)
                return option;
        }
        else{
            while(message[1]!="item_list"){
                outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|num\"");
                outToClient.flush();
                message=le_consola();
                String option=message[3];
                if(Integer.parseInt(option)<Elections.size() && Integer.parseInt(option)>=0)
                    return option;
                else{
                    outToClient.println("[Error] O valor de num deve ser 0-"+Elections.size());
                    outToClient.flush();
                    continue;
                }
            }
            
        }
        return null;
        
    }
    
    /*public ArrayList<ListaCandidatos> get_listas(String eleicao) throws IOException{
        
        
        ArrayList<ListaCandidatos> listas=Rmi_server.get_Listas(eleicao);
        String input;
        String output="type|item_list;item_count|"+ listas.size()+';';
        
        for(int i=0;i<listas.size();i++){
            ListaCandidatos lista=listas.get(i);
            for(int j=0;j<lista.listaCandidato;j++){
                output=output.concat("item_"+i+'|'+aux.get(i)+';');
                outToClient.println(output);
                outToClient.flush();      
            }
        }
     
    }
    public int select_lista(String eleicao){
        
        try{
             ArrayList<ListaCandidatos> listas=get_listas(eleicao);

            //input esperado "type|item_list;option|num"
            String[] message=le_consola();
            if(message[1]=="item_list"){
                int option=Integer.parseInt(message[3]);
                if(option<listas.size() && option>=0)
                    return option;
            }
            else{
                while(message[1]!="item_list"){
                    outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|num\"");
                    outToClient.flush();
                    message=le_consola();
                    int option=Integer.parseInt(message[3]);
                    if(option<listas.size() && option>=0)
                        return option;
                    else{
                        outToClient.println("[Error] O valor de num deve ser 0-"+listas.size());
                        outToClient.flush();
                        continue;
                    }
                }

            }
        }catch(IOException E){
            System.out.println("Erro na leitura das listas de candidatos");
        }
        return -1;

    }
    public void vote() throws IOException{
        Boolean validated=false, logon=false;
        
        while(!clientSocket.isClosed()){
            
            if(!validated){
                validated=validate_client(); //procura cliente na base de dados
        
            }
            if(!logon && validated){
                logon=login();              //autentica o cliente na mesa de voto (desbloqueia a mesa)
            }
            if(logon && validated){
              String eleicao=select_elections();
              int lista;
                if(eleicao==null)
                      System.out.println("[ERRO A SELECIONAR A ELEICAO]");
                else{
                    lista=select_lista(eleicao);
                    Rmi_server.vote();
                }
            }
        }
            
    }*/
        
        
        
        
        
           
       

   


        
    
        

    
    
    
    public void item_list(String[] message){
        System.out.println("chamar o metodo que devolve as listas de candidatos");
    }
    public void vote(String[] message){
        System.out.println("chamar o metodo que valida o voto");
    }
    public void look_for(String[] message){
        System.out.println("chamar metodo que procura utilizador na base de dados");
    }
    
    public void respond_item_list(){
        System.out.println("chamar o metodo que devolve as listas de candidatos");
    }
    public void respond_vote(){
        System.out.println("chamar o metodo que valida o voto");
    }
}



