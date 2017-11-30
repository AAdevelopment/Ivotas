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
import java.io.Serializable;
/**
 *
 * @author gustavo
 */
public class ListaCandidatos implements Serializable{
    public ArrayList<String> candidatos;
    public String nome;
    public String tipo;
    public ArrayList<Voto> votos;
    
    
    public ListaCandidatos(String nome, String tipo){
      //  super();
        this.nome = nome;
        this.tipo =tipo;
        this.candidatos = new ArrayList();
        this.votos=new ArrayList();
        
    }
    public void setLista(ArrayList<String> lista){
        this.candidatos=lista;
    }
    public void setList(ArrayList<String> lista){
        this.candidatos.addAll((lista));
    }
  
    public String toString(){
     return this.tipo+"|"+this.nome+"|"+ this.candidatos.toString();
    }
    
    public void printListaCandidatos(){
        System.out.println(this.nome+" | "+this.tipo+" | "+ this.votos.size());
        for(int i=0; i<this.candidatos.size();i++){
            System.out.print(this.candidatos.get(i)+" ; ");
        }
        System.out.println();
        for(int i=0;i<this.votos.size();i++){
            System.out.println(this.votos.get(i).toString());
        }

    }
    
}
