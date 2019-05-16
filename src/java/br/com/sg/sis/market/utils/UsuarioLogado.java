package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Versao;

/**
 *
 * @author Lachhab Ayoub
 */
public class UsuarioLogado implements Cloneable{

    private Usuario usuario;
    private String situacao;
    private String ip;
    private Versao versao;

    public UsuarioLogado(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public UsuarioLogado clone() throws CloneNotSupportedException {
        return (UsuarioLogado)super.clone();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Versao getVersao() {
        return versao;
    }

    public void setVersao(Versao versao) {
        this.versao = versao;
    }
}
