/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.daos.PedidoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import java.sql.SQLException;

/**
 *
 * @author Lachhab Ayoub
 */
public class PedidoService extends ServiceOperations{

    public PedidoService() throws SGExceptions {
        this.setDao(new PedidoDao(this.getUsuarioLogado()));
    }
    public String ExcluirPedido(Listagem excluir) throws SGExceptions, SQLException{
        return ((((PedidoDao)this.getDao()).excluirPedido(excluir) == RetornoPersistencia.OK) ? MSGDELETEOK : MSGDELETENAOOK);
    }
    
    /* Persiste o fornecedor */
    public String SalvarPedido(Listagem pedido) throws SGExceptions, SQLException {
        return (((PedidoDao) this.getDao()).salvarPedido(pedido) == RetornoPersistencia.OK ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }
	
}
