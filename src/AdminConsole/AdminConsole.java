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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 
   
    
    //CLIENT- SIDE METHODS
    public void nova_mesa_voto(Comunication_server h) throws RemoteException{
        Set<Mesa_voto>mesas=h.getBufferMesas();
      
        System.out.println("\n\nMesas ja Registradas");
        if(mesas.size()==0)
            System.out.println("Nenhuma");
        else{
            for (Mesa_voto m:mesas)
                System.out.println(m.toSring());
        }
        
        String dep;
        Mesa_voto mesa=null;
        boolean verifica=true;
        while(verifica==true){
            dep=JOptionPane.showInputDialog("Digite o departamento da mesa, clique em cancel para sair ");
            if(dep!=null){
                mesa=h.create_mesa(dep);
                if(mesa!=null)
                    mesa.toSring();
                else
                    System.out.println("A mesa nao foi criada com sucesso");
                verifica=false;
            }
        }     
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
    
    public static ArrayList<String> criarLista(Comunication_server h) throws RemoteException{
        String saida="";
        ArrayList<String> array = new ArrayList();
         Pessoa pessoa=null;
            boolean verifica =true;
            while(verifica==true){
                saida=JOptionPane.showInputDialog("digite o CC do candidato, clique em cancel para sair:");
                if(saida==null){
                    verifica=false;   
                    break;
                }
               
                else if((pessoa=h.procurarPessoaCC(Long.parseLong(saida)))!=null){
                     array.add(pessoa.getName());
                  
                }
            }
        return array;
    }
    
    
    public static ArrayList<String> criarFaculdadeDpto(Comunication_server h) throws IOException{
        ArrayList<Faculdade>facul=h.getBufferFaculdade();
        System.out.println("\n\nFaculdades ja Registradas");
        if(facul.size()==0)
            System.out.println("Nenhuma");
        else{
            for(Faculdade f:facul)
                System.out.println(f.toString());
        }
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
    
    public void configurarMesa(Comunication_server h){
        String saida="";
        String option[]=new String [4];
        for(int i=0  ; i < 4 ; i++){
            if(i==0)
                saida="ID da mesa a configurar";
            else if(i!=0)
                saida="CC do elemento "+i;
            option[i]=JOptionPane.showInputDialog(saida);
            if(option[i].isEmpty()){
                i--;
            }
            
        }
        try {
            if(!h.configMesa(option)){
                System.out.println("Nao foi possivel alterar os membros da mesa. Verifique a validade dos novos nomes");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(AdminConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
            String a="", nome="";
            boolean verifica=true;
            
            do{
                opcao=Integer.parseInt(JOptionPane.showInputDialog("1-Configurar membros mesa\n"+"2-criar eleicao\n"+"3-criar lista de candidato\n"+"4-Registrar Pessoa\n"
                      +"5-Criar mesa de voto\n"+"6-Criar dpto\n"+"7-remover departamento\n"+"8-alterar nome departamento"
                        +"\n9-Alterar eleicao"+"\n"+"10- sair do menu"));
                switch(opcao){
                    case 1:
                        c.configurarMesa(h);
                        break;
                    case 2:
                        Eleicao eleicao=null;
                        Mesa_voto mesa= null;
                        String s[]={"Defina o tipo de eleicao","nome da eleicao","Descreva a eleicao","Data_inicio (HH:mm:ss dd/MM/yyyy)","Data_fim (HH:mm:ss dd/MM/yyyy)"};
                        String saida[]= new String [s.length];
                        for(int i=0;i<s.length;i++){
                            saida[i]=JOptionPane.showInputDialog(s[i]);
                        }
                        
                        eleicao=h.criarEleicao(saida);
                        if(eleicao != null){
                            saida[0]=JOptionPane.showInputDialog("Quantas mesas de voto pretende associar?");
                            while(saida[0]==null || Integer.parseInt(saida[0])<=0)
                                 saida[0]=JOptionPane.showInputDialog("Adicione pelo menos uma mesa de voto");
                            for(int i=0; i< Integer.parseInt(saida[0]) ;i++){
                                saida[1]=JOptionPane.showInputDialog("ID da mesa" + i+1+ ":");
                                if((mesa=h.procuraMesa(Integer.parseInt(saida[1])))!=null){
                                    eleicao.mesas.add(mesa);
                                }
                                else{
                                    i--;
                                }
                            }
                        }
                        break; 
                    case 3:
                        String tipo="";
                        tipo=JOptionPane.showInputDialog("Digite o tipo da lista:");
                        String nomeLista="";
                        nomeLista=JOptionPane.showInputDialog("Digite o nome da Lista:");
                        h.CriarLista(criarLista(h),nomeLista,tipo);
                        break;
                        
                    case 4:
                        h.CadastrarPessoa(CadastroPessoa());
                        break;
                    case 5:
                        c.nova_mesa_voto(h);
                        break;
                    case 6:
                        nome=JOptionPane.showInputDialog("Digite o nome da faculdade:");
                        h.CriarFaculdade_Dpto(nome,criarFaculdadeDpto(h));
                        break;
                    case 7:
                        nome=JOptionPane.showInputDialog("Digite o nome da faculdade:");
                        h.removeDepartamento(nome);
                        break;
                    case 8:
                        String aux[] =new String[2];
                        String msg="";
                        for(int i=0; i<2; i++){
                            if(i==0)
                                msg="Digite o nome da faculdade:";
                            else
                                msg="Digite o novo nome da faculdade";
                            aux[i]=JOptionPane.showInputDialog(msg);
                        }
                        h.alterarDepartamento(aux[0],aux[1]);
                        break;
                    case 9:
                        String nome1;
                        nome1=JOptionPane.showInputDialog("Digite o nome da eleicao que dejesa alterar:");
                        String vet[]={"Deseja alterar o tipo?","Deseja alterar o titulo?","Deseja alterar a descricao?","Deseja alterar a data de inicio?","Deseja alterar a data de fim?"};
                        String v[] = new String[vet.length];
                        for (int i = 0; i <vet.length; i++) {
                            v[i]=JOptionPane.showInputDialog(vet[i]);
                        }
                        h.alterar_eleicao(nome1,v);
                        break;
                    case 10:{
                        verifica=false;
                        break;
                    }
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

    
