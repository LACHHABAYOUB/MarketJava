/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions;

/**
 *
 * @author Lachhab Ayoub
 */
public class SGDuplicateKeyExceptions extends SGExceptions{
 //private String campo;

    public SGDuplicateKeyExceptions(String campo) {
        super("** ATENÇÃO: Já Existe relacionamento com este registro " + campo + " que deve ser único!! **");
        //this.campo = campo;
    }

}
