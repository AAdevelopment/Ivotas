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
import mesa_voto.Mesa_voto;



public interface Comunication_server extends Remote {
    
    public String Test_connection() throws RemoteException;
    public void subscribe(String name, Comunication_client c) throws RemoteException;
    public void criarEleicao()throws RemoteException;
    public void vote(String lista, Eleicao eleicao, Pessoa pessoa, Mesa_voto mesa, Date data)throws RemoteException;
    public void CadastrarPessoa()throws RemoteException;
    public  Pessoa autenticate(String campo, String dados) throws RemoteException;
    public  Resposta unlock_terminal(Pessoa pessoa,String CC, String Password)throws RemoteException;
    public  ArrayList<Eleicao> get_Eleicoes()throws RemoteException;
    public void CriarLista()throws RemoteException;
    public ArrayList<ListaCandidatos> get_Listas(Eleicao eleicao)throws RemoteException;
    public void CriarFaculdade_Dpto()throws RemoteException;
    public void alterar_eleicao(Eleicao e)throws RemoteException;
    public void Add_ELectionlocal(String local,Pessoa p)throws RemoteException;
    public Eleicao getEleicao(String titulo) throws RemoteException;
}
