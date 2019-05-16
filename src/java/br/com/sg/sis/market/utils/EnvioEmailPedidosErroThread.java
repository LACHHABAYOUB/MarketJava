package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import java.sql.Connection;
import javax.mail.AuthenticationFailedException;

/**
 *
 * @author Lachhab Ayoub
 */
public class EnvioEmailPedidosErroThread extends EnvioEmailNovosPedidosThread {

    private Boolean manterEmExecucao = true;
    private final int OITO_HORAS = 28800000;
    
    private final String sqlBuscaNovosPedidos =
                        "SELECT "
                        + "   p.fkidfilial AS idFilial, "
                        + "   co.cotacao AS codigoCotacao, "
                        + "   co.cliente_sg as clienteSg, " 
                        + "   co.fkidfornecedor as idFornecedor, "
                        + "   replace(replace(replace(p.cpfcnpj, '.', ''), '/', ''), '-', '') AS cnpjFornecedor, "
                        + "   cm.email AS emailMercado, "
                        + "   GROUP_CONCAT(distinct cv.email SEPARATOR ',') AS emailVendedor, "
                        + "   cm.nome AS nomeFantasiaMercado, "
                        + "   p.cotacao AS numeroCotacao, "
                        + "   m.cnpj_cliente_sg AS cnpjMercado "
                        + "FROM pedido p "
                        + "   INNER JOIN item_cotacao ic ON ic.id = p.fkiditemcotacao "
                        + "   INNER JOIN cotacao co ON co.id = ic.fkidcotacao "
                        + "   INNER JOIN fornvend fv ON fv.fkidfornecedor = co.fkidfornecedor "
                        + "   INNER JOIN mercfornvend mfv ON mfv.fkidfornvend = fv.id AND mfv.fkidmercado = co.fkidmercado "
                        + "   INNER JOIN mercado m ON m.fkidcadastrao = co.fkidmercado "
                        + "   INNER JOIN cadastrao cv ON cv.id = fv.fkidvendedor "
                        + "   INNER JOIN cadastrao cm ON cm.id = m.fkidcadastrao "
                        + "WHERE p.novo = 'E' and co.data = date(now())"
                        + "GROUP BY co.id";
    private Connection conn;
    
    public EnvioEmailPedidosErroThread(){
        super();
        setSqlBuscaNovosPedidos(sqlBuscaNovosPedidos);
    }
    
    @Override
    public void run() {
        try {
            LogCotacao.escreveLog(LogCotacao.LEVEL_INFO, "Executando thread pedidos com erro");

            int tempoEnvioEmailNovosPedidos = OITO_HORAS;

            while (manterEmExecucao) {
                try {
                    setConn(Conexao.getC());
                    LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Buscando pedidos com erro");
                    buscaNovosPedidos();
                } catch (AuthenticationFailedException e) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
                } catch (Exception ex) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
                } finally {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Executar thead de pedidos com erro daqui a " + (tempoEnvioEmailNovosPedidos / 1000 / 60) + " minutos");
                    Thread.sleep(tempoEnvioEmailNovosPedidos);
                    Conexao.closeC(getConn(), getPs(), getRs());
                }
            }

        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
        }
    }

    public void interromper() {
        manterEmExecucao = false;
    }

}
