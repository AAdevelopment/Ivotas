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
import Server_RMI.Pessoa;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JOptionPane;
import mesa_voto.Mesa_voto;

/**
 *
 * @author gustavo
 */
public class AdminConsole extends UnicastRemoteObject implements Comunication_client,Serializable  {
    
    public AdminConsole() throws RemoteException {
        super();
    }
  
    // INTERFACE SERVER-SIDE METHODS
    
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
        ArrayList<ListaCandidatos>list=e.getListas_candidatas();
        for (int i = 0; i <list.size(); i++) {
            System.out.println(list.get(i));
        }
        
    }
    
    public void replyNrVoters(String state)throws RemoteException{
        System.out.println("Voters:"+state);
    }
     
    public void replyPeople(Pessoa p)throws RemoteException{
        System.out.println(p.toString());
    }
      
    public ArrayList<ListaCandidatos> Add_lists_toElection(ArrayList<ListaCandidatos> lista,Eleicao el)throws RemoteException{
        ArrayList <ListaCandidatos> list = new ArrayList();
        System.out.println("Listas de candidatos disponiveis:");
        for (int i = 0; i < lista.size(); i++) {
            System.out.println(lista.get(i));
        }
        boolean verifica=true;
        while(verifica==true){ 
            String nome=JOptionPane.showInputDialog("digite o nome da lista desejada, clique em cancelar para sair");
            if(nome==null){
              break;
            }
            else{
                for (int i = 0; i <lista.size(); i++) {
                    if(lista.get(i).nome.equalsIgnoreCase(nome)){
                        if(lista.get(i).tipo.equals(el.getTipo())){
                            list.add(lista.get(i));
                        }
                        else{
                            System.out.println("Erro tipo da eleicao diferente do tipo da lista  !!!");
                        }
                    }
                    else{
                        System.out.println("nome:"+lista.get(i).nome);
                        System.out.println("Erro nome  nao condizentes com a lista !!!");
                    }
                }
            }
        }
        return list;
    }
 
    public Set<String> Add_table_to_election(Set<Mesa_voto> mesas)throws RemoteException{
        Set <String> tables = new LinkedHashSet<String>();
        String dep;
        boolean verifica=true;
        Mesa_voto mesa;
        System.out.println("Listas de mesas Ja Criadas:");
        for (Mesa_voto m:mesas) {
            System.out.println(m.toSring());
        }
        while(verifica==true){
            dep=JOptionPane.showInputDialog("Digite o departamento da mesa, clique em cancel para sair ");
            if(dep==null){
                break;
            }
            else{
                tables.add(dep);
            }
        }
        for(String m:tables)
            System.out.println(m);
        
        return tables;
    }
    
    //CLIENT- SIDE METHODS
    public static ArrayList<Mesa_voto> Add_VoteTable() throws RemoteException{
      ArrayList<Mesa_voto> table = new  ArrayList();
      String dep;
      ArrayList <String> dpto= new ArrayList();
      Mesa_voto mesa=null;
      boolean verifica=true;
      while(verifica==true){
          dep=JOptionPane.showInputDialog("Digite o departamento da mesa, clique em cancel para sair ");
          if(dep==null){
              break;
          }
          else{
              mesa = new Mesa_voto(dep);
              table.add(mesa);
              ////wainting....  
            }
      }
       
      //JOptionPane.showInputDialog("Digite o Titulo da eleicao:"); 
      return table;
        
    }
      
    public static String [] CadastroPessoa(){
        String s[]={"Cadastrar tipo pessoa","Cadastrar nome:","Cadastrar Cartao do cidadao:","Cadastrar Password","Cadastrar DPto","Cadastrar Card_valid dd-mm-yyyy",
            "Cadastrar telefone","Cadastrar Moradia"};
        String o[] = new String[s.length];
        
        for(int i=0;i<o.length;i++){
           o[i]=JOptionPane.showInputDialog(s[i]); 
        }
        return o;
    }
    
    public static ArrayList<String> criarLista(){
        String saida="";
        ArrayList<String> array = new ArrayList();
         
            boolean verifica =true;
            while(verifica==true){
                saida=JOptionPane.showInputDialog("digite o nome do candidato, clique em cancel para sair:");
                if(saida==null){
                    verifica=false;   
                    break;
                }
                else{
                     array.add(saida);
                  
                }
            }
        return array;
    }
    
    
    public static ArrayList<String> criarFaculdadeDpto() throws IOException{
        String saida="";
        ArrayList<String> array = new ArrayList();
        boolean verifica =true;
        while(verifica==true){
            saida=JOptionPane.showInputDialog("digite o nome do Departamento, clique em cancel para sair:");
            if(saida==null){
                verifica =false;   
                break;
            }
            else{
                array.add(saida);
            }
        }
        return array;
    }
    
    
    
    public static void main(String args[]) throws RemoteException, NotBoundException, IOException{
        Integer opcao=0;
        
        try{
            
           // System.getProperties().put("java.security.policy", "C:\\Users\\Admin\\Desktop\\3_ano_1_sem\\SD\\Projecto1\\Ivotas\\src\\AdminConsole\\policy.all");
           // System.setSecurityManager(new RMISecurityManager());
            Comunication_server h = (Comunication_server) LocateRegistry.getRegistry(6500).lookup("connection_RMI");

            
            AdminConsole c = new AdminConsole();
            h.subscribe("new Cliente Conection", (Comunication_client) c);
           // System.out.println("Client sent subscription to server");
            String reply="";
            String a="";
            boolean verifica=true;
           
            
            
            do{
                opcao=Integer.parseInt(JOptionPane.showInputDialog("1-verificar conexao"+"\n"+"2-criar eleicao"+"\n"+"3-criar lista de candidato\n"+"4-Registrar Pessoa"
                      +"\n5-Criar faculdade/dpto\n"+"6-Alterar eleicao"+"\n"+"9- sair do menu"));
                switch(opcao){
                    case 1:
                        System.out.println(reply=h.Test_connection());
                        break;
                    case 2:
                        
                        String s[]={"Defina o tipo de eleicao","nome da eleicao","Descreva a eleicao","Data_inicio (HH:mm:ss dd/MM/yyyy)","Data_fim (HH:mm:ss dd/MM/yyyy)"};
                        String saida[]= new String [s.length];
                        for(int i=0;i<s.length;i++){
                            saida[i]=JOptionPane.showInputDialog(s[i]);
                        }
                        
                        h.criarEleicao(saida);
                        break; 
                    case 3:
                        String tipo="";
                        tipo=JOptionPane.showInputDialog("Digite o tipo da lista:");
                        String nomeLista="";
                        nomeLista=JOptionPane.showInputDialog("Digite o nome da Lista:");
                        h.CriarLista(criarLista(),nomeLista,tipo);
                        break;
                        
                    case 4:
                        h.CadastrarPessoa(CadastroPessoa());
                        break;
                    case 5:
                        String nome="";
                        nome=JOptionPane.showInputDialog("Digite o nome da faculdade:");
                        h.CriarFaculdade_Dpto(nome,criarFaculdadeDpto());
                        break;
                    case 6:
                        String nome1;
                        nome1=JOptionPane.showInputDialog("Digite o nome da eleicao que dejesa alterar:");
                        String vet[]={"Deseja alterar o tipo?","Deseja alterar o titulo?","Deseja alterar a descricao?","Deseja alterar a data de inicio?","Deseja alterar a data de fim?"};
                        String v[] = new String[vet.length];
                        for (int i = 0; i <vet.length; i++) {
                            v[i]=JOptionPane.showInputDialog(vet[i]);
                        }
                        h.alterar_eleicao(nome1,v);
                        break;
                    case 9:
                        verifica=false;
                       
                        break;
                }    
            }while(verifica == true);
             System.exit(0);
        }catch(RemoteException re){
            System.out.println(re.getMessage()); 
        } catch (NotBoundException ex) {
            ex.getMessage();
        }
      
    }
    
    }

    
