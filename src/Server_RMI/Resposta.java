/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;

import java.io.Serializable;

/**
 *
 * @author Admin
 */
public class Resposta implements Serializable{
    public int valor;
    public String mensagem;
    
    public Resposta(int valor, String mensagem){
        this.valor=valor;
        this.mensagem=mensagem;
    }
}
