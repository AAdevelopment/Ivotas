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

/**
 *
 * @author gustavo
 */

public class Eleicao implements Runnable,Serializable {
    String tipo;
    String titulo;
    String descricao;
    ArrayList<String> dptos;
    Thread t;
    Date data;
    Date horafim;
    Date horaini;
    SimpleDateFormat dt;
    
    public Eleicao(String tipo,String titulo,String descricao, String data, ArrayList<String> deptos) throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        this.dptos=deptos;
        this.descricao=descricao;
        this.data = new Date();
        dt = new SimpleDateFormat("dd-mm-yyyy"); 
        this.data =dt.parse(data);
        
        t = new Thread(this,titulo);
        t.start();
    }
    public Eleicao(String tipo,String titulo,String data) throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        this.data = new Date();
        dt = new SimpleDateFormat("dd-mm-yyyy"); 
        this.data =dt.parse(data);
        t = new Thread(this,titulo);
        System.out.println("");
    }
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public Date getData() {
        return data;
    }

    public void setData(String data) throws ParseException {
        this.data = new Date();
        this.data =dt.parse(data);

    }
    
    @Override
    public void run(){
        //8hrs
        //this.data.getTime();
        
        
        SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
        Date now = new Date();
        Date horafim = new Date();
        Date horaini = new Date();
        try {
            horafim=dt.parse("03:23");
            horaini=dt.parse("01:00");
        } catch (ParseException ex) {
            ex.getMessage();
        }
        
        //long inicio=System.currentTimeMillis();
        //long duracao=28800000;
        
        System.out.println(System.currentTimeMillis()+" date: "+dt.format(now.getTime())
        +"  "+dt.format(horaini.getTime())+" "+dt.format(horafim.getTime()));
        if(now.after(horaini)){
           // System.out.println("passou no primeiro");
            boolean verifica=true;
            Integer verify;
            while(verifica==true){ 
                now=new Date(System.currentTimeMillis()); 
                System.out.println(System.currentTimeMillis()+" date: "+dt.format(now));
                verify=horafim.compareTo(now);
                if(verify==0){
                    System.out.println("deu boa");
                    break;
                }
                else{
                    System.out.println("Mostra valor: "+verify);
                }
                try {
                    Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.getMessage();
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
        return this.tipo+this.titulo+this.data.toGMTString();
    }
    //
    /*
    *
    *Author Andre Santos
    */
    public void saveEleicao(){
        //funcao que salva as eleicoes no final das mesmas
        
    }

}
