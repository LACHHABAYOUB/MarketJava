package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Lachhab Ayoub
 */
public abstract class BaseDao {
    protected Connection conn;
    protected PreparedStatement ps = null;
    protected ResultSet rs = null;

    private String sqlListar;
    private String sqlLocate;
    private String sqlInserir;
    private String sqlAlterar;
    private String sqlExcluir;
    private UsuarioLogado usuarioLogado;

    public BaseDao(String sqlListar, String sqlLocate, String sqlInserir, String sqlAlterar, String sqlExcluir, UsuarioLogado usuarioLogado) {
        this.sqlListar = sqlListar;
        this.sqlLocate = sqlLocate;
        this.sqlInserir = sqlInserir;
        this.sqlAlterar = sqlAlterar;
        this.sqlExcluir = sqlExcluir;
        this.usuarioLogado = usuarioLogado;
    }

     /**
     *  list the objects from db
     */
    public abstract Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws Exception;
    public Listagem listar(Integer filtro, Integer numini, Integer numfim, String[] params) {
        Listagem retorno = null;

        try {
            conn = Conexao.getC();
            retorno = this.listar(conn, numini, numfim, filtro, params);
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            retorno = new Listagem(0, null);
            ex.printStackTrace();            
        } finally {
            Conexao.closeC(conn, null, null);
        }
        
        return retorno;
    }

    /**
     *  locate the objects from db
     */
    public abstract BaseBean locate(Connection conn, Long id) throws Exception;
    public BaseBean locate(Long id) {
        BaseBean retorno = null;

        try {
            conn = Conexao.getC();
            retorno = this.locate(conn, id);
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            retorno = null;
            ex.printStackTrace();
        } finally {
            Conexao.closeC(conn, null, null);
        }

        return retorno;
    }

    /**
     *  decides if insert or update the object into db
     */
    public RetornoPersistencia saveOrUpdate(BaseBean bean) throws Exception {
        //if(isValid(bean)) {
            if (bean.getId() <= 0) {
                return this.save(bean);
            } else {
                return this.update(bean);
            }
        /*} else {
            throw new SgValidatorException(getMsgErrors());
        }*/
    }

    /**
     *  save object to db
     */
    public abstract Long save(Connection conn, BaseBean bean) throws Exception;
    public RetornoPersistencia save(BaseBean bean) throws Exception {
        try {
            /* cria a conexao com o Banco de Dados */
            conn = Conexao.getC();

            /* Invoca método para Salvar */
            this.save(conn, bean);

            /* finaliza a transação */
            conn.commit();

            /* seta as informações para o resultado */
            return RetornoPersistencia.OK;
        } catch (SQLException ex) {
            /* verifica qual a causa e emite a mensagem */
//            if(ex != null && ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("key 1")) {
                /* seta as informações para o resultado */
//                throw new SgDupEntryException("codigo");
//            } else if(ex != null && ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("key 5")) {
                /* seta as informações para o resultado */
//                throw new SgDupEntryException("cnpj");
//            } else {
                /* seta as informações para o resultado */
//                throw new SgException("Não foi possível Alterar Registro(SQLE): " + ex.getMessage());
//            }
            Conexao.rollback(conn);
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
            /* salva no arquivo de log */
            /* seta as informações para o resultado */
//            throw new SgException("Não foi possível Alterar Registro(E): " + e.getMessage());
            Conexao.rollback(conn);
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            e.printStackTrace();
            throw e;
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }

    /**
     *  update object in db
     */
    public abstract Long update(Connection conn, BaseBean bean) throws Exception;
    
    public RetornoPersistencia atualizar(Connection conn, BaseBean bean) throws Exception{
        try {
            this.update(conn, bean);

            return RetornoPersistencia.OK;
        } catch (SQLException ex) {
	    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
	    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            e.printStackTrace();
            throw e;
        } 
    }
    
    
    public RetornoPersistencia update(BaseBean bean) throws Exception {
        try {
            /* cria a conexao com o Banco de Dados */
            conn = Conexao.getC();

            /* Invoca método para Atualizar */
            this.update(conn, bean);

            /* finaliza a transação */
            conn.commit();

            /* seta as informações para o resultado */
            return RetornoPersistencia.OK;
        } catch (SQLException ex) {
            /* verifica qual a causa e emite a mensagem */
//            if(ex != null && ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("key 1")) {
                /* seta as informações para o resultado */
//                throw new SgDupEntryException("codigo");
//            } else if(ex != null && ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("key 5")) {
                /* seta as informações para o resultado */
//                throw new SgDupEntryException("cnpj");
//            } else {
                /* seta as informações para o resultado */
//                throw new SgException("Não foi possível Alterar Registro(SQLE): " + ex.getMessage());
//            }
            Conexao.rollback(conn);
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
            /* salva no arquivo de log */
            /* seta as informações para o resultado */
//            throw new SgException("Não foi possível Alterar Registro(E): " + e.getMessage());
            Conexao.rollback(conn);
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            e.printStackTrace();
            throw e;
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }

    /**
     *  delete object from db
     */
    public void delete(Connection conn, Long id) throws Exception {
        try {
            ps = conn.prepareStatement(this.getSqlExcluir());
            ps.setLong(1, id);
            /* executa a exclusao */
            ps.executeUpdate();
        } finally {
            Conexao.closeC(null, ps, null);
        }
    }

    public RetornoPersistencia delete(Long id) throws SGExceptions{
        try {
            /* cria a conexao com o Banco de Dados */
            conn = Conexao.getC();

            /* Invoca método para excluir */
            this.delete(conn, id);
            
            /* finaliza a transação */
            conn.commit();
            
            return RetornoPersistencia.OK;
        } catch (Exception ex) {
            /* verifica qual a causa e emite a mensagem */
			Conexao.rollback(conn);
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            BaseBean bean = null;
            try {
                bean = this.locate(conn, id);
            } catch (Exception e){}
            Functions.TratarExcecoes(bean, ex.getMessage());
            return RetornoPersistencia.ERRO;
            
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }

    protected String getSqlAlterar() {
        return sqlAlterar;
    }   

    protected String getSqlExcluir() {
        return sqlExcluir;
    }

    protected String getSqlInserir() {
        return sqlInserir;
    }

    protected String getSqlListar() {
        return sqlListar;
    }

    protected void setSqlListar(String sqlListar) {
        this.sqlListar = sqlListar;
    }

    protected String getSqlLocate() {
        return sqlLocate;
    }

    protected UsuarioLogado getUsuarioLogado() {        
        return usuarioLogado;
    }   
}
