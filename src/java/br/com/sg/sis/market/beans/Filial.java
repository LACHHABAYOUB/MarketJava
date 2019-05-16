package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Eduardo Lachhab Ayoub
 */

public class Filial extends Cadastrao {
    private Integer codFilial;
    private String cnpj;
    private String contato;
    private Usuario usuario;


    public Filial() {
        super();
    }

    public Filial(Connection conn, UsuarioLogado usuarioLogado, ResultSet rs) throws Exception {
        super(conn, usuarioLogado, rs);
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Integer getCodFilial() {
        return codFilial;
    }

    public void setCodFilial(Integer codFilial) {
        this.codFilial = codFilial;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}