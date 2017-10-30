/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gustavo
 */

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import mesa_voto.Mesa_voto;

/**
 *
 * @author gustavo
 */

public class Eleicao implements Runnable,Serializable {
    String tipo;
    String titulo;
    String descricao;
    ArrayList<String> dptos;
    ArrayList<ListaCandidatos> listas;
    transient Thread t;
    Date data;
    String data_texto;
    String horafim;
    String horaini;
    ArrayList<Mesa_voto> mesas;
    SimpleDateFormat dt;
    
    
    public Eleicao(String tipo,String titulo,String descricao, String data, ArrayList<String> deptos) throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        this.dptos=deptos;
        this.descricao=descricao;
        
        dt = new SimpleDateFormat("dd-MM-yyyy"); 
        this.data=dt.parse(data);
        this.listas=new ArrayList();
        t = new Thread(this,titulo);
        t.start();
    }
    public Eleicao(String tipo,String titulo,String data,String horaini,String horafim)throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        dt = new SimpleDateFormat("dd-MM-yyyy"); 
        this.data =dt.parse(data);
        this.data_texto = data;
        dptos=new ArrayList();
        t = new Thread(this,titulo);
        this.listas=new ArrayList();
        this.mesas = new ArrayList();
        this.horafim=horafim;
        this.horaini=horaini;
    }
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public void setLista(ListaCandidatos lista){
        this.listas.add(lista);
    }
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public ArrayList<String> getDptos() {
        return dptos;
    }

    public void setDptos(ArrayList<String> dptos) {
        this.dptos = dptos;
    }
    public void setMesas(Mesa_voto m){
        this.mesas.add(m);
    }
    @Override
    public void run(){
        
        DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        boolean verifica=true;
       
        while(verifica==true){
            if(dt.format(new Date()).equals(this.data_texto)&&sdf.format(new Date()).equals(horaini)){
                verifica=false;
                while (true) {
                    System.out.println(sdf.format(new Date()));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Eleicao.class.getName()).log(Level.SEVERE, null, ex);            
                    }
                    if (sdf.format(new Date()).equals(horafim)) {
                        System.out.println("fim da eleicao "+this.titulo+" !");
                        break;
                        
                    }
                }
            }
        }
    }
        
    public void StartEleicao(){
        t = new Thread(this,titulo);
        t.start();
    }
    
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
    public String  getDescricao(){
        return this.descricao;
    }
    @Override
    public String toString(){

        return "tipo|"+this.tipo+";"+"titulo|"+this.titulo+";"+"data|"+this.data+
        ";"+"inicio|"+this.horaini+";"+"fim|"+this.horafim;


    }
    
   

}
