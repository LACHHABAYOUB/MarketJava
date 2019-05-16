/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.ResultSet;
import java.sql.Connection;
import java.util.Date;

/**
 *
 * @author Lachhab Ayoub
 */
public class Pedido extends BaseBean {

    private String cnpjClienteSG;
    private Integer codfilial;
    private Integer cotacao;
    private Integer codigopro;
    private String descpro;
    private Integer qtde;
    private Integer qtdeemb;
    private double precounit;
    private Integer prazo;
    private Long codbarra;
    private String cnpjfornecedor;
    private Integer fkidfilial;
    private Integer fkiditemcotacao;
    private String vendedor;
    private Date lancado;
    private String mercado;
    private String nomefornecedor;

    public Pedido() {
        super();
    }

    public Pedido(Connection conn, ResultSet rs) throws Exception {
        super(rs);
    }

    public String getCnpjClienteSG() {
        return cnpjClienteSG;
    }

    public void setCnpjClienteSG(String cnpjClienteSG) {
        this.cnpjClienteSG = cnpjClienteSG;
    }

    public String getCnpjfornecedor() {
        return cnpjfornecedor;
    }

    public void setCnpjfornecedor(String cnpjfornecedor) {
        this.cnpjfornecedor = cnpjfornecedor;
    }

    public Long getCodbarra() {
        return codbarra;
    }

    public void setCodbarra(Long codbarra) {
        this.codbarra = codbarra;
    }

    public Integer getCodfilial() {
        return codfilial;
    }

    public void setCodfilial(Integer codfilial) {
        this.codfilial = codfilial;
    }

    public Integer getCodigopro() {
        return codigopro;
    }

    public void setCodigopro(Integer codigopro) {
        this.codigopro = codigopro;
    }

    public Integer getCotacao() {
        return cotacao;
    }

    public void setCotacao(Integer cotacao) {
        this.cotacao = cotacao;
    }

    public String getDescpro() {
        return descpro;
    }

    public void setDescpro(String descpro) {
        this.descpro = descpro;
    }

    public Integer getFkidfilial() {
        return fkidfilial;
    }

    public void setFkidfilial(Integer fkidfilial) {
        this.fkidfilial = fkidfilial;
    }

    public Integer getFkiditemcotacao() {
        return fkiditemcotacao;
    }

    public void setFkiditemcotacao(Integer fkiditemcotacao) {
        this.fkiditemcotacao = fkiditemcotacao;
    }

    public Date getLancado() {
        return lancado;
    }

    public void setLancado(Date lancado) {
        this.lancado = lancado;
    }

    public String getMercado() {
        return mercado;
    }

    public void setMercado(String mercado) {
        this.mercado = mercado;
    }

    public Integer getPrazo() {
        return prazo;
    }

    public void setPrazo(Integer prazo) {
        this.prazo = prazo;
    }

    public double getPrecounit() {
        return precounit;
    }

    public void setPrecounit(double precounit) {
        this.precounit = precounit;
    }

    public Integer getQtde() {
        return qtde;
    }

    public void setQtde(Integer qtde) {
        this.qtde = qtde;
    }

    public Integer getQtdeemb() {
        return qtdeemb;
    }

    public void setQtdeemb(Integer qtdeemb) {
        this.qtdeemb = qtdeemb;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getNomefornecedor() {
        return nomefornecedor;
    }

    public void setNomefornecedor(String nomefornecedor) {
        this.nomefornecedor = nomefornecedor;
    }
}
