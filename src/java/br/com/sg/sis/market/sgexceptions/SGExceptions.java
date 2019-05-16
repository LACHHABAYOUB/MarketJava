/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions;

/**
 *
 * @author Lachhab Ayoub
 */
public class SGExceptions extends Exception {

    public SGExceptions() {
    }

    public SGExceptions(String Mensagem) {
        super(Mensagem);
    }

    public SGExceptions(String message, Throwable cause) {
        super(message, cause);
    }

}
