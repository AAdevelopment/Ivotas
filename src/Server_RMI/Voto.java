/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import mesa_voto.Mesa_voto;

/**
 *
 * @author Admin
 */
public class Voto implements Serializable{
    private Calendar data;
    Eleicao eleicao;
    Mesa_voto mesa;

    public Voto(Calendar data,Eleicao eleicao,Mesa_voto mesa){
        this.data=data;
        this.mesa=mesa;
        this.eleicao=eleicao;
    }
    public Voto(Calendar data,String titulo,Mesa_voto mesa){
        this.data=data;
        this.mesa=mesa;
        this.eleicao.titulo=titulo;
    }

    public void setEleicao(Eleicao eleicao) {
        this.eleicao = eleicao;
    }
   
    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

 public String toString(){
     SimpleDateFormat dt=new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
     return this.eleicao.titulo+";"+this.mesa.ID+";"+dt.format(data.getTime());
 }
    
}
