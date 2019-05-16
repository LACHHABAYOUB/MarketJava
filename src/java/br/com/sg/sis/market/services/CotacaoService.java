package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Cotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.CotacaoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import java.util.Map;

/**
 *
 * @author Lachhab Ayoub
 */
public class CotacaoService extends ServiceOperations {

    public CotacaoService() throws SGExceptions {
        this.setDao(new CotacaoDao(this.getUsuarioLogado()));
    }

    public String Fechar(Listagem fechar) throws SGExceptions {
        return ((((CotacaoDao) this.getDao()).fechamentoCotacoes(fechar) == RetornoPersistencia.OK) ? "Fechamento das cotações realizados com sucesso!" : "Não foi possível realizar o fechamento das cotações selecionadas.");
    }

    public String Reabrir(Listagem reabrir) throws SGExceptions {
        return ((((CotacaoDao) this.getDao()).reabrirCotacoes(reabrir) == RetornoPersistencia.OK) ? "Reabertura das cotações realizados com sucesso!" : "Não foi possível reabrir as cotações selecionadas.");
    }

    public String ExcluirCotacoes(Listagem excluir) throws SGExceptions {
        return ((((CotacaoDao) this.getDao()).excluiCotacoes(excluir) == RetornoPersistencia.OK) ? MSGDELETEOK : MSGDELETENAOOK);
    }

    public BaseBean LocateRelatorio(String[] params) throws SGExceptions {
        return ((CotacaoDao) this.getDao()).locateRelatorio(params);
    }

	public BaseBean LocateAgrupado(Map cotacoes) throws SGExceptions {
        return ((CotacaoDao) this.getDao()).locateAgrupado(cotacoes);
    }
	
    public BaseBean LocateRelatorioNaoGanhos(String[] params) throws SGExceptions {
        return ((CotacaoDao) this.getDao()).locateRelatorioNaoGanhos(params);
    }

    public Long SalvarObservacao(Cotacao cotacao) throws Exception {
        return ((CotacaoDao) this.getDao()).updateObservacao(cotacao);
    }
	
	/* Persiste a filial */
    public String GerarPrePedido(BaseBean cotacao) throws Exception {
        return (((CotacaoDao) this.getDao()).gerarPrePedido(cotacao) == RetornoPersistencia.OK ? MSGGERACAOPREPEDIDO : MSGGERACAOPREPEDIDONAOOK);
    }
}
