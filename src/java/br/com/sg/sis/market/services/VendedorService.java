package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Vendedor;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.FornVendDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.MercFornVendDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.VendedorDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */

public class VendedorService extends ServiceOperations {

    public VendedorService() throws SGExceptions {
        this.setDao(new VendedorDao(this.getUsuarioLogado()));
    }

    public Vendedor LocateByCpf(String cpf) throws Exception {
        VendedorDao dao = (VendedorDao) this.getDao();
        return dao.locateByCpf(cpf);
    }
    
    public Vendedor LocateByFornecedor(Long idVendedor, Long idFornecedor) throws Exception {
        VendedorDao dao = (VendedorDao) this.getDao();
        return dao.locateByFornecedor(idVendedor, idFornecedor);
    }
    
    public String SaveOrUpdate(Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        VendedorDao dao = (VendedorDao) this.getDao();
        return ((dao.saveOrUpdate(mercado, fornecedor, vendedor) == RetornoPersistencia.OK) ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }

    public String SaveOrUpdate(Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        VendedorDao dao = (VendedorDao) this.getDao();
        return ((dao.saveOrUpdate(fornecedor, vendedor) == RetornoPersistencia.OK) ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }

    public boolean ConsultaFornVend(Fornecedor fornecedor, Vendedor vendedor) {
        return new FornVendDao(this.getUsuarioLogado()).consulta(fornecedor, vendedor) > 0;
    }

    public boolean ConsultaMercFornVend(Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws SGExceptions {
        return new MercFornVendDao(this.getUsuarioLogado()).consulta(mercado, fornecedor, vendedor) > 0;
    }

    public String ExcluirMercFornVend(Long idMercado, Long idVendedor) throws Exception {
        return ((new MercFornVendDao(this.getUsuarioLogado()).delete(idMercado, idVendedor) == RetornoPersistencia.OK) ? MSGDELETEOK : MSGDELETENAOOK);
    }

    public String ExcluirFornVend(Long idFornecedor, Long idVendedor) throws Exception {
        return ((new FornVendDao(this.getUsuarioLogado()).delete(idFornecedor, idVendedor) == RetornoPersistencia.OK) ? MSGDELETEOK : MSGDELETENAOOK);
    }

    public List<Vendedor> ListarMercFornVendByFornecedor(Long idFornecedor, Long idMercado) {
        VendedorDao dao = (VendedorDao) this.getDao();
        return dao.listarMercFornVendByFornecedor(idFornecedor, idMercado);
    }
    
    public List<Vendedor> ListarFornVendByFornecedor(Long idFornecedor) {
        VendedorDao dao = (VendedorDao) this.getDao();
        return dao.listarFornVendByFornecedor(idFornecedor);
    }
    
}