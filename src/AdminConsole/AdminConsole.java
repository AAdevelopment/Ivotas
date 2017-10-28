/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AdminConsole;

import Server_RMI.Comunication_client;
import Server_RMI.Comunication_server;
import Server_RMI.Eleicao;
import Server_RMI.ListaCandidatos;
import Server_RMI.Faculdade;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JOptionPane;

/**
 *
 * @author gustavo
 */
public class AdminConsole extends UnicastRemoteObject implements Comunication_client  {
    
    public AdminConsole() throws RemoteException {
        super();
    }
    
    @Override
    public void reply_on_client(String a){
        System.out.println("Server: "+a);
    }
    @Override
    public void reply_list_on_client(ListaCandidatos list){
        System.out.println(list.toString());
    }
    @Override
    public void reply_FacultyDptolist_on_client(Faculdade f){
        System.out.println(f.toString());
    }
    @Override
    public void replyElection(Eleicao e){
        System.out.println(e.toString());
    }
    
    public static void main(String args[]) throws RemoteException, NotBoundException, IOException{
        Integer opcao=0;
        
        try{


            //System.getProperties().put("java.security.policy", "/home/gustavo/NetBeansProjects/Ivotas/src/Server_RMI/policy.all");
            //System.setSecurityManager(new RMISecurityManager());
            
           // System.getProperties().put("java.security.policy", "C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\AdminConsole\\policy.all");
           // System.setSecurityManager(new RMISecurityManager());
            Comunication_server h = (Comunication_server) LocateRegistry.getRegistry(6500).lookup("connection_RMI");

            
            AdminConsole c = new AdminConsole();
            h.subscribe("oi", (Comunication_client) c);
           // System.out.println("Client sent subscription to server");
            String reply="";
            String a="";
            boolean verifica=true;
           
            
            
            do{
                opcao=Integer.parseInt(JOptionPane.showInputDialog("1-verificar conexao"+"\n"+"2-criar eleicao"+"\n"+"3-criar lista de candidato\n"+"4-Registrar Pessoa"
                        +"\n"+"7-adicionar mesa de voto a eleicao\n"+"9- sair do menu"));
                switch(opcao){
                    case 1:
                        System.out.println(reply=h.Test_connection());
                        break;
                    case 2:
                        h.criarEleicao();
                        break; 
                    case 3:
                        h.CriarLista();
                        break;
                        
                    case 4:
                        h.CadastrarPessoa();
                        break;
                    case 5:
                        h.CriarFaculdade_Dpto();
                        break;
                    case 6:
                        h.alterar_eleicao();
                        break;
                    case 7:
                        String rep="";
                        rep=JOptionPane.showInputDialog("digite o titulo da eleicao:");
                        h.Add_VoteTable(rep);
                        break;
                    case 9:
                        verifica=false;
                    break;
                }    
            }while(verifica == true);
            
        }catch(RemoteException re){
            re.getMessage();
        } catch (NotBoundException ex) {
            ex.getMessage();
        }
      
    }
}
