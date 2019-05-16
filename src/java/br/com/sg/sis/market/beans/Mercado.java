package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public class Mercado extends Cadastrao {

    private String contato;
    private Integer clienteSgs;
    private String cnpjClienteSG;
    private Filial filial;
    private String trabalhaTresCasasDecimais;
   

    public Mercado() {
        super();
    }

    public Mercado(Connection conn, UsuarioLogado usuarioLogado, ResultSet rs) throws Exception {
        super(conn, usuarioLogado, rs);
    }

    public Integer getClienteSgs() {
        return clienteSgs;
    }

    public void setClienteSgs(Integer clienteSgs) {
        this.clienteSgs = clienteSgs;
    }

    public String getCnpjClienteSG() {
        return cnpjClienteSG;
    }

    public void setCnpjClienteSG(String cnpjClienteSG) {
        this.cnpjClienteSG = cnpjClienteSG;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public String getTrabalhaTresCasasDecimais() {
        return trabalhaTresCasasDecimais;
    }

    public void setTrabalhaTresCasasDecimais(String trabalhaTresCasasDecimais) {
        this.trabalhaTresCasasDecimais = trabalhaTresCasasDecimais;
    }

}
