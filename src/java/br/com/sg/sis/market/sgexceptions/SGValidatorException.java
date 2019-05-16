/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions;

/**
 *
 * @author Lachhab Ayoub
 */
public class SGValidatorException extends SGExceptions{
    public SGValidatorException(String campos){
        super("** ATENÇÃO: Os valores dos campos " + campos + " está Inválido!! **");
    }
}
