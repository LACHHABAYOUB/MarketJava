/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.servlets;

/**
 *
 * @author Lachhab Ayoub
 */
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Impressao;
import java.sql.Connection;
import java.util.Map;

public class RCOT0002 extends Impressao{
    public RCOT0002(){
    }
      @Override
     public void setParametros(Connection conn, Map params) throws Exception{
        this.setPs(conn.prepareStatement("select "+
                 " NOW() as 'datahoraemissao', "+
                 " me.trabalhaTresCasasDecimais, "+
                 " pe.id as 'pedido', pe.cnpj_cliente_sg as 'cnpjClienteSG', "+
                 " pe.cod_filial as 'codfilial', "+
                 " pe.cotacao as 'cotacao', pe.cpfcnpj as 'cnpj_fornecedor', "+
                 " pe.codbarra as 'codbarra', pe.desc_pro as 'desc_produto', "+
                 " pe.qtde as 'qtde', pe.qtde_emb as 'qtde_emb', pe.preco_unit as 'preco_unit', "+
                 " pe.prazo as 'prazo', e.endereco as 'endereco', e.numero as 'numero', e.cidade as 'cidade', e.uf as 'uf', "+
                 " c.nome as 'razao_filial', c.fonecomercial as 'fone_comercial_filial', "+
                 " forn.fantasia as 'forn_fantasia', "+
                 " fc.nome as 'forn_razao', "+
                 " ip.unidade as 'unidade', "+
                 " ifnull(pe.observacao,'') as 'observacao' "+
                 " from pedido pe  "+
                 " left join filial f on pe.fkidfilial = f.fkidcadastrao "+
                 " left join endereco e on e.fkidcadastrao = f.fkidcadastrao "+
                 " left join cadastrao c on f.fkidcadastrao = c.id "+
                 " left join fornecedor forn on forn.cnpj = pe.cpfcnpj "+
                 " left join cadastrao fc on forn.fkidcadastrao = fc.id "+
                 " left join mercado me on f.fkidmercado = me.fkidcadastrao "+
                 " left join item_cotacao ip on pe.fkiditemcotacao = ip.id "+
                 " where pe.cotacao in("+params.get("cotacoes")+")  "+
                 " and pe.fkidfilial in("+params.get("filial")+") "+
                 " and replace(replace(replace(forn.cnpj,'.',''),'/',''),'-','') in("+params.get("fornecedor")+")" +
                 " order by pe.fkidfilial, forn.fantasia, pe.desc_pro"));
              
    }
}
