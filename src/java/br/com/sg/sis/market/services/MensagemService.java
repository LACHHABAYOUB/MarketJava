/*
 * MensagemService.java
 *
 * Created on 28 de Novembro de 2013, 16:52
 *
 */
package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mensagem;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.MensagemDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
//import br.com.sgsistemas.cotacao.cotacaoweb.utils.Messages;

/**
 *
 * @author Lachhab Ayoub 
 */
public class MensagemService extends ServiceOperations {
	
	UsuarioLogado usuarioLogado = null;
	
		public MensagemService() throws SGExceptions {
        this.setDao(new MensagemDao(this.getUsuarioLogado()));
    }
	
	public Mensagem localizaMensagem() throws SGExceptions, CloneNotSupportedException, Exception {
        usuarioLogado = null;
        Mensagem mensagem = ((MensagemDao) this.getDao()).locate();
        return mensagem;
    }
}
