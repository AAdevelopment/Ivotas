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
public class Mesa_voto implements Serializable{
    
    public String departamento;
    public int ID=0;
    public Integer Nr_Voters=0;
    private static final long serialVersionUID = 1L;
    public ArrayList<Pessoa> membros;
    
    public Mesa_voto(String departamento){
        this.departamento=departamento;
        
    }

    public int getID() {
        return ID;
    }
 
    public String toSring(){
        return "ID|"+this.ID+";"+"Nome|"+this.departamento;
    }
    

   public void StartTable(Mesa_voto mesa) throws IOException{
        System.out.println("MESA: "+mesa.toSring()+"Iniciada !");
        int ID_TerminalVote=0;
        int serverPort = 6003;
        System.out.println("A Escuta no Porto "+ serverPort);
        ServerSocket listenSocket = new ServerSocket(serverPort);
        System.out.println("LISTEN SOCKET = "+listenSocket);
        while(true) {
            Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
            System.out.println("CLIENT_SOCKET (created at accept()) = "+ clientSocket);
            ID_TerminalVote ++;
            Terminal_voto term=new Terminal_voto(clientSocket, ID_TerminalVote, mesa);
        }
   } 
   
}


//= Thread para tratar de cada canal de comunicação com um cliente

//CASO DER MERDA, FICA Aí O BACKUP
   /* public static void main(String args[]) throws NotBoundException{
        int ID_TerminalVote=0;

         try{
            
            System.getProperties().put("java.security.policy","C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Mesa_voto\\src\\mesa_voto\\policy.all");
            System.setSecurityManager(new RMISecurityManager());

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
    }*/       



