package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Cadastrao;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class CadastraoDao extends BaseDao {

    public CadastraoDao(UsuarioLogado usuarioLogado) {
        super("sqlListar",
                "sqlLocate",
                "INSERT INTO cadastrao(id, nome, datacadastro, fonecomercial, email, situacao) VALUES(null,?,now(),?,?,?)",
                "update cadastrao set nome = ?,fonecomercial = ?, email = ?, situacao= ? where id = ?",
                "sqlExcluir",
                usuarioLogado);
    }

    @Override
    public BaseBean locate(Connection conn, Long fkidcadastrao) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws Exception {
        Cadastrao cadastrao = ((Cadastrao) bean);

        Long id = null;
        ps = conn.prepareStatement(this.getSqlInserir(), PreparedStatement.RETURN_GENERATED_KEYS);
		
        try {
            ps.setString(1, cadastrao.getNome());
            ps.setString(2, cadastrao.getFoneComercial());
            ps.setString(3, cadastrao.getEmail());
            ps.setString(4, cadastrao.getSituacao());

            /* executa a insercao */
            ps.executeUpdate();
			
            ResultSet resultKey = ps.getGeneratedKeys();
            resultKey.next();
			id = resultKey.getLong("GENERATED_KEY");
            
			Conexao.closeC(null, null, resultKey);
        } finally {
            Conexao.closeC(null, ps, null);
        }

        /*Agora salva os dados de endereco*/
        new EnderecoDao(this.getUsuarioLogado(), id).save(conn, cadastrao.getEndereco());

        //new Thread(new LogInfoThread(cadastrao, adicInfo, this.getUsuarioLogado())).start();
        return id;
    }

    @Override
    public Long update(Connection conn, BaseBean bean) throws Exception {
        Cadastrao cadastrao = ((Cadastrao) bean);
//        Cadastrao cadastraoAnt = (Cadastrao) this.locate(conn, cadastrao.getId());
        try {
            ps = conn.prepareStatement(this.getSqlAlterar());
            ps.setString(1, cadastrao.getNome());
            ps.setString(2, cadastrao.getFoneComercial());
            ps.setString(3, cadastrao.getEmail());
            ps.setString(4, cadastrao.getSituacao());
            ps.setLong(5, cadastrao.getId());

            /* executa a alteracao */
            ps.executeUpdate();
        } finally {
            Conexao.closeC(null, ps, null);
        }

        /*Agora salva os dados de endereco*/
        new EnderecoDao(this.getUsuarioLogado(), cadastrao.getId()).update(conn, cadastrao.getEndereco());

        //new Thread(new LogInfoThread(cadastraoAnt, cadastrao, adicInfo, this.getUsuarioLogado())).start();

        return cadastrao.getId();
    }

    @Override
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Usuario> listarUsuariosDisponiveis() throws SGExceptions {
        List<Usuario> usuarios = new ArrayList<Usuario>();
        Connection conn = null;
		
        String sql = "select distinct(cad.id), cad.nome, (case when me.fkidcadastrao is not null then 'ME' " +
                "when fo.fkidcadastrao is not null then 'FO' when fi.fkidcadastrao is not null then 'FI' " +
                "when ve.fkidcadastrao is not null then 'VE' end) as tipo from cadastrao cad " +
                "left outer join mercado me on me.fkidcadastrao = cad.id left outer join fornecedor fo on fo.fkidcadastrao = cad.id " +
                "left outer join filial fi on fi.fkidcadastrao = cad.id left outer join vendedor ve on ve.fkidcadastrao = cad.id " +
                "where ((fi.fkidcadastrao is not null) or (me.fkidcadastrao is not null or fo.fkidcadastrao " +
                "is not null or ve.fkidcadastrao is not null) and (cad.id > 0)) ";

        if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME")) {
            sql += "and cad.id in (select id from (" +
                    "(select fkidcadastrao as 'id' from mercado where fkidcadastrao = ?) union all " +
                    "(select fkidcadastrao as 'id' from filial where fkidmercado = ?) union all " +
                    "(select fkidfornecedor as 'id' from mercforn mf where fkidmercado = ?) union all " +
                    "(select fv.fkidvendedor as 'id' from fornvend fv inner join mercfornvend mfv on mfv.fkidfornvend = fv.id where mfv.fkidmercado = ?) " +
                    ") pessoas) ";
        } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
            sql += "and cad.id in (select id from ( " +
                    "(select fkidcadastrao as 'id' from fornecedor where fkidcadastrao = ?) union all " +
                    "(select fkidvendedor as 'id' from fornvend fv where fkidfornecedor = ?) " +
                    ") pessoas) ";
        } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FI") || this.getUsuarioLogado().getUsuario().getTipo().equals("VE")) {
            sql += "and cad.id in (select id from ( " +
                    "(select id from cadastrao where id = ?)) pessoas) ";
        }

        try {
            conn = Conexao.getC();
			
            if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME")) {
                ps = conn.prepareStatement(sql + "order by cad.nome");
                ps.setLong(1, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
                ps.setLong(2, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
                ps.setLong(3, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
                ps.setLong(4, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
                ps = conn.prepareStatement(sql + "order by cad.nome");
                ps.setLong(1, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
                ps.setLong(2, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FI") || this.getUsuarioLogado().getUsuario().getTipo().equals("VE")) {
                ps = conn.prepareStatement(sql + "order by cad.nome");
                ps.setLong(1, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("AD")) {
                ps = conn.prepareStatement(sql + "order by cad.nome");
            }

            rs = ps.executeQuery();

            while ((rs.next())) {
                // nao e necessario obter tudo
                Usuario usuario = new Usuario();
                Cadastrao cadastrao = new Cadastrao();
                
				cadastrao.setId(rs.getLong("id"));
                cadastrao.setNome(rs.getString("nome"));
                
				usuario.setTipo(rs.getString("tipo"));
                usuario.setCadastrao(cadastrao);
                
				usuarios.add(usuario);
            }
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            Functions.TratarExcecoes(null, ex.getMessage());
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return usuarios;
    }
    public List<Usuario> listarUsuariosDisponiveisLog() throws SGExceptions {
        List<Usuario> usuarios = new ArrayList<Usuario>();
        Connection conn = null;
		
        String sql = "select distinct(cad.id), cad.nome,  usu.id as idUsuario, usu.login, usu.nome as nomeUsuario, (case when me.fkidcadastrao is not null then 'ME' " +
                "when fo.fkidcadastrao is not null then 'FO' when fi.fkidcadastrao is not null then 'FI' " +
                "when ve.fkidcadastrao is not null then 'VE' end) as tipo from cadastrao cad " +
                " inner join usuario usu on usu.fkidcadastrao = cad.id " +
                "left outer join mercado me on me.fkidcadastrao = cad.id left outer join fornecedor fo on fo.fkidcadastrao = cad.id " +
                "left outer join filial fi on fi.fkidcadastrao = cad.id left outer join vendedor ve on ve.fkidcadastrao = cad.id " +
                "where ((fi.fkidcadastrao is null) and (me.fkidcadastrao is not null or fo.fkidcadastrao " +
                "is not null or ve.fkidcadastrao is not null) and (cad.id > 0)) ";

        if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME")) {
            sql += "and cad.id in (select id from (" +
                    "(select fkidcadastrao as 'id' from mercado where fkidcadastrao = ?) union all " +
                    "(select fkidcadastrao as 'id' from filial where fkidmercado = ?) union all " +
                    "(select fkidfornecedor as 'id' from mercforn mf where fkidmercado = ?) union all " +
                    "(select fv.fkidvendedor as 'id' from fornvend fv inner join mercfornvend mfv on mfv.fkidfornvend = fv.id where mfv.fkidmercado = ?) " +
                    ") pessoas) ";
        } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
            sql += "and cad.id in (select id from ( " +
                    "(select fkidcadastrao as 'id' from fornecedor where fkidcadastrao = ?) union all " +
                    "(select fkidvendedor as 'id' from fornvend fv where fkidfornecedor = ?) " +
                    ") pessoas) ";
        } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FI") || this.getUsuarioLogado().getUsuario().getTipo().equals("VE")) {
            sql += "and cad.id in (select id from ( " +
                    "(select id from cadastrao where id = ?)) pessoas) ";
        }

        try {
            conn = Conexao.getC();
			
            if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME")) {
                ps = conn.prepareStatement(sql + "order by cad.nome");
                ps.setLong(1, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
                ps.setLong(2, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
                ps.setLong(3, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
                ps.setLong(4, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
                ps = conn.prepareStatement(sql + "order by cad.nome");
                ps.setLong(1, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
                ps.setLong(2, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FI") || this.getUsuarioLogado().getUsuario().getTipo().equals("VE")) {
                ps = conn.prepareStatement(sql + "order by cad.nome");
                ps.setLong(1, this.getUsuarioLogado().getUsuario().getCadastrao().getId());
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("AD")) {
                ps = conn.prepareStatement(sql + "order by cad.nome");
            }

            rs = ps.executeQuery();

            while ((rs.next())) {
                // nao e necessario obter tudo
                Usuario usuario = new Usuario();
                Cadastrao cadastrao = new Cadastrao();
				
				cadastrao.setId(rs.getLong("id"));
                cadastrao.setNome(rs.getString("nome"));
				
                usuario.setLogin(rs.getString("login"));
                usuario.setId(rs.getLong("idUsuario"));
                usuario.setNome(rs.getString("nomeUsuario"));
				usuario.setTipo(rs.getString("tipo"));
                usuario.setCadastrao(cadastrao);
                
				usuarios.add(usuario);

            }
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            Functions.TratarExcecoes(null, ex.getMessage());
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return usuarios;
    }
}
