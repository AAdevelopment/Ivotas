/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;


/**
 *
 * @author gustavo
 */

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import mesa_voto.Mesa_voto;

/**
 *
 * @author gustavo
 */

public class Eleicao implements Runnable,Serializable {
    int ID;
    String tipo;
    String titulo;
    String descricao;
    ArrayList<ListaCandidatos> listas_candidatas;
    transient Thread t;
    Calendar data_inicio;
    Calendar data_fim;
    public Set<Mesa_voto> mesas;
    ArrayList<String> dptos;
    ArrayList<Voto> nulos;
    ArrayList<Voto> brancos;

    
    public void setData_inicio(Calendar data_inicio) {
        this.data_inicio = data_inicio;
    }

    public ArrayList<ListaCandidatos> getListas_candidatas() {
        return listas_candidatas;
    }

    public void setData_fim(Calendar data_fim) {
        this.data_fim = data_fim;
    }
    
    public Calendar getDataFim() {
        return data_fim;
    }

    public Calendar getDataInicio() {
        return data_inicio;
    }
    //construtor para carregar as eleicoes de ficheiro
    public Eleicao(int ID,String tipo,String titulo,String descricao, Calendar inicio, Calendar fim, ArrayList<String> deptos, Set<Mesa_voto> mesas) throws ParseException{
        this.ID=ID;
        this.tipo = tipo;
        this.titulo=titulo;
        this.mesas=mesas;
        this.dptos=deptos;
        this.descricao=descricao;
        this.data_inicio=inicio;
        this.data_fim=fim;
        this.listas_candidatas=new ArrayList();
        this.nulos=new ArrayList();
        this.brancos=new ArrayList();
        this.t = new Thread(this,titulo);
    }
   
    public Eleicao(String tipo,String titulo,String descricao, Calendar inicio, Calendar fim)throws ParseException{
        this.ID++;
        this.tipo = tipo;
        this.titulo=titulo;
        this.descricao=descricao;
        this.dptos=new ArrayList();
        this.t = new Thread(this,titulo);
        this.listas_candidatas=new ArrayList();
        this.mesas=new HashSet<>();
        this.dptos=new ArrayList();
        this.data_fim=fim;
        this.data_inicio=inicio;
        this.nulos=new ArrayList();
        this.brancos=new ArrayList();
    }
    
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public void setLista(ListaCandidatos lista){
        this.listas_candidatas.add(lista);
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
    
    @Override
    public synchronized void run(){
        System.out.println("A INICIAR A ELEICAO "+this.titulo);
        System.out.flush();
        for(Mesa_voto mesa: this.mesas){
            Thread thread= new Thread(mesa);
            thread.start();
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Eleicao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        while(true){
           if(isToStop()){
              System.out.println("TERMINADA ELEICAO "+this.titulo);
              System.out.flush();
              this.t.stop();
              
           }
       }
    }
    
    public boolean isToStop(){
        Calendar today= Calendar.getInstance();        
        if(today.compareTo(this.data_fim) ==0){
            return true;
        }
       return false;  
    }
    public void setDescricao(String descricao){
        this.descricao = descricao;
    }
    public String  getDescricao(){
        return this.descricao;
    }
    public String toStringMesas(){
       return this.mesas.toString();
    }
    @Override
    public String toString(){
        SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return "ID|"+this.ID+";"+"tipo|" + this.tipo+";"+"titulo|"+this.titulo+";"+"descricao|"+this.descricao+";"+"data_inicio|"+format.format(this.data_inicio.getTime())+
        ";"+"data_fim|"+format.format(this.data_fim.getTime());
    }
    
}
