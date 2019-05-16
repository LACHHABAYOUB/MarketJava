package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.daos.MercFornVendDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;

/**
 *
 * @author Lachhab Ayoub
 */
public class MercFornVendService extends ServiceOperations {

    public  MercFornVendService() throws SGExceptions {
        this.setDao(new MercFornVendDao(this.getUsuarioLogado()));
    }

    public String Deletemfv(Long idMercado, Long idVendedor, Long idFornecedor) throws Exception {
        return ((((MercFornVendDao)getDao()).delete(idMercado, idVendedor, idFornecedor) == RetornoPersistencia.OK) ? MSGDELETEOK : MSGDELETENAOOK);
    }

}
