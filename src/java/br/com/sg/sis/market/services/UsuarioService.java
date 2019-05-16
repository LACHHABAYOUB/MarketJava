package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.UsuarioDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;

/**
 *
 * @author Lachhab Ayoub
 */
public class UsuarioService extends ServiceOperations {

    public UsuarioService() throws SGExceptions {
        this.setDao(new UsuarioDao(this.getUsuarioLogado()));
    }

    public String AlterarSenha(Usuario usuario, String senhaAntiga, Boolean considerarSenhaAntiga)throws Exception{
        return ((((UsuarioDao)getDao()).alterarSenha(usuario, senhaAntiga,considerarSenhaAntiga) == RetornoPersistencia.OK) ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }

    @Override
    public String Excluir(Long id) throws Exception {
        if (this.getUsuarioLogado().getUsuario().getId().equals(id)) {
            //TODO: futuramente o flex tratara a excecao
            throw new Exception("Nao é possível excluir o usuário logado");
        }
        return super.Excluir(id);
    }

}
