package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.CadastraoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class CadastraoService extends ServiceOperations {

    public CadastraoService() throws SGExceptions {
        this.setDao(new CadastraoDao(this.getUsuarioLogado()));
    }

    public List<Usuario> ListarUsuariosDisponiveis() throws SGExceptions {
        return ((CadastraoDao) this.getDao()).listarUsuariosDisponiveis();
    }
    public List<Usuario> ListarUsuariosDisponiveisLog() throws SGExceptions {
        return ((CadastraoDao) this.getDao()).listarUsuariosDisponiveisLog();
    }

}
