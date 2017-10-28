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
public class Mesa_voto {
    
    public Comunication_server Rmi_server;
    public ArrayList<String> departamento;
    public int ID;
    
    public Mesa_voto(int ID){
        this.ID=ID;
        departamento= new ArrayList();
    }
 
    public String toSring(){
        return this.ID+";"+this.departamento.toString();
    }
        
    public void setMdptoMesa(String dpto){
        this.departamento.add(dpto);
    }
    public static void main(String args[]) throws NotBoundException{
        int numero=0;

         try{
            
           /* System.getProperties().put("java.security.policy","C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Mesa_voto\\src\\mesa_voto\\policy.all");
            System.setSecurityManager(new RMISecurityManager());

            String serverIP="192.168.43.53";
            
*/
            String serverIP="localhost";
            String url="rmi://" + serverIP  + ":6500/connection_RMI";
            System.out.println(url);
             Comunication_server Rmi_server= (Comunication_server) Naming.lookup(url);
             String reply=Rmi_server.Test_connection();
            System.out.println(reply);
            System.out.flush();
            Mesa_voto Mesa= new Mesa_voto(1);
            
             /*System.out.println(Rmi_server.unlock_terminal("2017199598", "abc123"));*/
          
          
           
         //TCP server
           //Mesa.departamento="DEI";
           int serverPort = 6003;
           System.out.println("A Escuta no Porto 6000");
           ServerSocket listenSocket = new ServerSocket(serverPort);
           System.out.println("LISTEN SOCKET = "+listenSocket);
           while(true) {
               Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
               System.out.println("CLIENT_SOCKET (created at accept()) = "+ clientSocket);
               Mesa.ID=numero ++;
         //      new Terminal_voto(clientSocket, numero, Mesa.Rmi_server, Mesa.departamento);
           }
       }catch(IOException e){
           System.out.println("Listen:" + e.getMessage());
       }
    }
   
}
//= Thread para tratar de cada canal de comunicação com um cliente
class Terminal_voto extends Thread {
    PrintWriter outToClient;
    BufferedReader inFromClient = null;
    Socket clientSocket;
    Comunication_server Rmi_server;
    String departamento;
    int ID_Mesa;
    
    public Terminal_voto (Socket aClientSocket, int ID, Comunication_server Rmi_server, String departamento) throws IOException {
        ID_Mesa = ID;
        try{
            clientSocket = aClientSocket;
             // create streams for writing to and reading from the socket
            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToClient = new PrintWriter(clientSocket.getOutputStream());
            this.departamento=departamento;
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
        
        vote();
        
       }
    

    public Pessoa validate_client() throws IOException{
        String resp;
        Pessoa user;
        resp="type|msg: Welcome to Ivotas";
        outToClient.println(resp);
        outToClient.flush();
         //type|validate;identificador|valor
        resp="Expected: type|validate;identificador|valor";
        outToClient.println(resp);
        outToClient.flush();
        String[] message=le_consola();
         // procurar a pessoa na base de dados
       
        if((user=Rmi_server.autenticate(message[2],message[3]))!=null){
            resp="type|validate;" + message[3] + "|OK";
            outToClient.println(resp);
            outToClient.println("Efectue login para poder votar");
            outToClient.flush();
            return user;
        }
        else{
            resp="type|validate;" + message[3] + "|NOT FOUND";
            outToClient.println(resp);
            outToClient.flush();
            return null;
        }
    }
    public boolean login(Pessoa Pessoa) throws IOException{
        String resp;
        resp="type|login;username|valor;password|valor";
        outToClient.println(resp);
        outToClient.println("Efectue login para poder votar");
        outToClient.flush();
         String[] message=le_consola();
        // desbloquear o terminal de voto
        //Type|login;username|valor;password|valor
        Resposta resposta=Rmi_server.unlock_terminal(Pessoa,message[3],message[5]);
        if(resposta.valor>0){
            resp="type|login; status|logged:on; msg:"+resposta.mensagem;
            outToClient.println(resp);
            outToClient.flush();
            return true;
        }
        else{
            resp="type|validate;" + message[3] + "|DENIED."+resposta.mensagem;
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
                outToClient.println("Exemplo: type|valor1;chave2|valor2;chave3|valor3" );
                outToClient.flush();
                bad_input=true;
            }
            else 
                bad_input=false;
        }
        return data;
    }
    public Eleicao select_elections() throws IOException{
        String resp;
        ArrayList<Eleicao> Elections;
        Elections=Rmi_server.get_Eleicoes();
        resp="type|item_list;item_count|"+ Elections.size()+';';
        outToClient.println(resp);
        for(int i=0;i<Elections.size();i++)
            outToClient.println("item_"+i+'|'+Elections.get(i).toString()+';');
        outToClient.flush();
        
        resp="Expected: type|item_list;option|nome";
        outToClient.println(resp);
        outToClient.flush();

        //input esperado "type|item_list;option|nome"
        String[] message=le_consola();
        if("item_list".equalsIgnoreCase(message[1]) && "option".equalsIgnoreCase(message[2])){
            Eleicao eleicao=Rmi_server.getEleicao(message[3]);
            return eleicao;
        }
        
        do{
            outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|nome\" escolhendo um nome da lista de eleicoes apresentada");
            outToClient.flush();
            message=le_consola();
        } while(!"item_list".equals(message[1]) && "option".equalsIgnoreCase(message[2]));
        Eleicao eleicao=Rmi_server.getEleicao(message[3]);
        
