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
    public ArrayList<String> listaCandidato;
    String nome;
    Eleicao e;
    
    public ListaCandidatos(String nome){
      //  super();
        this.nome = nome;
        this.listaCandidato = new ArrayList();
        
    }
    
   public void setList(String nomecandidato){
       this.listaCandidato.add(nomecandidato);
   }
  
    public String toString(){
     return this.nome+ this.listaCandidato.toString();
    }
    
     /* public ArrayList<String> Getlista(String[] v){
      listacandidato = new ArrayList();
      for(int i=0;i<v.length;i++)
            listacandidato.add(i,v[i]);    
      return listacandidato;
    }*/
}