/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.UsuarioDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.EnviarEmail;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import flex.messaging.FlexContext;
import flex.messaging.FlexSession;
import flex.messaging.io.ArrayCollection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;

/**
 *
 * @author Lachhab Ayoub
 */
public class RecuperaSenhaService extends ServiceOperations {

    public RecuperaSenhaService() throws SGExceptions {
    }

    public String SolicitaChave(String email) throws SGExceptions, Exception {
        Usuario usuario = null;
        UsuarioDao uD = new UsuarioDao(this.getUsuarioLogado());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        usuario = (Usuario) uD.LocateByLogin(email);
        if (usuario != null) {
            if (Functions.emailValido(usuario.getEmail())) {
                String chave = "";
                for (int i = 0; i < 10; i++) {
                    /* Gera um número aleatório até 122 (correspondente ao z em ASCII*/
                    int num = new Random().nextInt(122);
                    /* Se o número não estiver no intervalo 48~57(0-9), 65~90(A-Z) ou 97~122(a-z), recria o número*/
                    while (num < 48 || (num > 57 && num < 65) || (num > 90 && num < 97)) {
                        num = new Random().nextInt(122);
                    }
                    chave += (char) num;
                }
                usuario.setChave(chave);

                TimeZone timeZone = TimeZone.getDefault();
                Calendar dtExpiraChave = GregorianCalendar.getInstance(timeZone);
                dtExpiraChave.add(GregorianCalendar.DAY_OF_MONTH, 1);
                usuario.setDtExpiraChave(dtExpiraChave.getTime());

                if (uD.saveOrUpdate(usuario) == RetornoPersistencia.OK) {
                    EnviarEmail enviarEmail = new EnviarEmail();
                    enviarEmail.enviar("Cotação On-Line",
                    "Prezado(a) " + usuario.getNome() + ", <br/><br/>"
                    + "Uma solicitação de alteração de sua senha foi feita.<br/>"
                    + "Sua chave de recuperação é : " + chave
                    + "<br/>Atenção: Esta chave é válida somente até " + sdf.format(usuario.getDtExpiraChave()) + ".<br/><br/>"
                    + "Caso esta solicitação não tenha sido feita por você, recomendamos que entre em contato com sua Rede.<br/><br/>"
                    + "Atenciosamente,<br/>"
                    + "SG Sistemas", null, null, enviarEmail.getEmail(), usuario.getEmail(), "Cotação On-Line");

                    return "Recuperação de senha solicitada com sucesso. Uma chave de recuperação foi enviada para o e-mail: " + usuario.getEmail();
                } else {
                    return "Falha ao solicitar Recuperação de senha. Favor, entre em contato com a Rede";
                }
            } else {
                return "O usuário informado não possui e-mail para recuperação de senha cadastrado. Favor comunique a Rede";
            }
        } else {
            return "Email não encontrado!!!";
        }
    }

    public Usuario RecuperaSenha(String email, String chave) throws SGExceptions, Exception {
        Usuario usuario = null;
        UsuarioDao uD = new UsuarioDao(this.getUsuarioLogado());
        usuario = (Usuario) uD.LocateByLogin(email);

        if (usuario != null) {
            if (usuario.getChave() != null && usuario.getChave().equals(chave)) {
                Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
                if (usuario.getDtExpiraChave() != null && usuario.getDtExpiraChave().before(calendar.getTime())) {
                    /* Seta data como null quando a data expirou para verificar no flex*/
                    usuario.setDtExpiraChave(null);
                    uD.saveOrUpdate(usuario);
                } else if (usuario.getDtExpiraChave() != null) {
                    /* TUDO OK */
                    // Seta o usuário na sessão para conseguir efetuar a troca de senha e logar operaçoes
                    FlexSession mySession;
                    mySession = FlexContext.getFlexSession();
                    UsuarioLogado usuLogado = new UsuarioLogado(usuario);
                    usuLogado.setUsuario(usuario);
                    mySession.setAttribute("usuarioLogado", usuLogado);
                }
            } else {
                /* Seta chave como null quando a chave estiver incorreta para verificar no flex*/
                usuario.setChave(null);
            }
        }
        return usuario;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayCollection Listar(Integer filtro, String[] params) throws SGExceptions {
        UsuarioDao usuarioDao = new UsuarioDao(this.getUsuarioLogado());
        ArrayCollection listaUsuarios = new ArrayCollection();
        listaUsuarios.addAll(usuarioDao.listarUsuarioRecuperaSenha(filtro, params));
        return listaUsuarios;
    }
    
    public String TrocarSenha(Boolean verificaSenhaAntiga, String[] params) {
        /* Prepara para obter dados da sessão */

        String senhaMd5 = codificaSenha(params[1]);

        //quando não verifica a senha antiga significa que um usuário da rede está alterando a senha de um associado 
        if (!verificaSenhaAntiga || senhaMd5.equals(this.getUsuarioLogado().getUsuario().getSenha())) {
            UsuarioDao ud = new UsuarioDao(this.getUsuarioLogado());

            try {
                if (ud.alterarSenhaRecuperacao(params, codificaSenha(params[2]))) {
                    if (verificaSenhaAntiga) {
                        //senha alterada com sucesso, então altera a senha do usuário da sessao
                        this.getUsuarioLogado().getUsuario().setSenha(codificaSenha(params[2]));
                    }

                    return "Informações Alteradas com Sucesso!!!";
                } else {
                    //falha ao alterar senha
                    return "Não foi possível Alterar Registro!";
                }
            } catch (Exception ex) {
                LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, ex);
                return ex.getMessage();
            }
        } else {
            return "Senha informada não confere!";
        }
    }

    public static String codificaSenha(String senha) {
        try {
            //Quando se aplica o md5sum (do linux) colocando a senha em um arquivo,
            //deve-se lembrar que o flag de fim de arquivo '0x0A' é também usado no
            //cálculo. Para obter o mesmo resultado com este algoritmo, descomente
            //as próximas duas linhas para incluir o 0x0A na senha.
            //String flagFimDeArquivo = Character.toString((char) 0x0A);
            //senha = senha + flagFimDeArquivo;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(senha.getBytes());
            byte[] xx = md.digest();
            //---------------------------------------
            //Converte byte[] para um String de hexa:
            //---------------------------------------
            String n2 = null;
            StringBuffer resposta = new StringBuffer();
            for (int i = 0; i < xx.length; i++) {
                //para todos os bytes...
                //Obtém apenas o último byte do Integer.
                //O AND com 0xFF elimina a propagação do sinal
                //negativo que preenche todos os outros bytes
                //do int com ffffff.
                n2 = Integer.toHexString(0XFF & ((int) (xx[i])));
                //Evita um único caracter no hexa colocando zero antes.
                if (n2.length() < 2) {
                    n2 = "0" + n2;
                }
                resposta.append(n2);
            }
            return resposta.toString();
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, e);
            return "ERRO";
        }
    }
}
