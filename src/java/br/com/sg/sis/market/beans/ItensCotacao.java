package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class ItensCotacao extends BaseBean {

    private Integer codigoPro;
    private String cnpjClienteSG;
    private String descPro;
    private String cnpjFornecedor;
    private String fantasiaFornecedor;
    private String preco;
    private Integer prazo;
    private String unidade;
    private Integer qtdeEmb;
    private Integer quantidade;
    private String precoEmb;
    private Integer cotacao;
    private Double percDesc;
    private Double percAcre;
    private Long codBarra;
    private Long fkidCotacao;
    private String itemGanho;
    private Integer codFilial;
    private List<FornecedorPrecoPrazo> fornecedorPrecoPrazo = new ArrayList<FornecedorPrecoPrazo>();

    public ItensCotacao() {
        super();
    }

    public ItensCotacao(ResultSet rs) throws Exception {
        super(rs);
    }

    public Integer getCotacao() {
        return cotacao;
    }

    public void setCotacao(Integer cotacao) {
        this.cotacao = cotacao;
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

    public String getFantasiaFornecedor() {
        return fantasiaFornecedor;
    }

    public void setFantasiaFornecedor(String fantasiaFornecedor) {
        this.fantasiaFornecedor = fantasiaFornecedor;
    }

    public Integer getCodigoPro() {
        return codigoPro;
    }

    public void setCodigoPro(Integer codigoPro) {
        this.codigoPro = codigoPro;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public Integer getPrazo() {
        return prazo;
    }

    public void setPrazo(Integer prazo) {
        this.prazo = prazo;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getQtdeEmb() {
        return qtdeEmb;
    }

    public void setQtdeEmb(Integer qtdeEmb) {
        this.qtdeEmb = qtdeEmb;
    }

    public String getPrecoEmb() {
        return precoEmb;
    }

    public void setPrecoEmb(String precoEmb) {
        this.precoEmb = precoEmb;
    }

    public Double getPercDesc() {
        return percDesc;
    }

    public void setPercDesc(Double percDesc) {
        this.percDesc = percDesc;
    }

    public Double getPercAcre() {
        return percAcre;
    }

    public void setPercAcre(Double percAcre) {
        this.percAcre = percAcre;
    }

    public Long getCodBarra() {
        return codBarra;
    }

    public void setCodBarra(Long codBarra) {
        this.codBarra = codBarra;
    }

    public String getDescPro() {
        return descPro;
    }

    public void setDescPro(String descPro) {
        this.descPro = descPro;
    }

    public Long getFkidCotacao() {
        return fkidCotacao;
    }

    public void setFkidCotacao(Long fkidCotacao) {
        this.fkidCotacao = fkidCotacao;
    }

    public String getItemGanho() {
        return itemGanho;
    }

    public void setItemGanho(String itemGanho) {
        this.itemGanho = itemGanho;
    }

    public List<FornecedorPrecoPrazo> getFornecedorPrecoPrazo() {
        return fornecedorPrecoPrazo;
    }

    public void setFornecedorPrecoPrazo(List<FornecedorPrecoPrazo> fornecedorPrecoPrazo) {
        this.fornecedorPrecoPrazo = fornecedorPrecoPrazo;
    }

    public Integer getCodFilial() {
        return codFilial;
    }

    public void setCodFilial(Integer codFilial) {
        this.codFilial = codFilial;
    }

}
