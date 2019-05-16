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
public class RCOT0003 extends Impressao {

    public RCOT0003() {
    }

    @Override
    public void setParametros(Connection conn, Map params) throws Exception {
        this.setPs(conn.prepareStatement("select "
                + "NOW() as 'datahoraemissao', "
				+ "me.trabalhaTresCasasDecimais, "
                + "cadme.nome as 'nomemercado', co.cotacao as 'codcotacao', "
                + "cadfo.nome as 'nomefornecedo', co.data as 'datacotacao', "
                + "co.fechamento as 'datafechamento', co.fechadopor as 'fechadopor', "
                + "co.dataencerramento as 'dataencerramento', co.observacao as 'observacao', "
                + "(case when co.fechado = 'S' then 'Fechado'else "
                + "(case when (NOW()<= co.dataencerramento and co.fechado != 'S') then 'Aberto' else "
                + "(case when (NOW()> co.dataencerramento and co.fechado != 'S') then 'Encerrado' "
                + "else '-' end) end) end) as 'situacao', "
                + "ip.codigo_pro as 'codproduto', ip.desc_pro as 'descproduto', "
                + "ip.preco as 'precoun', ip.perc_desc as 'desconto', ip.unidade as 'unidade',"
                + "ip.qtde_emb as 'qtdeemb', ip.quantidade as 'quantidade', ip.preco_emb as 'precoemb' "
                + "from cotacao co "
                + "inner join cadastrao cadme on co.fkidmercado = cadme.id "
				+ "inner join mercado me on co.fkidmercado = me.fkidcadastrao "
                + "inner join cadastrao cadfo on co.fkidfornecedor = cadfo.id "
                + "inner join item_cotacao ip on co.id = ip.fkidcotacao "
                + "where ((co.fkidmercado between ? and ?) or (co.fkidfornecedor between ? and ?)) "
                + "and co.id in(" + params.get("cotacoes") + ") "
                + "and replace(replace(replace(co.cgc_forn,'.',''),'/',''),'-','') in(" + params.get("fornecedor") + ") "
                + "and co.cgc_forn = ip.cgc_forn "
                + "order by co.fkidmercado, co.cotacao, cadfo.nome, ip.desc_pro"));
        this.getPs().setString(1, (String) params.get("usuariologado"));
        this.getPs().setString(2, (String) params.get("usuariologado"));
        this.getPs().setString(3, (String) params.get("usuariologado"));
        this.getPs().setString(4, (String) params.get("usuariologado"));
    }
}
