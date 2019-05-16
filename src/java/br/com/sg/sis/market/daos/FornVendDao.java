package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Vendedor;
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

/**
 *
 * @author Lachhab Ayoub
 */
public class FornVendDao extends BaseDao {

	public FornVendDao(UsuarioLogado usuarioLogado) {
		super("sqlListar",
				"sqlLocate",
				"insert into fornvend (id, fkidvendedor, fkidfornecedor, situacao) values (null, ?, ?, ?)",
				"update fornvend set situacao = ? where fkidfornecedor = ? and fkidvendedor = ? ",
				"delete from fornvend where fkidfornecedor = ? and fkidvendedor = ?",
				usuarioLogado);
	}

	@Override
	public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public BaseBean locate(Connection conn, Long id) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long update(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long save(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Long save(Connection conn, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
		Long id = -1L;

		try {
			Long idFornVend = this.consulta(conn, fornecedor, vendedor);

			if (idFornVend > 0) {
				return idFornVend;
			}

			ps = conn.prepareStatement(this.getSqlInserir(), PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setLong(1, vendedor.getId());
			ps.setLong(2, fornecedor.getId());
			ps.setString(3, vendedor.getSituacao());

			ps.executeUpdate();

			ResultSet resultKey = ps.getGeneratedKeys();
			resultKey.next();
			id = resultKey.getLong("GENERATED_KEY");

			Conexao.closeC(null, null, resultKey);
		} finally {
			Conexao.closeC(null, ps, null);
		}

		return id;
	}

	public Long consulta(Connection conn, Fornecedor fornecedor, Vendedor vendedor) {
		Long retorno = -1L;

		try {
			ps = conn.prepareStatement("select fv.id from fornvend fv where fv.fkidvendedor = ? and fv.fkidfornecedor = ?");
			ps.setLong(1, vendedor.getId());
			ps.setLong(2, fornecedor.getId());

			rs = ps.executeQuery();

			if (rs.next()) {
				retorno = rs.getLong("id");
			}
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			retorno = -1L;
		} finally {
			Conexao.closeC(null, ps, rs);
		}

		return retorno;
	}

	public Long consulta(Fornecedor fornecedor, Vendedor vendedor) {
		Long retorno = -1L;
		Connection conn = null;

		try {
			conn = Conexao.getC();

			retorno = consulta(conn, fornecedor, vendedor);
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			retorno = -1L;
		} finally {
			Conexao.closeC(conn, null, null);
		}

		return retorno;
	}

	public RetornoPersistencia delete(Long idFornecedor, Long idVendedor) throws SGExceptions {
		Connection conn = null;

		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement(this.getSqlExcluir());
			ps.setLong(1, idFornecedor);
			ps.setLong(2, idVendedor);

			ps.executeUpdate();

			conn.commit();

			return RetornoPersistencia.OK;
		} catch (Exception ex) {
			Conexao.rollback(conn);

			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			Functions.TratarExcecoes(null, ex.getMessage());

			return RetornoPersistencia.ERRO;
		} finally {
			Conexao.closeC(conn, ps, rs);
		}
	}
}
