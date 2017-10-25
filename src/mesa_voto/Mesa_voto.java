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
    public static Comunication_server Rmi_server;

    public static void main(String args[]){
        int numero=0;

         try{
            
            //System.getProperties().put("java.security.policy","C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Mesa_voto\\src\\mesa_voto\\policy.all");
            //System.setSecurityManager(new RMISecurityManager());

            
            String serverIP="localhost";
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
         
           int serverPort = 6003;
           System.out.println("A Escuta no Porto 6000");
           ServerSocket listenSocket = new ServerSocket(serverPort);
           System.out.println("LISTEN SOCKET = "+listenSocket);
           while(true) {
               Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
               System.out.println("CLIENT_SOCKET (created at accept()) = "+ clientSocket);
               numero ++;
               new Connection(clientSocket, numero, Rmi_server);
           }
       }catch(IOException e){
           System.out.println("Listen:" + e.getMessage());
       }catch (NotBoundException ex) {
            Logger.getLogger(Mesa_voto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
//= Thread para tratar de cada canal de comunicação com um cliente
class Connection extends Thread {
    PrintWriter outToClient;
    BufferedReader inFromClient = null;
    Socket clientSocket;
    Comunication_server Rmi_server;
    int thread_number;
    
    public Connection (Socket aClientSocket, int numero, Comunication_server Rmi_server) throws IOException {
        thread_number = numero;
        try{
            clientSocket = aClientSocket;
             // create streams for writing to and reading from the socket
            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToClient = new PrintWriter(clientSocket.getOutputStream());
            this.Rmi_server=Rmi_server;
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
        

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
        
        System.out.println("A eleicao:"+ this.select_elections());
        
        try { inFromClient.close(); } catch (Exception e) {}
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
            resp="type|login; status|logged:on; msg: Welcome to Ivotas";
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
            if(!"type".equals(data[0])){
                outToClient.println("[ERROR]Primeiro campo obrigatorio \"type\" nao encontrado");
                outToClient.println("Evite colocar espacos entre campos!");
                outToClient.println("Exemplo: Type|valor1;chave2|valor2;chave3|valor3" );
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
        outToClient.print(resp);
        for(int i=0;i<Elections.size();i++)
            outToClient.print("item_"+i+'|'+Elections.get(i)+';');
        outToClient.flush();
        
        //input esperado "type|item_list;option|nome"
        String[] message=le_consola();
        String option=null;
        if("item_list".equals(message[1]) && Elections.contains(message[3])){
            option=message[3];
        }
        else{
            do{
                outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|nome\" escolhendo um nome da lista apresentada");
                outToClient.flush();
                message=le_consola();
            } while(!"item_list".equals(message[1]) && Elections.contains(message[3]) );
            option=message[3];
        }
        return option;
        
    }
    
    public void show_listas(String eleicao) throws IOException{
        
        
        ArrayList<ListaCandidatos> listas=Rmi_server.get_Listas(eleicao);
        String input;
        String output="type|item_list;item_count|"+ listas.size()+';';
        
        for(int i=0;i<listas.size();i++){
            ListaCandidatos lista=listas.get(i);
            ArrayList<String> aux=lista.Lista;
            for(int j=0 ; j < aux.size() ; j++){
                output=output.concat("item_" + i +'|' + aux.get(i) + ';');
                outToClient.println(output);
                outToClient.flush();      
            }
        }
     
    }
    public void select_lista(String eleicao){
        
        try{
             show_listas(eleicao);

            //input esperado "type|item_list;option|num"
            String[] message=le_consola();
            if("item_list".equals(message[1])){
                String option=message[3];
            }
            else{
                while(!"item_list".equals(message[1])){
                    outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|nome\"");
                    outToClient.flush();
                    message=le_consola();
                    int option=Integer.parseInt(message[3]);
                    if(Rmi_server.vote(message[3])){
                        outToClient.println("type|login; status|logged:off; msg: Vote sucessfull");
                        outToClient.flush();
                    }
                    else{
                        outToClient.println("[Error] O valor de \"nome\" nao e conhecido");
                        outToClient.flush();
                    }
                }
            }
        }catch(IOException E){
            System.out.println("Erro na leitura das listas de candidatos");
        }

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
                String eleicao=select_elections();  //escolhe  eleicao pretendida 
                select_lista(eleicao);              //vota na lista pretendida
                }
            }
        }
            
    }
       



