package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;
import javax.mail.AuthenticationFailedException;

/**
 *
 * @author Lachhab Ayoub 
 */
public class EnvioEmailNovasCotacoesThread implements Runnable {

    // controle de execução da thread
    private Boolean manterEmExecucao = true;
    private final int UM_MINUTO = 60000; // 60.000 ms = 60 s = 1 min
    private static final int QUANTIDADE_MAXIMA_TENTATIVAS = 3;
    private final String sqlBuscaTempoEnvioEmailNovasCotacoes =
                        "SELECT * FROM parametro WHERE nomeParametro = 'tempoEnvioEmailNovasCotacoes'";
    private String sqlBuscaNovasCotacoes =
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
                        + " WHERE co.novo = 'S'"
                        + " GROUP BY co.id";
    private final String sqlAtualizaFlagCotacaoNovo = "UPDATE cotacao SET novo = 'N' WHERE id = ?";
    private final String sqlAtualizaFlagCotacaoNovoErro = "UPDATE cotacao SET novo = 'E' WHERE id = ?";
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    // atributos abaixo são para o envio de email
    private String idCotacao;
    private String codigoCotacao;
    private String idMercado;
    private String cnpjFornecedor;
    private String clienteSg;
    private String idFilial;
    private String idFornecedor;
    private String dataCadastro;
    private String dataEncerramento;
    private String horaEncerramento;
    private String emailMercado;
    private String emailVendedor;
    private String nomeFantasiaMercado;
    private String cnpjMercado;
    private String assunto;
    private String mensagem;
    private byte[] bytesAnexo;
    private int tentativas;
    private EnviarEmail enviarEmail;
    
    private int buscaTempoEnvioEmailNovasCotacoes() {
        int tempoEnvioEmailNovasCotacoes = UM_MINUTO;// tempo padrão: 1 min
        
        try(Connection connInner = Conexao.getC();
            PreparedStatement psInner = connInner.prepareStatement(sqlBuscaTempoEnvioEmailNovasCotacoes);
            ResultSet rsInner = psInner.executeQuery();   
            ) {

            if (rsInner.next()) {
                tempoEnvioEmailNovasCotacoes = UM_MINUTO * Integer.parseInt(rsInner.getString("valorParametro"));
            }
        }catch(Exception e){
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
        }
        
        return tempoEnvioEmailNovasCotacoes;

    }

    protected void buscaNovasCotacoes() throws Exception {
        
        ps = getConn().prepareStatement(getSqlBuscaNovasCotacoes());
        rs = ps.executeQuery();

        while (rs.next()) {
            try {
                idCotacao = rs.getString("idCotacao");
                codigoCotacao = rs.getString("codigoCotacao");
                clienteSg = rs.getString("clienteSg");
                idFilial = rs.getString("idFilial");
                idFornecedor = rs.getString("idFornecedor");
                idMercado = rs.getString("fkIdMercado");
                cnpjFornecedor = rs.getString("cnpjFornecedor");
                dataCadastro = rs.getString("dataCadastro");
                dataEncerramento = rs.getString("dataEncerramento");
                horaEncerramento = rs.getString("horaEncerramento");
                emailMercado = rs.getString("emailMercado");
                emailVendedor = rs.getString("emailVendedor");
                nomeFantasiaMercado = rs.getString("nomeFantasiaMercado");
                cnpjMercado = rs.getString("cnpjMercado");
                
                if (!Functions.emailValido(emailVendedor)) {
                    atualizaFlag(true);
                    throw new RuntimeException("Email vendedor inválido: " + emailVendedor);
                }
                
                String emailMercadoValido = "";
                if (Functions.emailValido(emailMercado)) {
                    emailMercadoValido = emailMercado;
                }
                
                while(tentativas < QUANTIDADE_MAXIMA_TENTATIVAS) {
                    tentativas++;
                    enviarEmail = new EnviarEmail();
                    try {
                        enviaEmail();
                        atualizaFlag(false);
                        EnviarEmail.inserirLogEmail(getConn(),
                                Integer.parseInt(codigoCotacao), 
                                Integer.parseInt(clienteSg), 
                                Integer.parseInt(idFilial), 
                                Integer.parseInt(idFornecedor), 
                                "cotacao",
                                new EnviarEmail().getEmail(), 
                                emailMercadoValido,
                                emailVendedor, 
                                assunto, 
                                constroiTextoEmail(), 
                                true, 
                                "tentativa:" + tentativas);
                         break;
                    } catch (Exception ex) {
                        EnviarEmail.mudarEmail();
                        atualizaFlag(true);
                        LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
                        EnviarEmail.inserirLogEmail(getConn(),
                                Integer.parseInt(codigoCotacao), 
                                Integer.parseInt(clienteSg), 
                                Integer.parseInt(idFilial), 
                                Integer.parseInt(idFornecedor), 
                                "cotacao",
                                new EnviarEmail().getEmail(), 
                                emailMercado,
                                emailVendedor, 
                                assunto, 
                                constroiTextoEmail(), 
                                false, 
                                "tentativa:" + tentativas + "\n" + Functions.stack2String(ex));
                    }

                    Thread.sleep(1000);
                }
                tentativas = 0;

            } catch (Exception ex) {
                atualizaFlag(true);
                LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
                EnviarEmail.inserirLogEmail(getConn(),
                            Integer.parseInt(codigoCotacao), 
                            Integer.parseInt(clienteSg), 
                            Integer.parseInt(idFilial), 
                            Integer.parseInt(idFornecedor), 
                            "cotacao",
                            new EnviarEmail().getEmail(), 
                            emailMercado,
                            emailVendedor, 
                            assunto, 
                            constroiTextoEmail(), 
                            false, 
                            "tentativa:" + tentativas + "\n" + Functions.stack2String(ex));

            }
        } 
    }

