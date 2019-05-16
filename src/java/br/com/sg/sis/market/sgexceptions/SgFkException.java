/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions;

/**
 *
 * @author Lachhab Ayoub
 */
public class SgFkException extends SGExceptions {
    private String tabela;
    private String campo;
    private String tabelaReferenciada;
    private String campoReferenciado;

    public SgFkException(String tabelaReferenciada, String campo) {
        super("A tabela de " + tabelaReferenciada + " possui referencia para o " + campo + " desta tabela!");

        this.setTabela("");
        this.setCampo(campo);
        this.setTabelaReferenciada(tabelaReferenciada);
        this.setCampoReferenciado("");
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public String getCampoReferenciado() {
        return campoReferenciado;
    }

    public void setCampoReferenciado(String campoReferenciado) {
        this.campoReferenciado = campoReferenciado;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getTabelaReferenciada() {
        return tabelaReferenciada;
    }

    public void setTabelaReferenciada(String tabelaReferenciada) {
        this.tabelaReferenciada = tabelaReferenciada;
    }
}

