package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Vendedor;
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

public class VendedorDao extends BaseDao {

    public VendedorDao(UsuarioLogado usuarioLogado) {
        super("select distinct(c.id), c.nome, c.datacadastro, c.fonecomercial, c.email, c.situacao, v.foneresidencial, v.fonecelular, v.cpf " +
              "from vendedor v inner join cadastrao c on c.id = v.fkidcadastrao ",
              "select c.id, c.nome, c.datacadastro, c.fonecomercial, c.email, c.situacao, v.foneresidencial, v.fonecelular, v.cpf " +
              "from vendedor v inner join cadastrao c on c.id = v.fkidcadastrao where c.id = ?",
              "INSERT INTO vendedor(fkidcadastrao, foneresidencial, fonecelular, cpf)  VALUES(?,?,?,?)",
              "update vendedor set foneresidencial=?, fonecelular=?, cpf=? where fkidcadastrao=?",
              "sqlExcluir",
              usuarioLogado);
    }

    public List<Vendedor> listarMercFornVendByFornecedor(Long idFornecedor, Long idMercado) {
        List<Vendedor> lista = new ArrayList<Vendedor>();
        Connection conn = null;

        try {
            conn = Conexao.getC();

            String sql = this.getSqlListar();
            sql += " inner join fornvend fv on fv.fkidvendedor = v.fkidcadastrao inner join mercfornvend mfv on mfv.fkidfornvend = fv.id where fv.fkidfornecedor = ? and mfv.fkidmercado = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, idFornecedor);
            ps.setLong(2, idMercado);

            rs = ps.executeQuery();

            while (rs.next()) {
                Vendedor vendedor = new Vendedor(conn, this.getUsuarioLogado(), rs);
                lista.add(vendedor);
            }
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return lista;
    }

    public List<Vendedor> listarFornVendByFornecedor(Long idFornecedor) {
        List<Vendedor> lista = new ArrayList<Vendedor>();
        Connection conn = null;

        try {
            conn = Conexao.getC();

            String sql = this.getSqlListar();
            sql += " inner join fornvend fv on fv.fkidvendedor = v.fkidcadastrao where fv.fkidfornecedor = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, idFornecedor);            

            rs = ps.executeQuery();

            while (rs.next()) {
                Vendedor vendedor = new Vendedor(conn, this.getUsuarioLogado(), rs);
                lista.add(vendedor);
            }
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws Exception {
        List <BaseBean> vendedores = new ArrayList<BaseBean>();
        int qtdRegistros;

        try {
            String sql = this.getSqlListar();            

            if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
                sql += " inner join fornvend fv on fv.fkidvendedor = v.fkidcadastrao where fv.fkidfornecedor between ? and ? ";
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME") || this.getUsuarioLogado().getUsuario().getTipo().equals("AD")) {
                sql += " inner join fornvend fv on fv.fkidvendedor = v.fkidcadastrao inner join mercfornvend mfv on mfv.fkidfornvend = fv.id and mfv.fkidmercado between ? and ? where c.id = c.id ";
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("VE")) {
                sql += " where c.id between ? and ? ";
            } else {
                throw new Exception("Usuário não pode visualizar vendedores!");
            }

            if (filtro == Filtros.NOME.ordinal()) {
                ps = conn.prepareStatement(sql + " and c.nome like ? order by c.nome");
                ps.setString(1, params[0]);
                ps.setString(2, params[1]);
                ps.setString(3,  "%" + params[2] + "%");
            } else {                
                ps = conn.prepareStatement(sql.concat("order by c.nome"));
                ps.setString(1, params[0]);
                ps.setString(2, params[1]);
            }

            rs = ps.executeQuery();

            if (numini <= 1) {
                rs.beforeFirst();
            } else {
                rs.absolute(numini - 1);
            }

            while ((rs.next()) && (rs.getRow() <= numfim)) {
                Fornecedor fornecedor = new Fornecedor();

                if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")){
                    fornecedor = (Fornecedor) new FornecedorDao(this.getUsuarioLogado()).locate(this.getUsuarioLogado().getUsuario().getId());
                }

                Vendedor vendedor = new Vendedor(conn, this.getUsuarioLogado(), rs, fornecedor);
                vendedores.add(vendedor);
            }

            rs.last();
            qtdRegistros = rs.getRow();        
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return new Listagem(qtdRegistros, vendedores);
    }

    @Override
    public BaseBean locate(Connection conn, Long id) throws Exception {
        Vendedor vendedor = null;

        try {
            ps = conn.prepareStatement(this.getSqlLocate());
            ps.setLong(1, id);            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                vendedor = new Vendedor(conn, this.getUsuarioLogado(), rs);
            }
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return vendedor;
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws Exception {
        Long fkIdCadastrao = new CadastraoDao(this.getUsuarioLogado()).save(conn, bean);

        Vendedor vendedor = (Vendedor) bean;
        ps = conn.prepareStatement(this.getSqlInserir());
		
        try {
            vendedor.setId(fkIdCadastrao);

            ps.setLong(1, vendedor.getId());
            ps.setString(2, vendedor.getFoneResidencial());
            ps.setString(3, vendedor.getFoneCelular());
            ps.setString(4, vendedor.getCpf());

            ps.executeUpdate();

            String loginSenha = vendedor.getCpf().replace(".","").replace("/", "").replace("-", "");
            Usuario usuario = new Usuario(vendedor, loginSenha, loginSenha, vendedor.getNome(), vendedor.getEmail(), "VE", "A", Functions.getPermissoesVendedor(),"N", "", null);

            new UsuarioDao(this.getUsuarioLogado()).save(conn, usuario);

            new Thread(new LogInfoThread(vendedor, this.getUsuarioLogado())).start();
        } finally {
            Conexao.closeC(null, ps, null);
        }

        return vendedor.getId();
    }

    @Override
    public Long update(Connection conn, BaseBean bean) throws Exception {
        Long fkIdCadastrao = new CadastraoDao(this.getUsuarioLogado()).update(conn, bean);

        Vendedor vendedor = (Vendedor) bean;
        Vendedor vendedorOld = (Vendedor) this.locate(conn, vendedor.getId());
        
        ps = conn.prepareStatement(this.getSqlAlterar());
		
        try {
            vendedor.setId(fkIdCadastrao);

            ps.setString(1, vendedor.getFoneResidencial());
            ps.setString(2, vendedor.getFoneCelular());
            ps.setString(3, vendedor.getCpf());
            ps.setLong(4, vendedor.getId());

            ps.executeUpdate();

            new Thread(new LogInfoThread(vendedorOld, vendedor, this.getUsuarioLogado())).start();
        } finally {
            Conexao.closeC(null, ps, null);
        }

        return vendedor.getId();
    }

    public RetornoPersistencia saveOrUpdate(Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        if (vendedor.getId() <= 0) {
            return this.save(mercado, fornecedor, vendedor);
        } else {
            return this.update(mercado, fornecedor, vendedor);
        }
    }

    private void save(Connection conn, Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        // Salva o vendedor
        vendedor.setId(this.save(conn, vendedor));

        // Salva a relacao mercado - fornecedor - vendedor
        new MercFornVendDao(this.getUsuarioLogado()).save(conn, mercado, fornecedor, vendedor);
    }

    private RetornoPersistencia save(Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        Connection conn = null;
		
        try {
            conn = Conexao.getC();

            this.save(conn, mercado, fornecedor, vendedor);

            conn.commit();
            
            return RetornoPersistencia.OK;
        } catch (Exception e) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            //tratamento para Exception            
            Functions.TratarExcecoes(vendedor, e.getMessage());
            
            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }

    private void update(Connection conn, Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        // Salva o vendedor
        vendedor.setId(this.update(conn, vendedor));

        // Salva a relacao mercado - fornecedor - vendedor
        new MercFornVendDao(this.getUsuarioLogado()).save(conn, mercado, fornecedor, vendedor);
    }

    private RetornoPersistencia update(Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        Connection conn = null;
		
        try {
            conn = Conexao.getC();

            this.update(conn, mercado, fornecedor, vendedor);

            conn.commit();

            return RetornoPersistencia.OK;
        } catch (Exception e) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            //tratamento para Exception
            //colocar aqui outros exceptions
            Functions.TratarExcecoes(vendedor, e.getMessage());

            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }

    public RetornoPersistencia saveOrUpdate(Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        if (vendedor.getId() <= 0) {
            return this.save(fornecedor, vendedor);
        } else {
            return this.update(fornecedor, vendedor);
        }
    }

    private void save(Connection conn, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        // Salva o vendedor
        vendedor.setId(this.save(conn, vendedor));

        // Salva a relacao fornecedor - vendedor
        new FornVendDao(this.getUsuarioLogado()).save(conn, fornecedor, vendedor);
    }

    private RetornoPersistencia save(Fornecedor fornecedor, Vendedor vendedor) throws SGExceptions {
        Connection conn = null;

        try {
            conn = Conexao.getC();

            this.save(conn, fornecedor, vendedor);

            conn.commit();

            return RetornoPersistencia.OK;
        /*} catch (SQLException ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            //tratamento para SQLException
            if (ex != null && ex.getMessage().contains("Duplicate entry")) {
                throw new SGDuplicateKeyExceptions("CPF: " + vendedor.getCpf() + " ");
            } else {
                //colocar aqui outros exceptions
                throw new SGExceptions("excecao SQL: " + ex);
            }*/
        } catch (Exception e) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            //tratamento para Exception
            Functions.TratarExcecoes(vendedor, e.getMessage());

            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }

    private void update(Connection conn, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
        // Salva o vendedor
        vendedor.setId(this.update(conn, vendedor));

        // Salva a relacao fornecedor - vendedor
        new FornVendDao(this.getUsuarioLogado()).save(conn, fornecedor, vendedor);
    }

    private RetornoPersistencia update(Fornecedor fornecedor, Vendedor vendedor) throws SGExceptions {
        Connection conn = null;

        try {
            conn = Conexao.getC();

            this.update(conn, fornecedor, vendedor);

            conn.commit();

            return RetornoPersistencia.OK;
        } catch (Exception ex) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            //tratamento para Exception
            Functions.TratarExcecoes(vendedor, ex.getMessage());

            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }

    public Vendedor locateByCpf(String cpf) {
        Vendedor vendedor = null;
        Connection conn = null;

        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement("select c.id, c.nome, c.datacadastro, c.fonecomercial, c.email, c.situacao, v.foneresidencial, v.fonecelular, v.cpf " +
                                       "from vendedor v inner join cadastrao c on c.id = v.fkidcadastrao where v.cpf = ?");
            ps.setString(1, cpf);
            rs = ps.executeQuery();

            if (rs.next()) {
                vendedor = new Vendedor(conn, this.getUsuarioLogado(), rs);
            }
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            vendedor = null;
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return vendedor;
    }

    public Vendedor locateByFornecedor(Long idVendedor, Long idFornecedor) throws Exception {
        Vendedor vendedor = null;
        Connection conn = null;

        try {
            conn = Conexao.getC();

            String sql =  "select c.id, c.nome, c.datacadastro, c.fonecomercial, " +
                    "c.email, fv.situacao, v.foneresidencial, v.fonecelular, " +
                    "v.cpf " +
              "from vendedor v inner join cadastrao c on c.id = v.fkidcadastrao " +
              "inner join fornvend fv on fv.fkidvendedor = c.id where c.id = ? "+
              "and fv.fkidfornecedor = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, idVendedor);
            ps.setLong(2, idFornecedor);

            rs = ps.executeQuery();

            if (rs.next()) {
                vendedor = new Vendedor(conn, this.getUsuarioLogado(), rs);
            }
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return vendedor;
    }

}
