package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Versao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.MercadoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.UsuarioDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.VersaoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;

/**
 *
 * @author Lachhab Ayoub
 */
public class LoginService extends ServiceOperations {

    UsuarioLogado usuarioLogado = null;
    UsuarioLogado usuarioLogadoADM = null;

    public LoginService() throws SGExceptions {
        this.setDao(new UsuarioDao(this.getUsuarioLogado()));
    }

    public UsuarioLogado Logar(Usuario usuario) throws SGExceptions, CloneNotSupportedException {
        usuarioLogado = null;
        Usuario user = ((UsuarioDao) this.getDao()).locate(usuario.getLogin(), usuario.getSenha());

        if ((user != null) && (user.getSituacao().equals("A") || user.getSituacao().equals("I"))){
            usuarioLogado = new UsuarioLogado(user);
            usuarioLogado.setSituacao(user.getSituacao());
            usuarioLogado.setIp(getRequest().getRemoteHost());
            usuarioLogado.setVersao((Versao)new VersaoDao(usuarioLogado).locateLastVersao());
            if (user.getSituacao().equals("A")) {
                this.getSession().setAttribute("usuariologado", usuarioLogado);
            }
            if (user.getTipo().equals("AD")){
                // Faz backup do usuario ADM para futuro uso
                usuarioLogadoADM = usuarioLogado.clone();
                
                this.getSession().setAttribute("usuariologadoADM", usuarioLogadoADM);
            }
        }

        return usuarioLogado;
    }

    public Usuario TrocarUsuario(Long idMercado) throws SGExceptions, CloneNotSupportedException {
        if (idMercado.equals(0L)){
            // Restaura Usuario ADM para Usuario Logado
            usuarioLogado = ((UsuarioLogado)this.getSession().getAttribute("usuariologadoADM")).clone();

            this.getSession().setAttribute("usuariologado", usuarioLogado);
            return usuarioLogado.getUsuario();
        } else {
            Usuario usuario = (Usuario)((UsuarioDao) this.getDao()).locatebyIdCadastrao(idMercado);
            Mercado mercado = (Mercado)(new MercadoDao(this.getUsuarioLogado())).locate(idMercado);
            usuario.setCadastrao(mercado);
            usuarioLogado = (UsuarioLogado)this.getSession().getAttribute("usuariologado");
            usuarioLogado.setUsuario(usuario);
            this.getSession().setAttribute("usuariologado", usuarioLogado);
            return usuario;
        }
    }

    public void LogOut() throws Exception {
        try {            
            this.getSession().invalidate();
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            ex.printStackTrace();
            throw ex;
        }
    }
    
}
