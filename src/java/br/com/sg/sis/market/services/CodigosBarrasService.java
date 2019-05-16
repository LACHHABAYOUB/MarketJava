package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.CodigoBarra;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.CodigosBarrasDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class CodigosBarrasService extends ServiceOperations {

    public CodigosBarrasService() throws SGExceptions {
        this.setDao(new CodigosBarrasDao(this.getUsuarioLogado()));
    }

    public List<CodigoBarra> FindByClienteAndCnpjAndProduto(Integer clienteSg, String cnpjClienteSg, Integer codigoProduto) throws SQLException {
        List<CodigoBarra> retorno = ((((CodigosBarrasDao) this.getDao()).findByClienteAndCnpjAndProduto(clienteSg, cnpjClienteSg, codigoProduto)));
        return retorno;
    }
}
