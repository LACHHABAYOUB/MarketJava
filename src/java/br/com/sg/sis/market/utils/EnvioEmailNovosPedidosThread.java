package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.mail.AuthenticationFailedException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author Lachhab Ayoub
 */
public class EnvioEmailNovosPedidosThread implements Runnable {

    // controle de execução da thread
    private Boolean manterEmExecucao = true;
    private final int UM_MINUTO = 60000; // 60.000 ms = 60 s = 1 min
    private static final int QUANTIDADE_MAXIMA_TENTATIVAS = 5;
    private final String sqlBuscaTempoEnvioEmailNovosPedidos = "SELECT * FROM parametro WHERE nomeParametro = 'tempoEnvioEmailNovosPedidos'";
    private String sqlBuscaNovosPedidos =
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
                        + "WHERE p.novo = 'S' "
                        + "GROUP BY co.id";
    private final String sqlAtualizaFlagPedidoNovo = "UPDATE pedido SET novo = 'N' WHERE cotacao = ? and fkidfilial = ?";
    private final String sqlAtualizaFlagPedidoNovoErro = "UPDATE pedido SET novo = 'E' WHERE cotacao = ? and fkidfilial = ?";
    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;
    // atributos abaixo são para o envio de email
    private String idFilial;
    private String cnpjFornecedor;
    private String clienteSg;
    private String codigoCotacao;
    private String idFornecedor;
    private String emailMercado;
    private String emailVendedor;
    private String nomeFantasiaMercado;
    private String numeroCotacao;
    private String cnpjMercado;
    private String assunto;
    private String mensagem;
    private byte[] bytesAnexo;
    private int tentativas;
    private EnviarEmail enviarEmail;


    private int buscaTempoEnvioEmailNovosPedidos() {
        int tempoEnvioEmailNovosPedidos = UM_MINUTO;// tempo padrão: 1 min
        
        try(Connection connInner = Conexao.getC();
            PreparedStatement psInner = connInner.prepareStatement(sqlBuscaTempoEnvioEmailNovosPedidos);
            ResultSet rsInner = psInner.executeQuery();   
            ) {

            if (rsInner.next()) {
                tempoEnvioEmailNovosPedidos = UM_MINUTO * Integer.parseInt(rsInner.getString("valorParametro"));
            }
        }catch(Exception e){
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
        }
        
        return tempoEnvioEmailNovosPedidos;
    }

    protected void buscaNovosPedidos() throws Exception {
        ps = getConn().prepareStatement(getSqlBuscaNovosPedidos());
        rs = ps.executeQuery();

        while (rs.next()) {
            try {
                idFilial = rs.getString("idFilial");
                codigoCotacao = rs.getString("codigoCotacao");
                clienteSg = rs.getString("clienteSg");
                idFornecedor = rs.getString("idFornecedor");
                cnpjFornecedor = rs.getString("cnpjFornecedor");
                emailMercado = rs.getString("emailMercado");
                emailVendedor = rs.getString("emailVendedor");
                nomeFantasiaMercado = rs.getString("nomeFantasiaMercado");
                numeroCotacao = rs.getString("numeroCotacao");
                cnpjMercado = rs.getString("cnpjMercado");

                if (!Functions.emailValido(emailVendedor)) {
                    atualizaFlag(true);
                    throw new RuntimeException("Email vendedor inválido: " + emailVendedor);
                }
                
                String emailMercadoValido = "";
                if ( Functions.emailValido(emailMercado) ) {
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
                            "pedido",
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
                                    "pedido",
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
                            "pedido",
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

        sb.append("Segue em anexo o pedido referente à cotação nº ").append(numeroCotacao);
        sb.append(", do cliente ").append(nomeFantasiaMercado);
        sb.append(", com CNPJ ").append(cnpjMercado).append(".");
        sb.append("<br/><br/> Acesse o sistema pelo Link: http://www.sghost.com.br/cotacao/");

        return sb.toString();
    }

    private byte[] constroiAnexoEmail() throws Exception {
        Map parametros = new HashMap();
        parametros.put("cotacoes", numeroCotacao);
        parametros.put("filial", idFilial);
        parametros.put("fornecedor", cnpjFornecedor);

        File file = new File("../webapps/CotacaoWeb/jasper/RCOT0002.jasper");

        JasperReport relJasper = (JasperReport) JRLoader.loadObject(file);

        Class classeRelatorio = Class.forName("br.com.sgsistemas.cotacao.cotacaoweb.servlets.RCOT0002");

        Impressao rel = (Impressao) classeRelatorio.newInstance();
        rel.setParametros(getConn(), parametros);

        JasperPrint impressao = JasperFillManager.fillReport(relJasper, parametros, new JRResultSetDataSource(rel.executaRelatorio()));

        return JasperExportManager.exportReportToPdf(impressao);
    }

    private void enviaEmail() throws Exception {
        assunto = "Cotação " + nomeFantasiaMercado;
        mensagem = constroiTextoEmail();

        String cnpjMercadoAux = cnpjMercado.replaceAll("[^0-9]", "");
        String nomeArquivoAnexo = "M" + cnpjMercadoAux + "PC" + numeroCotacao + ".pdf";

        bytesAnexo = constroiAnexoEmail();

        enviarEmail.enviar(assunto, mensagem, bytesAnexo, nomeArquivoAnexo, emailMercado, emailVendedor, nomeFantasiaMercado);
    }

    // atribuir pedido.novo = 'N' para não enviar novamente email para os pedidos que já receberam
    private void atualizaFlag(boolean erro) throws SQLException {
        try {
            if (erro) {
                ps = getConn().prepareStatement(sqlAtualizaFlagPedidoNovoErro);
            } else {
                ps = getConn().prepareStatement(sqlAtualizaFlagPedidoNovo);
            }

            ps.setString(1, numeroCotacao);
            ps.setString(2, idFilial);

            ps.executeUpdate();

            getConn().commit();
        } finally {
            Conexao.closeC(null, ps, null);
        }
    }

    @Override
    public void run() {
        try {
            LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Executando thread pedidos novos");
            
            int tempoEnvioEmailNovosPedidos = buscaTempoEnvioEmailNovosPedidos();

            while (manterEmExecucao) {
                try {
                    setConn(Conexao.getC());
                    LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Buscando pedidos novos");
                    buscaNovosPedidos();
                } catch (AuthenticationFailedException e) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
                } catch (Exception ex) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
                } finally {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Executar thead de pedidos novos daqui a " + (tempoEnvioEmailNovosPedidos /1000) +" segundos");
                    Thread.sleep(tempoEnvioEmailNovosPedidos);
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

    protected void setSqlBuscaNovosPedidos(String sqlBuscaNovosPedidos){
        this.sqlBuscaNovosPedidos = sqlBuscaNovosPedidos;
    }
    
    protected String getSqlBuscaNovosPedidos(){
        return sqlBuscaNovosPedidos;
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

