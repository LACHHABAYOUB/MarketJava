package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.ItensCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.ItensCotacaoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lachhab Ayoub
 */
public class ItensCotacaoService extends ServiceOperations {

    public ItensCotacaoService() throws SGExceptions {
        this.setDao(new ItensCotacaoDao(this.getUsuarioLogado()));
    }

    public String SalvaPrazoCotacao(BaseBean itemCotacao) throws SGExceptions, Exception {
        return ((((ItensCotacaoDao) this.getDao()).salvaPrazoCotacao(itemCotacao) == RetornoPersistencia.OK) ? "Prazo salvo com sucesso!" : "Não foi possível salvar o prazo.");
    }

    public String SalvaPrazoCotacaoAgrupado(BaseBean itemCotacao, Map cotacoes) throws SGExceptions, Exception {
        return ((((ItensCotacaoDao) this.getDao()).salvaPrazoCotacaoAgrupado(itemCotacao, cotacoes) == RetornoPersistencia.OK) ? "Prazo salvo com sucesso!" : "Não foi possível salvar o prazo.");
    }

    public Listagem ListarAgrupado(Integer filtro, Integer numini, Integer numfim, Map cotacoes) throws SGExceptions {
        return ((((ItensCotacaoDao) this.getDao()).listar(filtro, numini, numfim, cotacoes)));
    }
    //update para cotacoes agrupado

    public String UpdateAgrupado(BaseBean itemCotacao, Map cotacoes) throws SGExceptions, Exception {
        return ((((ItensCotacaoDao) this.getDao()).update(itemCotacao, cotacoes) == RetornoPersistencia.OK) ? "Item salvo com sucesso!" : null);
    }

    public RetornoPersistencia salvarItensCotacaoUpdateAgrupado(List<ItensCotacao> itensCotacao, Map cotacoes) throws Exception {
        Connection conn = null;
        try {
            conn = Conexao.getC();
            for (ItensCotacao item : itensCotacao) {
                ((ItensCotacaoDao) this.getDao()).updateConn(item, cotacoes, conn);
            }
            conn.commit();
        } catch (Exception e) {
            Conexao.rollback(conn);
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            throw e;
        } finally {
            Conexao.closeC(conn, null, null);
        }

        return RetornoPersistencia.OK;
    }

    public Listagem ListarPrePedido(Integer numini, Integer numfim, String[] params) throws SGExceptions {
        return ((((ItensCotacaoDao) this.getDao()).listarPrePedido(numini, numfim, params)));
    }

    public String AlterarItemGanho(BaseBean itemGanho) throws SGExceptions, Exception {
        return ((((ItensCotacaoDao) this.getDao()).alterarItemGanho(itemGanho) == RetornoPersistencia.OK) ? "Item ganho alterado com sucesso!" : null);
    }

    public String AlterarQtde(BaseBean item) throws SGExceptions, Exception {
        return ((((ItensCotacaoDao) this.getDao()).alterarQtde(item) == RetornoPersistencia.OK) ? "Item alterado com sucesso!" : null);
    }

    public Listagem ListarPorFilial(Integer numini, Integer numfim, String[] params, Map cotacoes, BaseBean cotacao) throws SGExceptions, Exception {
        return ((ItensCotacaoDao) this.getDao()).listarPorFilial(numini, numfim, params, cotacoes, cotacao);
    }

    /* insere item de cotacao */
    public String SalvarItemCotacao(ItensCotacao itensCotacao, Map cotacoes) throws Exception {
        return (((ItensCotacaoDao) this.getDao()).salvaItemCotacao(itensCotacao, cotacoes) == RetornoPersistencia.OK ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }

    public RetornoPersistencia salvarItensCotacao(List<ItensCotacao> itensCotacao) throws Exception {
        Connection conn = null;
        try {
            conn = Conexao.getC();
            for (ItensCotacao item : itensCotacao) {
                ((ItensCotacaoDao) this.getDao()).atualizar(conn, item);
            }
            conn.commit();
        } catch (Exception e) {
            Conexao.rollback(conn);
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            throw e;
        } finally {
            Conexao.closeC(conn, null, null);
        }

        return RetornoPersistencia.OK;
    }

}
