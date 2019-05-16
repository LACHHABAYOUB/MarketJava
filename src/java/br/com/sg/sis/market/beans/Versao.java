/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

/**
 *
 * @author Lachhab Ayoub
 */
public class Versao extends BaseBean{

    private Date dataversao;
    private String baseline;

    public Versao() {
        super();
    }

    public Versao(Connection conn, ResultSet rs) throws Exception{
        super(rs);
    }

    public Date getDataversao() {
        return dataversao;
    }

    public void setDataversao(Date dataversao) {
        this.dataversao = dataversao;
    }

    public String getBaseline() {
        return baseline;
    }
  
    public void setBaseline(String baseline) {
        this.baseline = baseline;
    }
}
