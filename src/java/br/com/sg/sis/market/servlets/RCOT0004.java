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
public class RCOT0004 extends Impressao{

    public RCOT0004(){
    }

    @Override
    public void setParametros(Connection conn, Map params) throws Exception{
        this.setPs(conn.prepareStatement("select " +
                    "NOW() as 'datahoraemissao'," +
                    "cadme.nome as 'nomemercado', co.cotacao as 'codcotacao', " +
                    "cadfo.nome as 'nomefornecedo', co.data_lanc as 'datacotacao', " +
                    "co.fechamento as 'datafechamento', co.fechadopor as 'fechadopor', " +
                    "co.dataencerramento as 'dataencerramento', " +
                    "(case when co.fechado = 'S' then 'Fechado'else " +
                    "(case when (NOW()<= co.dataencerramento and co.fechado != 'S') then 'Aberto' else " +
                    "(case when (NOW()> co.dataencerramento and co.fechado != 'S') then 'Encerrado' " +
                    "else '-' end) end) end) as 'situacao', " +
                    "ip.codigo_pro as 'codproduto', ip.desc_pro as 'descproduto', " +
                    "ip.preco as 'precoun', ip.perc_desc as 'desconto', " +
                    "ip.qtde_emb as 'qtdeemb', ip.quantidade as 'quantidade', ip.preco_emb as 'precoemb' " +
                    "from cotacao co " +
                    "inner join cadastrao cadme on co.fkidmercado = cadme.id " +
                    "inner join cadastrao cadfo on co.fkidfornecedor = cadfo.id " +
                    "inner join item_cotacao ip on co.id = ip.fkidcotacao " +
					"left join filial fi on fi.fkidmercado = co.fkidmercado " +
                    "where ((co.fkidmercado between ? and ?) or (co.fkidfornecedor between ? and ?) or (fi.fkidcadastrao between ? and ?)) " +
                "and co.cotacao = ? " +
                "and replace(replace(replace(co.cgc_forn,'.',''),'/',''),'-','') = ? " +
                "and ip.codigo_pro not in(select codigo_pro from pedido where cotacao = ? and replace(replace(replace(cpfcnpj,'.',''),'/',''),'-','') =? ) "+
                "group by ip.codigo_pro order by co.cotacao, cadfo.nome, ip.desc_pro "));
        this.getPs().setString(1, (String)params.get("usuariologado"));
        this.getPs().setString(2, (String)params.get("usuariologado"));
        this.getPs().setString(3, (String)params.get("usuariologado"));
        this.getPs().setString(4, (String)params.get("usuariologado"));
		this.getPs().setString(5, (String)params.get("usuariologado"));
        this.getPs().setString(6, (String)params.get("usuariologado"));

        this.getPs().setString(7, (String)params.get("cotacao"));
        this.getPs().setString(8, (String)params.get("fornecedor"));
        this.getPs().setString(9, (String)params.get("cotacao"));
        this.getPs().setString(10, (String)params.get("fornecedor"));
    }
}
