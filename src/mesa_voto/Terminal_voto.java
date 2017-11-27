/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mesa_voto;

import Server_RMI.Comunication_server;
import Server_RMI.Eleicao;
import Server_RMI.ListaCandidatos;
import Server_RMI.Pessoa;
import Server_RMI.Resposta;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
//= Thread para tratar de cada canal de comunicação com um cliente
class Terminal_voto extends Thread {
    PrintWriter outToClient;
    private static final long serialVersionUID = 1L;
    BufferedReader inFromClient = null;
    Socket clientSocket;
    public Comunication_server Rmi_server;
    Mesa_voto mesa;
    
    
    public Terminal_voto (Socket aClientSocket, int ID, Mesa_voto mesa) throws IOException {
        try{
            clientSocket = aClientSocket;
             // create streams for writing to and reading from the socket
            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToClient = new PrintWriter(clientSocket.getOutputStream());
            String serverIP="localhost";
            String url="rmi://" + serverIP  + ":6500/connection_RMI";
            Comunication_server Rmi= (Comunication_server) Naming.lookup(url);
            this.Rmi_server=Rmi;
            this.mesa=mesa;
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());} catch (NotBoundException ex) {
            Logger.getLogger(Terminal_voto.class.getName()).log(Level.SEVERE, null, ex);
        }

        vote();
        clientSocket.close();
    }
    

    public Pessoa validate_client() throws IOException{
        String resp;
        Pessoa user;
        resp="type|msg: Welcome to Ivotas";
        outToClient.println(resp);
        outToClient.flush();
         //type|validate;identificador|valor
        resp="Expected: type|validate;tipo_identificador|valor";
        outToClient.println(resp);
        outToClient.flush();
      
        
        String[] message=le_consola();
         // procurar a pessoa na base de dados
        System.out.println(Arrays.toString(message));
        if((user=Rmi_server.autenticate(message[2],message[3]))!=null){
            resp="type|validate;" + user.getName() + "|OK";
            outToClient.println(resp);
            outToClient.println("Efectue login para poder votar");
            outToClient.flush();
            return user;
        }
        else{
            resp="type|validate;" + message[3] + "|NOT FOUND";
            outToClient.println(resp);
            outToClient.println();
            outToClient.flush();
            return null;
        }
    }
    public boolean login(Pessoa Pessoa) throws IOException{
        String resp;
        resp="Expected: \"type|login;username|valor;password|valor\"";
        outToClient.println(resp);
        outToClient.flush();
        String[] message=le_consola();
        // desbloquear o terminal de voto
        //Type|login;username|valor;password|valor
        Resposta resposta=Rmi_server.unlock_terminal(Pessoa,message[3],message[5]);
        if(resposta.valor > 0){
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

            //System.out.println(Arrays.toString(data));
            
            if(!"type".equalsIgnoreCase(data[0])){
                outToClient.println("[ERROR]Primeiro campo obrigatorio \"type\" nao encontrado");
                outToClient.println("Evite colocar espacos entre campos!");
                outToClient.println("Exemplo: type|valor1;chave2|valor2;chave3|valor3" );
                outToClient.flush();
                bad_input=true;
            }
            else 
                bad_input=false;
        }
        outToClient.println();
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
        outToClient.println();
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
            ArrayList<String> aux=lista.candidatos;
            for(int j=0 ; j < aux.size() ; j++){
                output="item_" + j +'|' + aux.get(j) + ';';
                outToClient.print(output);
                outToClient.flush();      
            }
            outToClient.println();
        }
        outToClient.println();
     
    }
   public boolean select_lista(Eleicao eleicao, Pessoa pessoa) throws IOException{
        
        try{
            show_listas(eleicao);
            String resp="Expected: type|item_list;option|list_name";
            outToClient.println(resp);
            outToClient.flush();
          //  input esperado "type|item_list;option|nome"
            String[] message=le_consola();
            if("item_list".equalsIgnoreCase(message[1]) && "option".equalsIgnoreCase(message[2])){
                System.out.println("ENTROU NO VOTO");
                return Rmi_server.vote(message[3], eleicao,pessoa, this.mesa, new Date());
               
            }
            else{
               
                while(!"item_list".equalsIgnoreCase(message[1]) && !"option".equalsIgnoreCase(message[2])){
                    outToClient.println("[Error] Digite a sua opcao na forma: \"type|item_list;option|nome\"");
                    outToClient.flush();
                    message=le_consola();
                    if("item_list".equalsIgnoreCase(message[1]) && "option".equalsIgnoreCase(message[2])){
                        Rmi_server.vote(message[3], eleicao,pessoa, this.mesa, new Date());
                        outToClient.println("type|login; status|logged:off; msg: Vote sucessfull");
                        outToClient.flush();
                        return true;
                    }
                }
            }
        }catch(IOException E){
             E.printStackTrace();
             outToClient.println("Ocorreu um erro no processamento do voto. Repita o processo!");
             outToClient.flush();
        }
        return false;
    }
    public void vote() throws IOException{
        Boolean logon=false;
        Pessoa user=null;
        boolean votou= false;
        while(!clientSocket.isClosed() && !votou){
            
            if(user==null){
                user=validate_client(); //procura cliente na base de dados
            }
            if(!logon && user!=null){
                logon=login(user);              //autentica o cliente na mesa de voto (desbloqueia a mesa)
            }
            if(logon && user!=null ){
                Eleicao eleicao=select_elections();  //escolhe  eleicao pretendida 
                votou=select_lista(eleicao, user);   //vota na lista pretendida
              
                if(votou){
                    outToClient.println("type|login; status|logged:off; msg: Vote sucessfull");
                    outToClient.flush();
                    Rmi_server.Count_voters(eleicao,mesa);
                }
                else{
                    outToClient.println("type|login; status|logged:off; msg: An error has occorred. Repeat the process.");
                    outToClient.flush();
                }

            }
            
        }
    }
        
}