    private String constroiTextoEmail() {
        StringBuilder sb = new StringBuilder();
        Locale localeBr = new Locale("pt", "br");
        Calendar agora = Calendar.getInstance(localeBr);
        int hora = agora.get(Calendar.HOUR_OF_DAY);

        if (hora < 12) {
            sb.append("Bom dia.<br/><br/>");
        } else if (hora >= 12 && hora < 18) {
            sb.append("Boa tarde.<br/><br/>");
        } else {
            sb.append("Boa noite.<br/><br/>");
        }

        sb.append("Foi aberta uma cotação no dia ").append(dataCadastro);
        sb.append(", do cliente ").append(nomeFantasiaMercado);
        sb.append(", com CNPJ ").append(cnpjMercado);
        sb.append(", onde a mesma será encerrada dia ").append(dataEncerramento);

        if (horaEncerramento.equals("00:00:00")) {
            sb.append(".");
        } else {
            sb.append(" às ").append(horaEncerramento);
            sb.append(" hs");
        }

        sb.append("<br/><br/> Acesse o sistema pelo Link: http://www.sghost.com.br/cotacao/");
        sb.append("<br/><br/> Para digitar suas cotações através de seu dispositivo móvel, instale nosso aplicativo <a href=\"https://play.google.com/store/apps/details?id=air.br.com.sgsistemas.cotacao.CotacaoMobileFlex&hl=pt_BR\">clicando aqui (GooglePlay)</a>");

        return sb.toString();
    }

    private void enviaEmail() throws Exception {
        assunto = "Cotação " + nomeFantasiaMercado;
        mensagem = constroiTextoEmail();
        enviarEmail.enviar(assunto, mensagem, null, null, emailMercado, emailVendedor, nomeFantasiaMercado);

    }

    // atribuir cotacao.novo = 'N' para não enviar novamente email para os pedidos que já receberam
    private void atualizaFlag(boolean erro) throws SQLException {
        try {
            if (erro) {
                ps = getConn().prepareStatement(sqlAtualizaFlagCotacaoNovoErro);
            } else {
                ps = getConn().prepareStatement(sqlAtualizaFlagCotacaoNovo);
            }

            ps.setString(1, idCotacao);

            ps.executeUpdate();

            getConn().commit();
            
        }catch(Exception ex){
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
        } finally {
            Conexao.closeC(null, ps, null);
        }
    }
    
    @Override
    public void run() {
        try {
            LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Executando thread cotacoes novas");
                    
            int tempoEnvioEmailNovasCotacoes = buscaTempoEnvioEmailNovasCotacoes();

            while (manterEmExecucao) {
                try {
                    setConn(Conexao.getC());
                    LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Buscando cotacoes novas");
                    buscaNovasCotacoes();
                } catch (AuthenticationFailedException e) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
                } catch (Exception ex) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
                } finally {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Executar thead de cotacoes novas daqui a " + (tempoEnvioEmailNovasCotacoes /1000) +" segundos");
                    Thread.sleep(tempoEnvioEmailNovasCotacoes);
                    Conexao.closeC(getConn(), ps, rs);
                }
            }
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
        }
    }

    public void interromper() {
        manterEmExecucao = false;
    }

    protected String getSqlBuscaNovasCotacoes(){
        return this.sqlBuscaNovasCotacoes;
    }
    
    protected void setSqlBuscaNovasCotacoes(String sqlBuscaNovasCotacoes){
        this.sqlBuscaNovasCotacoes = sqlBuscaNovasCotacoes;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public PreparedStatement getPs() {
        return ps;
    }

    public ResultSet getRs() {
        return rs;
    }
    
    
    
    
}
