/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author gustavo
 */
public class Faculdade  implements Serializable {
    ArrayList<String> dpto;
    String nome;
    
     public Faculdade(String nome){
        this.nome=nome;
        this.dpto = new ArrayList();
    }
    
    public void criarDPTO( ArrayList<String> array){
        this.dpto.addAll(array);
    }
    
    public String toString(){
        return this.nome+" "+ this.dpto.toString();
    }
}
