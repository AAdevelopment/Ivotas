/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;

import java.io.Serializable;
import java.util.Date;
import mesa_voto.Mesa_voto;

/**
 *
 * @author gustavo
 */


public class Pessoa implements Serializable {
    String tipo_pessoa;
    String name;
    Long   cartao;
    String Password;
    String Dpto;
    Date card_valid;
    String tel;
    String morada;
    String localVoto;
    Date data_voto;
   
    public Pessoa(String tipo_pessoa,String name,Long cartao,String Password,String Dpto,Date card_valid,String tel,String morada){
        this.name =name;
        this.cartao =cartao;
        this.Password =Password;
        this.Dpto =Dpto;
        this.card_valid = card_valid ;
        this.tel = tel ;
        this.morada = morada;
        this.tipo_pessoa = tipo_pessoa; 
    }
    
    public String getLocalVoto() {
        return localVoto;
    }
    
    public void setData_voto() {
        this.data_voto = new Date();
    }

    public void setLocalVoto(Mesa_voto m) {
        //Mesa de voto precisa passar o local
        this.localVoto = m.departamento;
    }
    public void setName(String name){
        this.name =name;
    }
    public void setcartao(Long cartao){
            this.cartao = cartao;
    }
    public void setPassword(String Password){
        this.Password =Password;
    }
    public void setDpto_facul(String Dpto ){
        this.Dpto =Dpto;
    }
    public void setCard_valid(Date card_valid){
        this.card_valid = card_valid;
    }
    public void setTel(String tel){
        this.tel = tel;
    }
    public void setMorada(String morada){
        this.morada = morada;
    }
    
    public String getTipoPessoa(){
        return this.tipo_pessoa;
    }
    public String getName(){
        return this.name;
    }
    public Long getCartao(){
        return this.cartao;
    }
    public String getPassword(){
        return this.Password;
    }
    public String getDpto(){
        return this.Dpto;
    }
    public Date getCard_valid(){
        return this.card_valid;
    }
    public String getTel(){
        return this.tel;
    }
    public String getMorada(){
        return this.morada;
    }
    
    
}