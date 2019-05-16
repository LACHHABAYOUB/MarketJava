package br.com.sgsistemas.cotacao.cotacaoweb.beans;

/**
 *
 * @author Lachhab Ayoub
 */
public class FornecedorPrecoPrazo {

	private Long idItemGanho;
	private String cnpfFornecedor;
	private String preco;
	private int prazo;
	private String itemGanho;
    private Integer codFilial;

	public FornecedorPrecoPrazo(ItensCotacao itemCotacao) {
		this.idItemGanho = itemCotacao.getId();
		this.cnpfFornecedor = itemCotacao.getCnpjFornecedor();
		this.preco = itemCotacao.getPreco();
		this.prazo = itemCotacao.getPrazo();
		this.itemGanho = itemCotacao.getItemGanho();
        this.codFilial = itemCotacao.getCodFilial();
	}

	public Long getIdItemGanho() {
		return idItemGanho;
	}

	public void setIdItemGanho(Long idItemGanho) {
		this.idItemGanho = idItemGanho;
	}

	
	public String getCnpfFornecedor() {
		return cnpfFornecedor;
	}

	public void setCnpfFornecedor(String cnpfFornecedor) {
		this.cnpfFornecedor = cnpfFornecedor;
	}

	public String getPreco() {
		return preco;
	}

	public void setPreco(String preco) {
		this.preco = preco;
	}

	public int getPrazo() {
		return prazo;
	}

	public void setPrazo(int prazo) {
		this.prazo = prazo;
	}

	public String getItemGanho() {
		return itemGanho;
	}

	public void setItemGanho(String itemGanho) {
		this.itemGanho = itemGanho;
	}

    public Integer getCodFilial() {
        return codFilial;
    }

    public void setCodFilial(Integer codFilial) {
        this.codFilial = codFilial;
    }
}
