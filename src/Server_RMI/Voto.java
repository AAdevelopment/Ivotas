/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import mesa_voto.Mesa_voto;

/**
 *
 * @author Admin
 */
public class Voto implements Serializable{
    private Date data;
    Eleicao eleicao;
    Mesa_voto mesa;

    public Voto(Date data,Eleicao eleicao,Mesa_voto mesa){
        this.data=data;
        this.mesa=mesa;
        this.eleicao=eleicao;
    }
    public Voto(Date data,String titulo,Mesa_voto mesa){
        this.data=data;
        this.mesa=mesa;
        this.eleicao.titulo=titulo;
    }

    public void setEleicao(Eleicao eleicao) {
        this.eleicao = eleicao;
    }
   
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

 public String toString(){
     DateFormat dt=new SimpleDateFormat("hh:mm dd-mm-yyyy");
     return this.eleicao.titulo+";"+this.mesa.departamento+";"+dt.format(this.data);
 }
    
}
