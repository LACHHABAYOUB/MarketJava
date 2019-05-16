package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Vendedor;
import br.com.sgsistemas.cotacao.cotacaoweb.services.ServiceOperations;
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
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class MercFornDao extends BaseDao {

	public MercFornDao(UsuarioLogado usuarioLogado) {
		super("sqlListar",
				"sqlLocate",
				"insert into mercforn (id,fkidmercado,fkidfornecedor, situacao) values (null,?,?, ?)",
				"update mercforn set situacao = ? where fkidmercado = ? and fkidfornecedor = ? ",
				"delete from mercforn where fkidmercado = ? and fkidfornecedor = ?",
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

	public Fornecedor locateByMercado(Mercado mercado, Fornecedor fornecedor) {
		Connection conn = null;
		Fornecedor forn = null;
		
		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement("select mf.fkidmercado, mf.fkidfornecedor from mercforn mf where mf.fkidmercado = ? and mf.fkidfornecedor = ?");
			ps.setLong(1, mercado.getId());
			ps.setLong(2, fornecedor.getId());

			rs = ps.executeQuery();

			if (rs.next()) {
				forn = (Fornecedor) new FornecedorDao(this.getUsuarioLogado()).locate(conn, rs.getLong("fkidfornecedor"));
			}
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			forn = null;
		} finally {
			Conexao.closeC(conn, ps, rs);
		}

		return forn;
	}

	@Override
	public Long save(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long update(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String save(Mercado mercado, Fornecedor fornecedor) throws Exception {
		Connection conn = null;

		try {
			conn = Conexao.getC();

			this.save(conn, mercado, fornecedor);

			conn.commit();

			return ServiceOperations.MSGSAVEUPOK;
		} catch (Exception e) {
			Conexao.rollback(conn);

			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			//tratamento para Exception
			throw e;
		} finally {
			Conexao.closeC(conn, null, null);
		}
	}

	protected Long save(Connection conn, Mercado mercado, Fornecedor fornecedor) throws Exception {
		Long id = -1L;
		
		if (mercado.getId().equals(0L)) {
			throw new SGExceptions("Não é possível realizar esta operação como Administrador. Selecione uma empresa!");
		}
		
		try {
			Long idMercForn = this.consulta(conn, mercado, fornecedor);

			if (idMercForn > 0) {
				return idMercForn;
			}

			ps = conn.prepareStatement(this.getSqlInserir(), PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setLong(1, mercado.getId());
			ps.setLong(2, fornecedor.getId());
			ps.setString(3, fornecedor.getSituacao());

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

	public Long consulta(Mercado mercado, Fornecedor fornecedor) {
		Long retorno = -1L;
		Connection conn = null;

		try {
			conn = Conexao.getC();

			retorno = this.consulta(conn, mercado, fornecedor);
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			retorno = -1L;
		} finally {
			Conexao.closeC(conn, ps, rs);
		}

		return retorno;
	}

	private Long consulta(Connection conn, Mercado mercado, Fornecedor fornecedor) {
		Long retorno = -1L;

		try {
			ps = conn.prepareStatement("select mf.id from mercforn mf where mf.fkidmercado = ? and mf.fkidfornecedor = ?");
			ps.setLong(1, mercado.getId());
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

	public RetornoPersistencia delete(Long idMercado, Long idFornecedor) throws SGExceptions {
		Connection conn = null;
		
		if (idMercado.equals(0L)) {
			throw new SGExceptions("Não é possível realizar esta operação como Administrador. Selecione uma empresa!");
		}
		
		//Usuario logado é um mercado
		List<Vendedor> vendedores = new VendedorDao(this.getUsuarioLogado()).listarMercFornVendByFornecedor(idFornecedor, idMercado);
		if (!vendedores.isEmpty()){
			throw new SGExceptions("Existe vendedor vinculado ao fornecedor! Favor desvincular os vendedores.");
		}
		
		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement(this.getSqlExcluir());
			ps.setLong(1, idMercado);
			ps.setLong(2, idFornecedor);

			/* executa a insercao */
			ps.executeUpdate();

			conn.commit();

			return RetornoPersistencia.OK;
		} catch (Exception e) {
			Conexao.rollback(conn);

			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			//tratamento para Exception
			Functions.TratarExcecoes(null, e.getMessage());

			return RetornoPersistencia.ERRO;
		} finally {
			Conexao.closeC(conn, ps, null);
		}
	}
}
