/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.ResultSet;
import java.util.Date;

/**
 *
 * @author Lachhab Ayoub
 */
public class LogInfo extends BaseBean {

    private Date data;
    private String operacao;
    private String descricao;
    private String loginUsuario;
    private String hora;
    private String ip;

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public LogInfo(TipoOperacao tipoOperacao, UsuarioLogado usuarioLogado) {
        this();

        switch (tipoOperacao){
            case INSERCAO:
                this.setOperacao("INS");
            break;

            case ALTERACAO:
                this.setOperacao("ALT");
            break;

            case DELECAO:
                this.setOperacao("DEL");
            break;

            case LOG:
                this.setOperacao("LOG");
                break;
            case FECHAMENTO:
                this.setOperacao("FEC");
                break;
            case REABERTURA:
                this.setOperacao("RAB");
                break;
        }
        if(usuarioLogado == null){
            this.setIp("");
            this.setLoginUsuario("");
        }else{
            this.setIp(usuarioLogado.getIp());
            this.setLoginUsuario(usuarioLogado.getUsuario().getLogin());
        }
        
    }

    public LogInfo() {
        this.setId(-1L);
    }

    public LogInfo(ResultSet rs) throws Exception {
        Functions.copyPropertiesRegistroBancoToFormBean(rs, this);
    }

    public LogInfo(Date dataLog, ResultSet rs) throws Exception {
        Functions.copyPropertiesRegistroBancoToFormBean(rs, this);

        /* Seta a data de Log */
        this.setData(data);
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

  

    public enum TipoOperacao {
        INSERCAO, ALTERACAO, DELECAO, LOG, FECHAMENTO, REABERTURA;
    }
    public String getLoginUsuario() {
        return loginUsuario;
    }

    public void setLoginUsuario(String loginUsuario) {
        this.loginUsuario = loginUsuario;
    }
}
