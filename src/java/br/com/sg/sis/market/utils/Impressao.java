/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author Lachhab Ayoub
 */
public abstract class Impressao {    
    private PreparedStatement ps;

    public abstract void setParametros(Connection conn, Map params) throws Exception;

    public ResultSet executaRelatorio() throws SQLException {
        return ps.executeQuery();

    }

    public String nomeRelatorio() {
        return this.getClass().getSimpleName().concat(".jasper");
    }

    public PreparedStatement getPs() {
        return ps;
    }

    public void setPs(PreparedStatement ps) {
        this.ps = ps;
    }
}
