package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.daos.MercadoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;

/**
 *
 * @author Lachhab Ayoub
 */
public class MercadoService extends ServiceOperations {    

    public MercadoService() throws SGExceptions {
        this.setDao(new MercadoDao(this.getUsuarioLogado()));
    }

    public Boolean LocateByCnpj(String cnpj) throws SGExceptions {
        return ((MercadoDao)getDao()).locateByCnpj(cnpj);
    }
    
}
