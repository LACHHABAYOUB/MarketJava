/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.servlets;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.Impressao;
import java.sql.Connection;
import java.util.Map;

/**
 *
 * @author Lachhab Ayoub
 */
public class RCOT0001 extends Impressao {

    public RCOT0001(){
    }

    @Override
    public void setParametros(Connection conn, Map params) throws Exception{
        this.setPs(conn.prepareStatement("select " +
                " cadme.nome as 'cliente', " +
                " CURDATE() as 'dataemissao', " +
                " co.cotacao as 'numcotacao', " +
                " co.data as 'dataabertura', " +
                " CURTIME() AS 'horaemissao', " +
                " co.cgc_forn as 'cnpjfornecedor', " +
                " cadfo.nome as 'fornecedor', " +
                " co.fechadopor as 'fechadopor', " +
                " co.fechamento as 'datafechamento', " +
                " (case when co.fechado = 'S' then 'Fechado'" +
                " else (case when (NOW()<= co.dataencerramento and co.fechado != 'S') then 'Aberto' " +
                " else (case when (NOW()> co.dataencerramento and co.fechado != 'S') then 'Encerrado' " +
                " else '-' end) end) end) as 'situacao', " +
                " (case when co.fechado = 'S' then 1 else 0 end) as 'fechado', " +
                " (case when (NOW()<= co.dataencerramento and co.fechado != 'S') then 1 else 0 end) as 'aberto', " +
                " (case when (NOW()> co.dataencerramento and co.fechado != 'S') then 1 else 0 end) as 'encerrado' " +
                " from cotacao co " +
                " inner join cadastrao cadme on co.fkidmercado = cadme.id " +
                " inner join cadastrao cadfo on co.fkidfornecedor = cadfo.id " +
				" left join filial fi on fi.fkidmercado = co.fkidmercado "+
                " where (co.fkidmercado between ? and ?) or (fi.fkidcadastrao between ? and ?)" +
                " and co.cotacao = ? " +
                " and (case when 'T' = ? then co.fechado = co.fechado " +
                " else (case when 'F' = ? then co.fechado = 'S' " +
                " else (case when 'A' = ? then (NOW()<= co.dataencerramento and co.fechado != 'S') " +
                " else (case when 'E'= ? then (NOW()> co.dataencerramento and co.fechado != 'S') " +
                " end) end) end) end)" +
                " order by cotacao, fornecedor"));
        this.getPs().setString(1, (String)params.get("usuariologado"));
        this.getPs().setString(2, (String)params.get("usuariologado"));
        this.getPs().setString(3, (String)params.get("usuariologado"));
        this.getPs().setString(4, (String)params.get("usuariologado"));
		this.getPs().setString(5, (String)params.get("cotacao"));
        this.getPs().setString(6, (String)params.get("statuscotacao"));
        this.getPs().setString(7, (String)params.get("statuscotacao"));
        this.getPs().setString(8, (String)params.get("statuscotacao"));
        this.getPs().setString(9, (String)params.get("statuscotacao"));
    }

}
