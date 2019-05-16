package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import java.sql.Connection;
import javax.mail.AuthenticationFailedException;


/**
 *
 * @author Lachhab Ayoub 
 */
public class EnvioEmailCotacoesErroThread extends EnvioEmailNovasCotacoesThread {

    private Boolean manterEmExecucao = true;
    private final int OITO_HORAS = 28800000;
    
    private final String sqlBuscaNovasCotacoes =
                        "SELECT "
                        + " co.id AS idCotacao, "
                        + " co.cotacao AS codigoCotacao, "
                        + " co.cliente_sg as clienteSg, " 
                        + " co.fkidfilial as idFilial, "
                        + " co.fkidfornecedor as idFornecedor, "
                        + " co.fkIdMercado, "
                        + " replace(replace(replace(co.cgc_forn, '.', ''), '/', ''), '-', '') AS cnpjFornecedor, "
                        + " DATE_FORMAT(co.data, '%d/%m/%Y') as dataCadastro,"
                        + " DATE_FORMAT(co.dataencerramento, '%d/%m/%Y') as dataEncerramento, "
                        + " DATE_FORMAT(co.dataencerramento, '%H:%i:%s') as horaEncerramento, "
                        + " cm.email AS emailMercado,"
                        + " GROUP_CONCAT(distinct cv.email SEPARATOR ',') AS emailVendedor,"
                        + " cm.nome AS nomeFantasiaMercado,"
                        + " f.cnpj AS cnpjMercado"
                        + " FROM cotacao co"
                        + " INNER JOIN fornvend fv ON fv.fkidfornecedor = co.fkidfornecedor"
                        + " INNER JOIN mercfornvend mfv ON mfv.fkidfornvend = fv.id AND mfv.fkidmercado = co.fkidmercado"
                        + " INNER JOIN mercado m ON m.fkidcadastrao = co.fkidmercado"
                        + " INNER JOIN filial f ON f.fkidcadastrao = co.fkidfilial"
                        + " INNER JOIN cadastrao cv ON cv.id = fv.fkidvendedor"
                        + " INNER JOIN cadastrao cm ON cm.id = m.fkidcadastrao"
                        + " WHERE co.novo = 'E' and co.data = date(now())"
                        + " GROUP BY co.id";

    private Connection conn;
    
    public EnvioEmailCotacoesErroThread(){
        super();
        setSqlBuscaNovasCotacoes(sqlBuscaNovasCotacoes);
    }
    
    @Override
    public void run() {
        try {
            LogCotacao.escreveLog(LogCotacao.LEVEL_INFO, "Executando thread cotacoes com erro");
            
            int tempoEnvioEmailNovasCotacoes = OITO_HORAS;

            while (manterEmExecucao) {
                try {
                    setConn(Conexao.getC());
                    LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Buscando cotacoes com erro");
                    buscaNovasCotacoes();
                } catch (AuthenticationFailedException e) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
                } catch (Exception ex) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
                } finally {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Executar thead de cotacoes com erro daqui a " + (tempoEnvioEmailNovasCotacoes / 1000 / 60) + " minutos");
                    Thread.sleep(tempoEnvioEmailNovasCotacoes);
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
