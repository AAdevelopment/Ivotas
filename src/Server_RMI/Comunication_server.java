/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;

/**
 *
 * @author gustavo
 */

import java.rmi.*;
import java.util.ArrayList;
import java.util.Date;



public interface Comunication_server extends Remote {
    
    public String Test_connection() throws RemoteException;
    public void subscribe(String name, Comunication_client c) throws RemoteException;
    public void criarEleicao()throws RemoteException;
    public boolean vote(String list, String eleica, int id_mesa, String depto, Date data)throws RemoteException;
    public void CadastrarPessoa()throws RemoteException;
    public  boolean autenticate(String campo, String dados) throws RemoteException;
    public  boolean unlock_terminal(String cartao, String pass)throws RemoteException;
    public  ArrayList<String> get_Eleicoes()throws RemoteException;
    public void CriarLista()throws RemoteException;
    public ArrayList<ListaCandidatos> get_Listas(String eleicao)throws RemoteException;
    public void CriarFaculdade_Dpto()throws RemoteException;
    public void alterar_eleicao();
}
