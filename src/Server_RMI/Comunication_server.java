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
import java.util.Calendar;
import java.util.Set;
import mesa_voto.Mesa_voto;



public interface Comunication_server extends Remote {
    
    public String Test_connection() throws RemoteException;
    public void subscribe(String name, Comunication_client c) throws RemoteException;
    public Eleicao criarEleicao(String v[])throws RemoteException;
    public boolean vote(String lista, Eleicao eleicao, Pessoa pessoa, Mesa_voto mesa, Calendar data)throws RemoteException;
    public void CadastrarPessoa(String o[])throws RemoteException;
    public Pessoa autenticate(String campo, String dados) throws RemoteException;
    public Resposta unlock_terminal(Pessoa pessoa,String CC, String Password)throws RemoteException;
    public ArrayList<Eleicao> get_Eleicoes()throws RemoteException;
    public void CriarLista(ArrayList<String> lista,String nome,String tipo)throws RemoteException;
    public ArrayList<ListaCandidatos> get_Listas(Eleicao eleicao)throws RemoteException;
    public void CriarFaculdade_Dpto(String nome,ArrayList<String> array)throws RemoteException;
    public void alterar_eleicao(String nome_eleicao,String v[])throws RemoteException;
    public Eleicao procuraEleicao(String titulo) throws RemoteException;
    public Mesa_voto create_mesa(String departamento) throws RemoteException;
    public boolean alterarDepartamento(String oldname, String new_name) throws RemoteException;
    public boolean removeDepartamento(String departamento) throws RemoteException;
    public boolean removeMesaVoto(int id) throws RemoteException;
    public void Count_voters(Eleicao e,Mesa_voto mesa)throws RemoteException;
    public Pessoa procurarPessoaCC(long CC) throws RemoteException;
    public Mesa_voto procuraMesa(int id) throws RemoteException;
    public ListaCandidatos procuraLista(String nome) throws RemoteException;
    public String getMesasList()throws RemoteException;
    public String getCandidatosList()throws RemoteException;
    public boolean Add_list_to_Election(String el, String lista) throws RemoteException;
    public boolean Add_table_to_election(String titulo_eleicao, int id)throws RemoteException;
    public boolean Add_dpto_to_election(String eleicao, String nome) throws RemoteException;
    public Set<Mesa_voto> getBufferMesas()throws RemoteException;
    public ArrayList<Faculdade> getBufferFaculdade()throws RemoteException;
    public  Resposta unlock_Web(Pessoa pessoa,String CC, String Password)throws RemoteException;
}
