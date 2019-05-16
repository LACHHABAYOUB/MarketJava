package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.BaseDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import flex.messaging.FlexContext;
import flex.messaging.FlexSession;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Lachhab Ayoub
 */
public abstract class ServiceOperations {
    private FlexSession session;
    private HttpServletRequest request;
    UsuarioLogado usuarioLogado;

    private BaseDao dao;

    public static String MSGSAVEUPOK = "Informações Salvas com Sucesso!!!";
    public static String MSGDELETEOK = "Informações Excluídas com Sucesso!!!";
    public static String MSGSAVEUPNAOOK = "Não foi possível Salvar Informações!!!";
    public static String MSGDELETENAOOK = "Não foi possível Excluir Informações!!!";
    public static String MSGSESSAONAOOK = "SessaoExpirada";
    public static String MSGGERACAOPREPEDIDO = "Pré-pedido Gerado com sucesso!!!";
    public static String MSGGERACAOPREPEDIDONAOOK = "Não foi possível Gerar pré-pedido!!!";
	    
    public ServiceOperations() throws SGExceptions {
        this.session = FlexContext.getFlexSession();
        this.request = FlexContext.getHttpRequest();
        this.usuarioLogado = (UsuarioLogado) this.session.getAttribute("usuariologado");

        if (!(this instanceof LoginService) && !(this instanceof RecuperaSenhaService) && !(this instanceof MensagemService) && this.usuarioLogado == null){
            throw new SGExceptions(MSGSESSAONAOOK);
    }   }

    public Listagem Listar(Integer filtro, Integer numini, Integer numfim, String[] params) {
        return getDao().listar(filtro, numini, numfim, params);
    }

    public BaseBean Locate(Long id) {
        return getDao().locate(id);
    }

    public String SaveOrUpdate(BaseBean bean) throws Exception {
        return ((getDao().saveOrUpdate(bean) == RetornoPersistencia.OK) ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }

    public String Excluir(Long id) throws Exception {
        return ((getDao().delete(id) == RetornoPersistencia.OK) ? MSGDELETEOK : MSGDELETENAOOK);
    }

    protected BaseDao getDao() {
        return dao;
    }

    protected FlexSession getSession() {
        return session;
    }

    protected UsuarioLogado getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setDao(BaseDao dao) {
        this.dao = dao;
    }

    /**
     * @return the request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
