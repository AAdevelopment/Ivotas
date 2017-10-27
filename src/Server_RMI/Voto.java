/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server_RMI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class Voto implements Serializable{
    private ArrayList<String> ListaCandidatos;
    private Date data;
    private String departamento;

    public ArrayList<String> getListaCandidatos() {
        return ListaCandidatos;
    }

    public void setListaCandidatos(ArrayList<String> ListaCandidatos) {
        this.ListaCandidatos = ListaCandidatos;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
}
