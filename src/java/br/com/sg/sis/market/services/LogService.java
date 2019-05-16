/*
 * LogService.java
 *
 * Created on 2 de Junho de 2009, 08:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.services;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.LogInfoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;

/**
 *
 * @author Lachhab Ayoub 
 */
public class LogService extends ServiceOperations{
    
    //@SuppressWarnings("unchecked")
    public LogService() throws SGExceptions {
        this.setDao(new LogInfoDao(this.getUsuarioLogado()));
    }
    
}
