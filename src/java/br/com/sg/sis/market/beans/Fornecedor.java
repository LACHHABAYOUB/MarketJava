package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public class Fornecedor extends Cadastrao {
    private String fantasia;
    private String contato;
    private String cnpj;

    public Fornecedor() {
        super();
    }
	
	public Fornecedor(String cnpj, String fantasia) {
		this.cnpj = cnpj;
		this.fantasia = fantasia;
	}

    public Fornecedor(Connection conn, UsuarioLogado usuarioLogado, ResultSet rs) throws Exception {
        super(conn, usuarioLogado, rs);
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getContato() {
        return contato;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }
}
