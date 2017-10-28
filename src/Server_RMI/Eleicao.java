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
    Thread t;
    String data;
    String horafim;
    String horaini;
    ArrayList<Mesa_voto> mesas;
    SimpleDateFormat dt;
    
    public Eleicao(){
        listas=new ArrayList();
        dptos=new ArrayList();
        this.mesas = new ArrayList();
        dt = new SimpleDateFormat("dd-mm-yyyy"); 

    }
    
    public Eleicao(String tipo,String titulo,String descricao, String data, ArrayList<String> deptos, ArrayList <ListaCandidatos> listas) throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        this.dptos=deptos;
        this.descricao=descricao;
        
        dt = new SimpleDateFormat("dd-mm-yyyy"); 
        
        this.listas=listas;
        t = new Thread(this,titulo);
        t.start();
    }
    public Eleicao(String tipo,String titulo,String data,String horaini,String horafim) throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        this.data = data;
        dptos=new ArrayList();
        t = new Thread(this,titulo);
        ArrayList<ListaCandidatos> listas=new ArrayList();
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
        //8hrs
        //this.data.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdf1= new SimpleDateFormat("dd/MM/yyyy");
        sdf1.setLenient(false);
        boolean verifica=true;
            while(verifica==true){
                if(sdf1.format(new Date()).equals(data)&&sdf.format(new Date()).equals(horaini)){
                    verifica=false;
                    while (true) {
                        System.out.println(sdf.format(new Date()));
                        try {
                            Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Eleicao.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        if (sdf.format(new Date()).equals(horafim)) {
                            System.out.println("fim da eleicao!");
                            break;
                        }            
                    }
                }
        /*
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
        
        if(now.after(horaini)){
           // System.out.println("passou no primeiro");
            boolean verifica=true;
            Integer verify;
            while(verifica==true){ 
                now=new Date(System.currentTimeMillis()); 
              //  System.out.println(System.currentTimeMillis()+" date: "+dt.format(now));
                verify=horafim.compareTo(now);
                if(verify==0){
                    break;
                }
                try {
                    
                    } catch (InterruptedException ex) {
                        ex.getMessage();
                    }        
                }
            
            }*/
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
        return this.tipo+";"+this.titulo+";"+this.data+
        ";"+this.horaini+";"+this.horafim+";"+this.mesas.toString();
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
