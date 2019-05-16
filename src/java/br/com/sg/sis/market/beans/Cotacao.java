package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

/**
 *
 * @author Lachhab Ayoub
 */
public class Cotacao extends BaseBean {

    private Integer cotacao;
    private Date dataCotacao;
    private String cnpjClienteSG;
    private Date dataFechamento;
    private String fechadoPor;
    private String cnpjFornecedor;
    private Integer prazoCotacao;
    private Date dataDigitacao;
    private Integer prazo1;
    private Integer prazo2;
    private Integer prazo3;
    private String fechado;
    private Date dataEncerramento;
    private Mercado mercado;
    private Fornecedor fornecedor;
    private Date dataAtual;
    private String observacao;
    private String idCotacaoAgrupado;
    private String cotacaoAgrupado;
    private String cnpjFornecedorAgrupado;
    private String gerado;
    private Integer codFilial;
    private Boolean existePedido;

    public Cotacao() {
        super();
    }

    public Cotacao(Connection conn, ResultSet rs) throws Exception {
        super(rs);
    }
    
    public String getCnpjClienteSG() {
        return cnpjClienteSG;
    }

    public void setCnpjClienteSG(String cnpjClienteSG) {
        this.cnpjClienteSG = cnpjClienteSG;
    }

    public String getCnpjFornecedor() {
        return cnpjFornecedor;
    }

    public void setCnpjFornecedor(String cnpjFornecedor) {
        this.cnpjFornecedor = cnpjFornecedor;
    }

    public Integer getCotacao() {
        return cotacao;
    }

    public void setCotacao(Integer cotacao) {
        this.cotacao = cotacao;
    }

    public Date getDataAtual() {
        return dataAtual;
    }

    public void setDataAtual(Date dataAtual) {
        this.dataAtual = dataAtual;
    }

    public Date getDataCotacao() {
        return dataCotacao;
    }

    public void setDataCotacao(Date dataCotacao) {
        this.dataCotacao = dataCotacao;
    }

    public Date getDataDigitacao() {
        return dataDigitacao;
    }

    public void setDataDigitacao(Date dataDigitacao) {
        this.dataDigitacao = dataDigitacao;
    }

    public Date getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(Date dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public String getFechado() {
        return fechado;
    }

    public void setFechado(String fechado) {
        this.fechado = fechado;
    }

    public String getFechadoPor() {
        return fechadoPor;
    }

    public void setFechadoPor(String fechadoPor) {
        this.fechadoPor = fechadoPor;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Mercado getMercado() {
        return mercado;
    }

    public void setMercado(Mercado mercado) {
        this.mercado = mercado;
    }

    public Integer getPrazo1() {
        return prazo1;
    }

    public void setPrazo1(Integer prazo1) {
        this.prazo1 = prazo1;
    }

    public Integer getPrazo2() {
        return prazo2;
    }

    public void setPrazo2(Integer prazo2) {
        this.prazo2 = prazo2;
    }

    public Integer getPrazo3() {
        return prazo3;
    }

    public void setPrazo3(Integer prazo3) {
        this.prazo3 = prazo3;
    }

    public Integer getPrazoCotacao() {
        return prazoCotacao;
    }

    public void setPrazoCotacao(Integer prazoCotacao) {
        this.prazoCotacao = prazoCotacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getIdCotacaoAgrupado() {
        return idCotacaoAgrupado;
    }

    public void setIdCotacaoAgrupado(String idCotacaoAgrupado) {
        this.idCotacaoAgrupado = idCotacaoAgrupado;
    }

    public String getCotacaoAgrupado() {
        return cotacaoAgrupado;
    }

    public void setCotacaoAgrupado(String cotacaoAgrupado) {
        this.cotacaoAgrupado = cotacaoAgrupado;
    }

    public String getCnpjFornecedorAgrupado() {
        return cnpjFornecedorAgrupado;
    }

    public void setCnpjFornecedorAgrupado(String cnpjFornecedorAgrupado) {
        this.cnpjFornecedorAgrupado = cnpjFornecedorAgrupado;
    }

    public String getGerado() {
        return gerado;
    }

    public void setGerado(String gerado) {
        this.gerado = gerado;
    }

    public Integer getCodFilial() {
        return codFilial;
    }

    public void setCodFilial(Integer codFilial) {
        this.codFilial = codFilial;
    }

    public Boolean getExistePedido() {
        return existePedido;
    }

    public void setExistePedido(Boolean existePedido) {
        this.existePedido = existePedido;
    }

}
