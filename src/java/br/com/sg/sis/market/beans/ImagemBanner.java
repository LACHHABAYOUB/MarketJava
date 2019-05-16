/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public class ImagemBanner extends BaseBean {

    private Long fkidmercado;
    private String descricao;
    private String nomearquivo;
    private byte[] arquivo;

    public ImagemBanner() {
    }

    public ImagemBanner(ResultSet rs) throws Exception {
        this.setId(rs.getLong("id"));
        this.fkidmercado = rs.getLong("fkidmercado");
        this.descricao = rs.getString("descricao");
        this.arquivo = rs.getBytes("arquivo");
        this.nomearquivo = rs.getString("nomearquivo");
    }

    public Long getFkidmercado() {
        return fkidmercado;
    }

    public void setFkidmercado(Long fkidmercado) {
        this.fkidmercado = fkidmercado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomearquivo() {
        return nomearquivo;
    }

    public void setNomearquivo(String nomearquivo) {
        this.nomearquivo = nomearquivo;
    }

    public byte[] getArquivo() {
        return arquivo;
    }

    public void setArquivo(byte[] arquivo) {
        this.arquivo = arquivo;
    }
}
