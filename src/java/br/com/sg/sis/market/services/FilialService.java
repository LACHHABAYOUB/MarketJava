package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.FilialDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;


/**
 *
 * @author Lachhab Ayoub
 */
public class FilialService extends ServiceOperations {

    public FilialService() throws SGExceptions {
        this.setDao(new FilialDao(this.getUsuarioLogado()));
    }   

    public Boolean LocateByCnpj(String cnpj) {
        return ((FilialDao)getDao()).locateByCnpj(cnpj);
    }

    /* Persiste a filial */
    public String SaveOrUpdate(BaseBean filial, Mercado mercado) throws Exception {
        return (((FilialDao) this.getDao()).saveOrUpdate(filial, mercado) == RetornoPersistencia.OK ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }
	
	 @Override
     public String Excluir(Long id) throws Exception {
        return ((new FilialDao(this.getUsuarioLogado()).excluir(id) == RetornoPersistencia.OK) ? MSGDELETEOK : MSGDELETENAOOK);
    }
    
}
