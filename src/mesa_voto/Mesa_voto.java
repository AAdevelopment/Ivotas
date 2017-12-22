/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesa_voto;


// TCPServer2.java: Multithreaded server
import Server_RMI.Comunication_server;
import Server_RMI.Eleicao;
import java.net.*;
import java.io.*;
import java.util.*;
import java.rmi.*;
import Server_RMI.ListaCandidatos;
import Server_RMI.Pessoa;
import Server_RMI.Resposta;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Admin
 */
public class Mesa_voto implements Serializable, Runnable{
    public String departamento;
    public int ID;
    public Integer Nr_Voters=0;
    private static int serverPort=6140;
    private static final long serialVersionUID = 1L;
    
    public Mesa_voto(String departamento){
        this.departamento=departamento;
    }

    public int getID() {
        return ID;
    }
 
    public String toSring(){
        return "ID|"+this.ID+";"+"Nome|"+this.departamento;
    }
    public synchronized ServerSocket get_Port() throws IOException{
        ServerSocket listenSocket;
         System.out.println("MESA: "+this.toSring()+" iniciada...");
         System.out.println("A Escuta no Porto "+ serverPort);
         listenSocket = new ServerSocket(serverPort);
         System.out.println("LISTEN SOCKET = "+listenSocket);
         Mesa_voto.serverPort=Mesa_voto.serverPort+1;
         notify();
         return listenSocket;
    }

    @Override
   public void run(){
        try {
            ServerSocket listenSocket=get_Port();
            while(true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept()) = "+ clientSocket);
                new Terminal_voto(clientSocket, this);
            }
        } catch (IOException ex) {
            Logger.getLogger(Mesa_voto.class.getName()).log(Level.SEVERE, null, ex);
        }
   } 
    
   
}


//= Thread para tratar de cada canal de comunicação com um cliente
/*
//CASO DER MERDA, FICA Aí O BACKUP
   public static void main(String args[]) throws NotBoundException{
        int ID_TerminalVote=0;

         try{
            
            //System.getProperties().put("java.security.policy","C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Mesa_voto\\src\\mesa_voto\\policy.all");
            //System.setSecurityManager(new SecurityManager());

            String serverIP="192.168.43.53";
           
           Mesa_voto Mesa= new Mesa_voto("DEI");
           
           int serverPort = 6003;
           System.out.println("A Escuta no Porto "+ serverPort);
           ServerSocket listenSocket = new ServerSocket(serverPort);
           System.out.println("LISTEN SOCKET = "+listenSocket);
           while(true) {
               Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
               System.out.println("CLIENT_SOCKET (created at accept()) = "+ clientSocket);
               ID_TerminalVote ++;
               new Terminal_voto(clientSocket, ID_TerminalVote, Mesa);
           }
       }catch(IOException e){
           System.out.println("Listen:" + e.getMessage());
       }
    }   
}*/



