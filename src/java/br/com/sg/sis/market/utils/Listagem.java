package br.com.sgsistemas.cotacao.cotacaoweb.utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class Listagem {
    private Integer numReg;
    private List registros;

    public Listagem() {
    }

    /** Creates a new instance of Listagem */
    public Listagem(Integer numReg, List objList) {
        this.setNumReg(numReg);
        this.setRegistros(objList);
    }

    public Integer getNumReg() {
        return numReg;
    }

    public void setNumReg(Integer numReg) {
        this.numReg = numReg;
    }

    public List getRegistros() {
        return registros;
    }

    public void setRegistros(List registros) {
        this.registros = registros;
    }
}
