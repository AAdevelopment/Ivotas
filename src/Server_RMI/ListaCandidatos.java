/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 *
 * @author gustavo
 */
public class ListaCandidatos implements Serializable{
    public ArrayList<String> Lista;
    public String nome;
    public String tipo;
    public ArrayList<Voto> votos;
    
    public ListaCandidatos(){
        Lista=new ArrayList();
    }
    
    public ListaCandidatos(String nome){
      //  super();
        this.nome = nome;
        this.Lista = new ArrayList();
        this.votos=new ArrayList();
        
    }
    public ListaCandidatos(String nome, String tipo){
      //  super();
        this.nome = nome;
        this.tipo =tipo;
        this.Lista = new ArrayList();
        this.votos=new ArrayList();
        
    }
    public void setLista(ArrayList<String> lista){
        this.Lista=lista;
    }
   public void setList(String nomecandidato){
       this.Lista.add(nomecandidato);
   }
  
    public String toString(){
     return this.tipo+this.nome+ this.Lista.toString();
    }
    
     /* public ArrayList<String> Getlista(String[] v){
      listacandidato = new ArrayList();
      for(int i=0;i<v.length;i++)
            listacandidato.add(i,v[i]);    
      return listacandidato;
    }*/
}
