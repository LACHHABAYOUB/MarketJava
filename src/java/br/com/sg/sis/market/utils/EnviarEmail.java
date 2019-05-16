package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

/**
 *
 * @author Lachhab Ayoub
 */
public class EnviarEmail {

    private static final String HOST_SMTP = "smtp.gmail.com";
    private static final String PORTA_SMTP = "587";
    private static final String EMAIL_AUTENTICACAO = "cotacao@sgsistemas.com.br";
    private static final String SENHA_AUTENTICACAO = "Web@Cotacao";
    private static final String EMAIL_AUTENTICACAO2 = "cotacao2@sgsistemas.com.br";
    private static final String SENHA_AUTENTICACAO2 = "Web@Cotacao2";

    private static boolean emailPrincipal = true;
    
    public EnviarEmail() {
    }

    public static void mudarEmail(){
        emailPrincipal = !emailPrincipal;
    }
    
    public void enviar(String assunto, String corpo, byte[] bytesAnexo, String nomeArquivoAnexo, String de, String para, String nomeFantasiaMercado) throws Exception {
        Properties mailProps = System.getProperties();
        mailProps.put("mail.smtp.host", HOST_SMTP);
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.port", PORTA_SMTP);
        mailProps.put("mail.smtp.starttls.enable", "true");
        SimpleAuth auth;
        String emailAutenticacao;
        
        emailAutenticacao = getEmail();
        auth = new SimpleAuth(emailAutenticacao, getSenha());

        Session mailSession = Session.getInstance(mailProps, auth);

        MimeMessage email = new MimeMessage(mailSession);
        email.setRecipients(Message.RecipientType.TO, InternetAddress.parse(para));
        email.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(de));

        email.setFrom(new InternetAddress(de, nomeFantasiaMercado));
        email.setReplyTo(new Address[]{new InternetAddress(de, nomeFantasiaMercado)});

        email.setSubject(assunto);

        String message = assunto;
        email.setText(message);

        Multipart mp = new MimeMultipart();

        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setContent(corpo, "text/html; charset=utf-8");
        mp.addBodyPart(mbp1);

        if (bytesAnexo != null) {
            MimeBodyPart mbp2 = new MimeBodyPart();
            mbp2.setFileName(nomeArquivoAnexo);
            mbp2.setDataHandler(new DataHandler(new ByteArrayDataSource(bytesAnexo, "application/pdf")));
            mp.addBodyPart(mbp2);
        }

        email.setContent(mp);

        Transport tr = mailSession.getTransport("smtp");
        tr.connect();

        email.saveChanges();
        LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"Enviando Email:"+new Date(System.currentTimeMillis()) + " DE: "+de + " PARA: "+ para + "EMAIL DE ENVIO: " + emailAutenticacao);
        tr.sendMessage(email, email.getAllRecipients());
        tr.close();
    }
    
    public String getEmail(){
       if(emailPrincipal){
            LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"EMAIL1 "+emailPrincipal);
            return EMAIL_AUTENTICACAO;
        } else {
            LogCotacao.escreveLog(LogCotacao.LEVEL_INFO,"EMAIL2 "+emailPrincipal);
            return EMAIL_AUTENTICACAO2;
        } 
    }
    
    public String getSenha(){
       if(emailPrincipal){
            return SENHA_AUTENTICACAO;
        } else {
            return SENHA_AUTENTICACAO2;
        } 
    }

    public static void inserirLogEmail(Connection conn, Integer idCotacao, Integer clienteSg, Integer idFilial, Integer idFornecedor, String contexto, String emailAutenticacao, String emailOrigem, String emailDestino, String assunto, String conteudo, Boolean enviado, String mensagemErro) throws SQLException {
        String sqlInsereLogEmail = "INSERT INTO log_email values (null,?,?,?,?,?,now(),?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlInsereLogEmail);
            ps.setInt(1, idCotacao);
            ps.setInt(2, clienteSg);
            ps.setInt(3, idFilial);
            ps.setInt(4, idFornecedor);
            ps.setString(5, contexto);
            ps.setString(6, emailAutenticacao);
            ps.setString(7, emailOrigem);
            ps.setString(8, emailDestino);
            ps.setString(9, assunto);
            ps.setString(10, conteudo);
            ps.setBoolean(11, enviado);
            ps.setString(12, mensagemErro);
            ps.executeUpdate();
            conn.commit();
           
        }catch(Exception ex){
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, "EMAIL Destino" + emailDestino, ex );
        } finally {
            Conexao.closeC(null, ps, null);
        }
    }
}

class SimpleAuth extends Authenticator {

    public String username = null;
    public String password = null;

    public SimpleAuth(String user, String pwd) {
        username = user;
        password = pwd;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
