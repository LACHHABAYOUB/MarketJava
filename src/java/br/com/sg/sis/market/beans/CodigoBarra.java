package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public class CodigoBarra extends BaseBean {

    private Long id;
    private Integer clienteSg;
    private String cnpjClienteSg;
    private Integer codigoPro;
    private String codBarra;

    public CodigoBarra(Long id, Integer clienteSg, String cnpjClienteSg, Integer codigoPro, String codBarra) {
        this.id = id;
        this.clienteSg = clienteSg;
        this.cnpjClienteSg = cnpjClienteSg;
        this.codigoPro = codigoPro;
        this.codBarra = codBarra;
    }

    public CodigoBarra() {
    }

    public CodigoBarra(ResultSet rs) throws Exception {
        super(rs);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getClienteSg() {
        return clienteSg;
    }

    public void setClienteSg(Integer clienteSg) {
        this.clienteSg = clienteSg;
    }

    public String getCnpjClienteSg() {
        return cnpjClienteSg;
    }

    public void setCnpjClienteSg(String cnpjClienteSg) {
        this.cnpjClienteSg = cnpjClienteSg;
    }

    public Integer getCodigoPro() {
        return codigoPro;
    }

    public void setCodigoPro(Integer codigoPro) {
        this.codigoPro = codigoPro;
    }

    public String getCodBarra() {
        return codBarra;
    }

    public void setCodBarra(String codBarra) {
        this.codBarra = codBarra;
    }
    
    

}
