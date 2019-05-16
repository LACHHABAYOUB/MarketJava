package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.PermUsuario;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class PermUsuarioDao extends BaseDao {

	public PermUsuarioDao(UsuarioLogado usuarioLogado) {
		super("select id, fkidusuario, rotina, menu, buscar, inserir, alterar, excluir from permusuario where fkidusuario = ?",
				"sqlLocate",
				"insert into permusuario (id, fkidusuario, rotina, menu, buscar, inserir, alterar, excluir) values (null,?,?,?,?,?,?,?)",
				"sqlAlterar",
				"delete from permusuario where fkidusuario = ?",
				usuarioLogado);
	}

	public List<PermUsuario> listar(Connection conn, Long idUsuario) throws Exception {
		List<PermUsuario> permissoes = new ArrayList<PermUsuario>();

		try {
			ps = conn.prepareStatement(this.getSqlListar());
			ps.setLong(1, idUsuario);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				permissoes.add(new PermUsuario(rs));
			}
		} finally {
			Conexao.closeC(null, ps, rs);
		}

		return permissoes;
	}

	public void save(Connection conn, Long idUsuario, List<PermUsuario> permissoes) throws Exception {
		try {
			ps = conn.prepareStatement(this.getSqlExcluir());
			ps.setLong(1, idUsuario);
			
			ps.executeUpdate();
			
			Conexao.closeC(null, ps, null);

			ps = conn.prepareStatement(this.getSqlInserir());
			
			for (PermUsuario permUsuario : permissoes) {
				ps.setLong(1, idUsuario);
				ps.setString(2, permUsuario.getRotina());
				ps.setString(3, permUsuario.getMenu());
				ps.setString(4, permUsuario.getBuscar());
				ps.setString(5, permUsuario.getInserir());
				ps.setString(6, permUsuario.getAlterar());
				ps.setString(7, permUsuario.getExcluir());
				
				ps.executeUpdate();
				
				new Thread(new LogInfoThread(permUsuario, this.getUsuarioLogado())).start();
			}
		} finally {
			Conexao.closeC(null, ps, null);
		}
	}

	@Override
	public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public BaseBean locate(Connection conn, Long id) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long save(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long update(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
