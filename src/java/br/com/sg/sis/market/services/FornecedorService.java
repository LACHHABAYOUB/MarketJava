package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.FornecedorDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.MercFornDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;

/**
 *
 * @author Lachhab Ayoub
 */
public class FornecedorService extends ServiceOperations {

    public FornecedorService() throws SGExceptions {
        this.setDao(new FornecedorDao(this.getUsuarioLogado()));
    }

    public Fornecedor locateByCnpj(String cnpj) throws SGExceptions {
        /* Cnpj é uma string porque ele vem com a mascara */
        return ((FornecedorDao) this.getDao()).locateByCnpj(cnpj);
    }

    public Fornecedor LocateByMercado(Long idMercado, Long idFornecedor) throws SGExceptions {
        return ((FornecedorDao) this.getDao()).locateByMercado(idMercado, idFornecedor);
    }

    /* Persiste o fornecedor */
    public String SaveOrUpdate(Mercado mercado, Fornecedor fornecedor) throws Exception {
        return (((FornecedorDao) this.getDao()).saveOrUpdate(mercado, fornecedor) == RetornoPersistencia.OK ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }

    /* Persiste relacao mercado - fornecedor */
    public String SalvarMercForn(Mercado mercado, Fornecedor fornecedor) throws Exception {
        return new MercFornDao(this.getUsuarioLogado()).save(mercado, fornecedor);
    }

    public Fornecedor consultaMercForn(Mercado mercado, Fornecedor fornecedor) {
        /* Cnpj é uma string porque ele vem com a mascara */
        return new MercFornDao(this.getUsuarioLogado()).locateByMercado(mercado, fornecedor);
    }

    public String excluiMercForn(Long idMercado, Long idFornecedor) throws Exception {        
        return (new MercFornDao(this.getUsuarioLogado()).delete(idMercado, idFornecedor) == RetornoPersistencia.OK ? MSGDELETEOK : MSGDELETENAOOK);
    }
}
