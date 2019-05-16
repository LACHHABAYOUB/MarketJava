/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions;

/**
 *
 * @author Lachhab Ayoub
 */
public class SGExcluiUsuarioException extends SGExceptions{
    public SGExcluiUsuarioException(String campo) {
        super("** ATENÇÃO: Não é possível excluir o usuario " + campo +  " quando estiver logado ");
        //this.campo = campo;
    }

}
