package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Filtros;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class FornecedorDao extends BaseDao {

    public FornecedorDao(UsuarioLogado usuarioLogado) {
        super("select c.id, c.nome, c.fonecomercial, c.datacadastro, c.email, c.situacao, f.fantasia, f.contato, f.cnpj from fornecedor f inner join cadastrao c on c.id = f.fkidcadastrao ",
              "select c.id, c.nome, c.fonecomercial, c.datacadastro, c.email, c.situacao, f.fantasia, f.contato, f.cnpj from fornecedor f inner join cadastrao c on c.id = f.fkidcadastrao where f.fkidcadastrao = ? ",
              "insert into fornecedor (fkidcadastrao, fantasia, contato, cnpj) values (?,?,?,?)",
              "update fornecedor f set f.fantasia = ?, f.contato = ? where f.fkidcadastrao = ?",
              "delete from mercforn where fkidmercado = ? and fkidfornecedor = ?",
              usuarioLogado);
    }

    @Override
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws SGExceptions {
        List<BaseBean> fornecedores = new ArrayList<BaseBean>();
        int qtdRegistros;

        String sql = this.getSqlListar();

        if (this.getUsuarioLogado() != null){
            /*Efetua a filtragem de acordo com o tipo de usuario logado*/
            if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")){
                sql += " where f.fkidcadastrao between ? and ?";
            }else if (this.getUsuarioLogado().getUsuario().getTipo().equals("VE")){
                sql += " inner join fornvend fv on f.fkidcadastrao = fv.fkidfornecedor where fv.fkidvendedor between ? and ?";
            }else if(this.getUsuarioLogado().getUsuario().getTipo().equals("ME") || this.getUsuarioLogado().getUsuario().getTipo().equals("AD")){
                sql += " inner join mercforn mf on f.fkidcadastrao = mf.fkidfornecedor where mf.fkidmercado between ? and ?";
            }
        }

        try {
            if (filtro == Filtros.DESCRICAO.ordinal()) {
                ps = conn.prepareStatement(sql + " and c.nome like ? order by c.nome");
                ps.setString(3,  "%" +params[2] + "%");
            } else if (filtro == Filtros.NOMEFANTASIA.ordinal()) {
                ps = conn.prepareStatement(sql + " and f.fantasia like ? order by f.fantasia");
                ps.setString(3,  "%" +params[2] + "%");
            } else if (filtro == Filtros.CNPJ.ordinal()) {
                ps = conn.prepareStatement(sql + " and f.cnpj like ? order by c.nome");
                ps.setString(3,  "%" +params[2] + "%");
            }/* else if (filtro == Filtros.MERCADO.ordinal()) {
                ps = conn.prepareStatement(sql + " and inner join mercforn mf on mf.fkidfornecedor = c.id where mf.fkidmercado = ?");
                ps.setLong(2, Long.parseLong(params[0]));
            }*/ else {
                ps = conn.prepareStatement(sql + " order by c.nome");
            }

            /*Seta o id do mercado ou fornecedor, dependendo do usuario logado*/
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
                Fornecedor fornecedor = new Fornecedor(conn, this.getUsuarioLogado(), rs);
                fornecedores.add(fornecedor);
            }

            rs.last();
            qtdRegistros = rs.getRow();
        } catch(Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            Functions.TratarExcecoes(null, e.getMessage());
            
            return new Listagem(0, null);
        } finally {
            Conexao.closeC(null, ps, rs);
        }
        
        return new Listagem(qtdRegistros, fornecedores);
    }

    @Override
    public BaseBean locate(Connection conn, Long id) throws Exception {
        Fornecedor fornecedor = null;

        ps = conn.prepareStatement(this.getSqlLocate());
        ps.setLong(1, id);

        rs = ps.executeQuery();

        if (rs.next()){
            fornecedor = new Fornecedor(conn, this.getUsuarioLogado(), rs);
        }

        return fornecedor;
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long update(Connection conn, BaseBean bean) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RetornoPersistencia saveOrUpdate (Mercado mercado, Fornecedor fornecedor) throws Exception {
        if(fornecedor.getId() <= 0) {
            return this.save(mercado,fornecedor);
        } else {
            if (mercado != null){
                return this.update(mercado, fornecedor);
            }else{
                return this.update(fornecedor);
            }
        }
    }

    private RetornoPersistencia save(Mercado mercado, Fornecedor fornecedor) throws SGExceptions {
        Connection conn = null;

        try {
            conn = Conexao.getC();

            Long fkIdCadastrao = new CadastraoDao(this.getUsuarioLogado()).save(conn,fornecedor);

            ps = conn.prepareStatement(this.getSqlInserir());
            ps.setLong(1, fkIdCadastrao);
            ps.setString(2, fornecedor.getFantasia());
            ps.setString(3, fornecedor.getContato());
            ps.setString(4, fornecedor.getCnpj());

            ps.executeUpdate();

            fornecedor.setId(fkIdCadastrao);

            new Thread(new LogInfoThread(fornecedor, this.getUsuarioLogado())).start();

            /*Salva a relacao mercado - fornecedor*/
            new MercFornDao(this.getUsuarioLogado()).save(conn, mercado, fornecedor);

            /*Cria o usuario para o fornecedor*/
            String loginSenha = fornecedor.getCnpj().replace(".","").replace("/", "").replace("-", "");
            Usuario usuario = new Usuario(fornecedor, loginSenha, loginSenha, fornecedor.getNome(), fornecedor.getEmail(), "FO", "A", Functions.getPermissoesFornecedor(),"N", "", null);
            new UsuarioDao(this.getUsuarioLogado()).save(conn, usuario);

            conn.commit();

            return RetornoPersistencia.OK;
        } catch (Exception ex) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            Functions.TratarExcecoes(fornecedor, ex.getMessage());
            
            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, ps, null);
        }
    }

    private RetornoPersistencia update(Fornecedor fornecedor) throws Exception {
        Connection conn = null;

        try {
            conn = Conexao.getC();

            Fornecedor fornecedorOld = (Fornecedor) this.locate(conn, fornecedor.getId());

            Long fkidcadastrao  = new CadastraoDao(this.getUsuarioLogado()).update(conn, fornecedor);
            
            ps = conn.prepareStatement(this.getSqlAlterar());
            ps.setString(1, fornecedor.getFantasia());
            ps.setString(2, fornecedor.getContato());
            ps.setLong(3, fkidcadastrao);

            /* executa a insercao */
            ps.executeUpdate();

            conn.commit();

            new Thread(new LogInfoThread(fornecedorOld, fornecedor, this.getUsuarioLogado())).start();

            return RetornoPersistencia.OK;
        } catch (Exception ex) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            Functions.TratarExcecoes(fornecedor, ex.getMessage());

            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, ps, null);
        }
    }

    private RetornoPersistencia update(Mercado mercado , Fornecedor fornecedor) throws Exception {
        Connection conn = null;
        RetornoPersistencia retorno;
		
        try {
            conn = Conexao.getC();
            
            retorno = this.update(fornecedor);

            new MercFornDao(this.getUsuarioLogado()).save(conn, mercado, fornecedor);

            conn.commit();
        } catch (Exception ex) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            Functions.TratarExcecoes(fornecedor, ex.getMessage());
            
            retorno = RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return retorno;
    }

    public Fornecedor locateByCnpj(String cnpj) throws SGExceptions {
        Fornecedor fornecedor = null;
        Connection conn = null;

        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement("select c.id, c.nome, c.fonecomercial, c.datacadastro, c.email, c.situacao, f.fantasia, f.contato, f.cnpj from fornecedor f inner join cadastrao c on c.id = f.fkidcadastrao where f.cnpj = ?");
            ps.setString(1, cnpj);

            rs = ps.executeQuery();

            if (rs.next()){
                fornecedor = new Fornecedor(conn, this.getUsuarioLogado(), rs);
            }
        } catch (Exception ex) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            fornecedor = null;
            Functions.TratarExcecoes(fornecedor, ex.getMessage());
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return fornecedor;
    }

    public Fornecedor locateByMercado (Long idMercado, Long idFornecedor) throws SGExceptions {
        Fornecedor fornecedor = null;
        Connection conn = null;

        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement("select c.id, c.nome, c.fonecomercial, c.datacadastro, c.email, mf.situacao, f.fantasia, f.contato, f.cnpj " +
                    "from fornecedor f " +
                    "inner join cadastrao c on c.id = f.fkidcadastrao  " +
                    "inner join mercforn mf on mf.fkidfornecedor = c.id " +
                    "where f.fkidcadastrao = ? and mf.fkidmercado = ?");
            ps.setLong(1, idFornecedor);
            ps.setLong(2, idMercado);

            rs = ps.executeQuery();
            
            if(rs.next()){
                fornecedor = new Fornecedor(conn, this.getUsuarioLogado(), rs);
            }
        } catch (Exception e) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            fornecedor = null;
            Functions.TratarExcecoes(fornecedor, e.getMessage());
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return fornecedor;
    }
}
