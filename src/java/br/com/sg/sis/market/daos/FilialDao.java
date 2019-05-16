package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Filtros;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Filial;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eduardo Lachhab Ayoub
 */
public class FilialDao extends BaseDao {

    public FilialDao(UsuarioLogado usuarioLogado) {
        super(//"select * from filial f inner join cadastrao c on c.id = f.fkidcadastrao ",

                "select c.id, c.nome, c.datacadastro, c.fonecomercial, c.email, c.situacao, f.fkidmercado, f.codfilial, f.cnpj, f.contato from filial f inner join cadastrao c on c.id = f.fkidcadastrao ",
                "select c.id, c.nome, c.datacadastro, c.fonecomercial, c.email, c.situacao, f.fkidmercado, f.codfilial, f.cnpj, f.contato from filial f inner join cadastrao c on c.id = f.fkidcadastrao where c.id = ?",
                "insert into filial values (?, ?, ?, ?, ?)",
                "update filial set codfilial = ?, cnpj = ?, contato = ? where fkidcadastrao = ?",
                "delete from cadastrao where id = ?",
                usuarioLogado);
    }

    @Override
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) {
        List<BaseBean> filiais = new ArrayList<BaseBean>();
        int qtdRegistros;

        try {
            if (filtro == Filtros.DESCRICAO.ordinal()) {
                ps = conn.prepareStatement(this.getSqlListar() + "where f.fkidmercado between ? and ? and c.nome like ? order by f.codfilial");
                ps.setString(3, "%" + params[2] + "%");
            } else if (filtro == Filtros.CODFILIAL.ordinal()) {
                ps = conn.prepareStatement(this.getSqlListar() + "where f.fkidmercado between ? and ? and f.codfilial between ? and ? order by f.codfilial");
                ps.setInt(3, Integer.parseInt(params[2]));
                ps.setInt(4, Integer.parseInt(params[3]));
            } else if (filtro == Filtros.CIDADE.ordinal()) {
                ps = conn.prepareStatement(this.getSqlListar() + " inner join endereco e on c.id = e.fkidcadastrao where f.fkidmercado between ? and ? and e.cidade like ? order by f.codfilial");
                ps.setString(3, "%" + params[2] + "%");
            } else {
                ps = conn.prepareStatement(this.getSqlListar() + " where f.fkidmercado between ? and ? order by f.codfilial");
            }

            ps.setString(1, params[0]);
            ps.setString(2, params[1]);

            rs = ps.executeQuery();

            if (numini <= 1) {
                //primeira pagina
                rs.beforeFirst();
            } else {
                rs.absolute(numini - 1);
            }

            while ((rs.next()) && (rs.getRow() <= numfim)) {
                Filial filial = new Filial(conn, this.getUsuarioLogado(), rs);
                filiais.add(filial);
            }

            rs.last();
            qtdRegistros = rs.getRow();
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);

            return new Listagem(0, null);
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return new Listagem(qtdRegistros, filiais);
    }

    @Override
    public BaseBean locate(Connection conn, Long id) {
        Filial filial = null;

        try {
            ps = conn.prepareStatement(this.getSqlLocate());
            ps.setLong(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                filial = new Filial(conn, this.getUsuarioLogado(), rs);
            }
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
        } finally {
            Conexao.closeC(null, ps, rs);
        }
        return filial;
    }

    public Boolean locateByCnpj(String cnpj) {
        Connection conn = null;

        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement("select * from filial where cnpj = ?");
            ps.setString(1, cnpj);

            rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (Exception ex) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return false;
    }

    public RetornoPersistencia saveOrUpdate(Connection conn, BaseBean filial, Mercado mercado) throws Exception {
        if (filial.getId() <= 0) {
            return this.save(conn, filial, mercado);
        } else {
            this.update(conn, filial);
            return RetornoPersistencia.OK;
        }
    }

    public RetornoPersistencia saveOrUpdate(BaseBean filial, Mercado mercado) throws Exception {
        if (filial.getId() <= 0) {
            return this.save(filial, mercado);
        } else {
            return this.update(filial);

        }
    }

    @Override
    public Long update(Connection conn, BaseBean bean) throws Exception {
        Filial filial = (Filial) bean;
        Filial filialOld = (Filial) this.locate(conn, filial.getId());

        Long fkidcadastrao = new CadastraoDao(this.getUsuarioLogado()).update(conn, bean);

        ps = conn.prepareStatement(this.getSqlAlterar());
        ps.setInt(1, ((Filial) bean).getCodFilial());
        ps.setString(2, ((Filial) bean).getCnpj());
        ps.setString(3, ((Filial) bean).getContato());
        ps.setLong(4, fkidcadastrao);

        /* executa a alteracao */
        ps.executeUpdate();

        new Thread(new LogInfoThread(filialOld, filial, this.getUsuarioLogado())).start();

        return fkidcadastrao;
    }

    private RetornoPersistencia save(Connection conn, BaseBean bean, Mercado mercado) throws SGExceptions, Exception {
        try {
            Filial filial = (Filial) bean;


            Long fkidcadastrao = new CadastraoDao(this.getUsuarioLogado()).save(conn, bean);

            ps = conn.prepareStatement(this.getSqlInserir());
            ps.setLong(1, fkidcadastrao);
            ps.setLong(2, mercado.getId());
            ps.setInt(3, filial.getCodFilial());
            ps.setString(4, filial.getCnpj());
            ps.setString(5, filial.getContato());

            /* executa a insercao */
            ps.executeUpdate();

            //verifica se foi inserido pelo mercado
            String loginSenha = ((Filial) bean).getCnpj().trim().replace(".", "").replace("-", "").replace("/", "");
            Usuario retorno = new UsuarioDao(this.getUsuarioLogado()).locate(conn, loginSenha, loginSenha);

            if (retorno == null) {
                bean.setId(fkidcadastrao);
                Usuario usuario = new Usuario(((Filial) bean), loginSenha, loginSenha, ((Filial) bean).getNome(), ((Filial) bean).getEmail(), "FI", "A", Functions.getPermissoesFilial(), "N", "", null);
                new UsuarioDao(this.getUsuarioLogado()).save(conn, usuario);
            }


            new Thread(new LogInfoThread(filial, this.getUsuarioLogado())).start();

            return RetornoPersistencia.OK;
        } finally {
            Conexao.closeC(null, ps, rs);
        }
    }

    private RetornoPersistencia save(BaseBean bean, Mercado mercado) throws SGExceptions, Exception {
        Connection conn = null;
		RetornoPersistencia rp = null;
        try {
            conn = Conexao.getC();
            rp = this.save(conn, bean, mercado);
            conn.commit();

            return rp;
        } catch (Exception ex) {
            Conexao.rollback(conn);
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            ex.printStackTrace();
            throw ex;
        } finally {
            Conexao.closeC(conn, ps, rs);
        }
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
    public RetornoPersistencia excluir(Long id) throws SGExceptions{
        try {
            conn = Conexao.getC();
            this.delete(conn, id);
            conn.commit();
            return RetornoPersistencia.OK;
        } catch (Exception ex) {
            Conexao.rollback(conn);
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            Functions.TratarExcecoes(new Filial(), ex.getMessage());
            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }


}
