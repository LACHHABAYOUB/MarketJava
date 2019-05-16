/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Parametro;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class ParametroDao extends BaseDao {

	public ParametroDao(UsuarioLogado usuarioLogado) {
		super("SELECT id, nomeParametro, valorParametro FROM parametro",
				"sqlLocate",
				"INSERT INTO parametro SET id = null, nomeParametro = ?, valorParametro = ?",
				"sqlUpdate",
				"DELETE FROM parametro",
				usuarioLogado);
	}

	@Override
	public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws SGExceptions {
		List<BaseBean> parametros = new ArrayList<BaseBean>();
		int qtdRegistros = 0;

		try {
			ps = conn.prepareStatement(this.getSqlListar());
			rs = ps.executeQuery();

			if (numini <= 1) {
				//primeira pagina
				rs.beforeFirst();
			} else {
				rs.absolute(numini - 1);
			}

			while ((rs.next()) && (rs.getRow() <= numfim)) {
				Parametro parametro = new Parametro(conn, rs);
				parametros.add(parametro);
			}

			rs.last();
			qtdRegistros = rs.getRow();
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());
			
			return new Listagem(0, null);
		} finally {
			Conexao.closeC(null, ps, rs);
		}

		return new Listagem(qtdRegistros, parametros);
	}

	@Override
	public BaseBean locate(Connection conn, Long id) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long save(Connection conn, BaseBean bean) throws Exception {
		try {
			ps = conn.prepareStatement(this.getSqlInserir());
			ps.setString(1, ((Parametro) bean).getNomeParametro());
			ps.setString(2, ((Parametro) bean).getValorParametro());

			ps.executeUpdate();
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			Functions.TratarExcecoes(null, ex.getMessage());
		} finally {
			Conexao.closeC(null, ps, null);
		}

		return ((Parametro) bean).getId();
	}

	@Override
	public Long update(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void deletarTodos() throws SGExceptions {
		Connection conn = null;
		
		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement(this.getSqlExcluir());
			ps.executeUpdate();

			conn.commit();
		} catch (Exception ex) {
			Conexao.rollback(conn);
			
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			Functions.TratarExcecoes(null, ex.getMessage());
		} finally {
			Conexao.closeC(conn, ps, null);
		}
	}

}