        return eleicao;
        
    }
    
    public void show_listas(Eleicao eleicao) throws IOException{
        
        
        ArrayList<ListaCandidatos> listas=Rmi_server.get_Listas(eleicao);
        
        String output="type|item_list;item_count|"+ listas.size()+';';
        outToClient.println(output);
        outToClient.flush();
        for(int i=0;i<listas.size();i++){
            ListaCandidatos lista=listas.get(i);
            outToClient.print("list_name|"+lista.nome+";");
            outToClient.flush();
            ArrayList<String> aux=lista.Lista;
            for(int j=0 ; j < aux.size() ; j++){
                output=output.concat("item_" + i +'|' + aux.get(i) + ';');
                outToClient.println(output);
                outToClient.flush();      
            }
            outToClient.println();
        }
     
    }
   /* public void select_lista(Eleicao eleicao) throws IOException{
        
        try{
             show_listas(eleicao);

          //  input esperado "type|item_list;option|num"
            String[] message=le_consola();
            if("item_list".equals(message[1])){
                if(Rmi_server.vote(message[3], eleicao, this.ID_Mesa, this.departamento, new Date())){
                        outToClient.println("type|login; status|logged:off; msg: Vote sucessfull");
                        outToClient.flush();
                    }
                    else{
                        outToClient.println("[Error] O valor de \"nome\" nao e conhecido");
                        outToClient.flush();
                        while(!"item_list".equals(message[1])){
                            outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|nome\"");
                            outToClient.flush();
                            message=le_consola();
                            if(Rmi_server.vote(message[3], eleicao, this.ID_Mesa, this.departamento, new Date())){
                                outToClient.println("type|login; status|logged:off; msg: Vote sucessfull");
                                outToClient.flush();
                            }
                            else{
                                outToClient.println("[Error] O valor de \"nome\" nao e conhecido");
                                outToClient.flush();
                            }
                        }
                    }
            }
            else{
                while(!"item_list".equals(message[1])){
                    outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|nome\"");
                    outToClient.flush();
                    message=le_consola();
                    if(Rmi_server.vote(message[3], eleicao, this.ID_Mesa, this.departamento, new Date())){
                        outToClient.println("type|login; status|logged:off; msg: Vote sucessfull");
                        outToClient.flush();
                    }
                    else{
                        outToClient.println("[Error] O valor de \"nome\" nao e conhecido");
                        outToClient.flush();
                        while(!"item_list".equals(message[1])){
                            outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|nome\"");
                            outToClient.flush();
                            message=le_consola();
                            if(Rmi_server.vote(message[3], eleicao, this.ID_Mesa, this.departamento, new Date())){
                                outToClient.println("type|login; status|logged:off; msg: Vote sucessfull");
                                outToClient.flush();
                            }
                            else{
                                outToClient.println("[Error] O valor de \"nome\" nao e conhecido");
                                outToClient.flush();
                            }
                        }
                    }
                  }
            }
            
        }catch(IOException E){
            System.out.println("Erro na leitura das listas de candidatos");
        }

    }*/
    public void vote() throws IOException{
        Boolean logon=false;
        Pessoa user=null;
        while(!clientSocket.isClosed()){
            
            if(user==null){
                user=validate_client(); //procura cliente na base de dados
            }
            if(!logon && user==null){
                logon=login(user);              //autentica o cliente na mesa de voto (desbloqueia a mesa)
            }
            if(logon && user!=null){
                Eleicao eleicao=select_elections();  //escolhe  eleicao pretendida 
              //  select_lista(eleicao);              //vota na lista pretendida
                }
            }
        }
        
    }
       



