package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub 
 */
public class Mensagem extends BaseBean {

	private String mensagem;

	public Mensagem() {
		super();
	}

	public Mensagem(Connection conn, ResultSet rs) throws Exception {
		super(rs);
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

}
