package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public class Endereco extends BaseBean {
    private String endereco;
    private String numero;
    private String cidade;
    private String bairro;
    private String cep;
    private String uf;

    public Endereco() {
        super();
    }

    public Endereco(ResultSet rs) throws Exception {
        super(rs);
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
