package Server_RMI;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




import java.rmi.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Set;
import mesa_voto.Mesa_voto;

/**
 *
 * @author gustavo
 */
public interface Comunication_client extends Remote {
     public void reply_on_client(String a)throws RemoteException;
     public void reply_list_on_client(ListaCandidatos list)throws RemoteException;
     public void reply_FacultyDptolist_on_client(Faculdade f)throws RemoteException;
     public void replyElection(Eleicao e)throws RemoteException;
     public void replyNrVoters(String state)throws RemoteException;
     public void replyPeople(Pessoa p)throws RemoteException;
}