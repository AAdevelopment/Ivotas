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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    ArrayList<ListaCandidatos> listas_candidatas;
    transient Thread t;
    Date data;
    String data_texto;
    String horafim;
    String horaini;
    ArrayList<Mesa_voto> mesas;
    SimpleDateFormat dt;

    public String getData_texto() {
        return data_texto;
    }

    public String getHorafim() {
        return horafim;
    }

    public String getHoraini() {
        return horaini;
    }
    
    
    public Eleicao(String tipo,String titulo,String descricao, String data, ArrayList<String> deptos) throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        this.dptos=deptos;
        this.descricao=descricao;
        
        dt = new SimpleDateFormat("dd-MM-yyyy"); 
        this.data=dt.parse(data);
        this.listas_candidatas=new ArrayList();
        t = new Thread(this,titulo);
        t.start();
    }
    public Eleicao(String tipo,String titulo,String descricao, String data,String horaini,String horafim, ArrayList<String> deptos)throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        this.descricao=descricao;
        dt = new SimpleDateFormat("dd-MM-yyyy"); 
        this.data =dt.parse(data);
        this.data_texto = data;
        dptos=new ArrayList();
        t = new Thread(this,titulo);
        this.listas_candidatas=new ArrayList();
        this.mesas = new ArrayList();
        this.dptos=deptos;
        this.horafim=horafim;
        this.horaini=horaini;
    }
    public Eleicao(String tipo,String titulo,String descricao, String data,String horaini,String horafim)throws ParseException{
        this.tipo = tipo;
        this.titulo=titulo;
        dt = new SimpleDateFormat("dd-MM-yyyy"); 
        this.data =dt.parse(data);
        this.data_texto = data;
        dptos=new ArrayList();
        t = new Thread(this,titulo);
        this.listas_candidatas=new ArrayList();
        this.mesas = new ArrayList();
        this.dptos=new ArrayList();
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

    public void setData(String data) throws ParseException {
        this.data = dt.parse(data);
    }

    public void setHorafim(String horafim) {
        this.horafim = horafim;
    }

    public void setHoraini(String horaini) {
        this.horaini = horaini;
    }
    public void setMesas(Mesa_voto m){
        this.mesas.add(m);
    }
    
    public void setData_texto(String data){
        this.data_texto=data;
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
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(Eleicao.class.getName()).log(Level.SEVERE, null, ex);
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

        return "tipo|"+this.tipo+";"+"titulo|"+this.titulo+";"+"titulo|"+this.descricao+";"+"data|"+this.data+
        ";"+"inicio|"+this.horaini+";"+"fim|"+this.horafim;


    }
    
   

}
