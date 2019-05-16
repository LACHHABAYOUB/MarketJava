package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
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
import java.util.ArrayList;
import java.util.List;

public class MercFornVendDao extends BaseDao {

	public MercFornVendDao(UsuarioLogado usuarioLogado) {
		super("select v.foneresidencial, v.fonecelular, v.cpf, cv.id, cv.nome, cv.datacadastro, cv.fonecomercial, cv.email, fv.situacao, f.fkidcadastrao as 'idfornecedor' "
				+ "from mercfornvend mfv "
				+ "inner join fornvend fv on mfv.fkidfornvend = fv.id "
				+ "inner join fornecedor f on fv.fkidfornecedor = f.fkidcadastrao "
				+ "inner join vendedor v on fv.fkidvendedor = v.fkidcadastrao "
				+ "inner join cadastrao cv on cv.id = v.fkidcadastrao "
				+ "inner join cadastrao cf on cf.id = f.fkidcadastrao "
				+ "where mfv.fkidmercado = ? order by cf.nome, cv.nome",
				"sqlLocate",
				"insert into mercfornvend (id, fkidmercado, fkidfornvend) values (null,?,?)",
				"sqlAlterar",
				"delete from mercfornvend where fkidmercado = ? and fkidfornvend in (select id from fornvend where fkidvendedor = ?)",
				usuarioLogado);
	}

	@Override
	public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws Exception {
		List<BaseBean> lista = new ArrayList<BaseBean>();
		int qtdRegistros;

		try {
			String sql = (this.getSqlListar());

			ps = conn.prepareStatement(sql);
			ps.setLong(1, Long.parseLong(params[0]));

			rs = ps.executeQuery();

			while (rs.next()) {
				Fornecedor fornecedor = (Fornecedor) new FornecedorDao(this.getUsuarioLogado()).locate(conn, rs.getLong("idfornecedor"));
				Vendedor vendedor = new Vendedor(conn, this.getUsuarioLogado(), rs, fornecedor);

				lista.add(vendedor);
			}

			rs.last();
			qtdRegistros = rs.getRow();

			return new Listagem(qtdRegistros, lista);
		} finally {
			Conexao.closeC(null, ps, rs);
		}
	}

	@Override
	public BaseBean locate(Connection conn, Long id) throws Exception {
		return new VendedorDao(this.getUsuarioLogado()).locate(conn, id);
	}

	@Override
	public Long update(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long save(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	protected Long save(Connection conn, Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws Exception {
		Long id = -1L;

		if (mercado.getId().equals(0L)) {
			throw new SGExceptions("Não é possível realizar esta operação como Administrador. Selecione uma empresa!");
		}
		
		try {
			Long idMercFornVend = this.consulta(conn, mercado, fornecedor, vendedor);

			if (idMercFornVend > 0) {
				return idMercFornVend;
			}

			FornVendDao fornVendDao = new FornVendDao(this.getUsuarioLogado());
			Long idFornVend = fornVendDao.save(conn, fornecedor, vendedor);

			ps = conn.prepareStatement(this.getSqlInserir(), PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setLong(1, mercado.getId());
			ps.setLong(2, idFornVend);

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

	private Long consulta(Connection conn, Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws SGExceptions {
		Long retorno = -1L;

		try {
			Long idFornVend = new FornVendDao(this.getUsuarioLogado()).consulta(conn, fornecedor, vendedor);

			ps = conn.prepareStatement("select mfv.id from mercfornvend mfv where mfv.fkidmercado = ? and mfv.fkidfornvend = ?");
			ps.setLong(1, mercado.getId());
			ps.setLong(2, idFornVend);

			rs = ps.executeQuery();

			if (rs.next()) {
				retorno = rs.getLong("id");
			}
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			retorno = -1L;
			//ex.printStackTrace();
			//tratamento para SQLException
			Functions.TratarExcecoes(null, ex.getMessage());
		} finally {
			Conexao.closeC(null, ps, rs);
		}

		return retorno;
	}

	public Long consulta(Mercado mercado, Fornecedor fornecedor, Vendedor vendedor) throws SGExceptions {
		Long retorno = -1L;
		Connection conn = null;

		try {
			conn = Conexao.getC();

			retorno = this.consulta(conn, mercado, fornecedor, vendedor);
		} catch (Exception ex) {
			Conexao.rollback(conn);

			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			retorno = -1L;
			//ex.printStackTrace();
			Functions.TratarExcecoes(null, ex.getMessage());
		} finally {
			Conexao.closeC(conn, null, null);
		}

		return retorno;
	}

	public RetornoPersistencia delete(Long idMercado, Long idVendedor) throws SGExceptions {
		Connection conn = null;

		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement(this.getSqlExcluir());
			ps.setLong(1, idMercado);
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

	public RetornoPersistencia delete(Long idMercado, Long idVendedor, Long idFornecedor) throws SGExceptions {
		Connection conn = null;

		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement("delete from mercfornvend where fkidmercado = ? and fkidfornvend = (select id from fornvend where fkidvendedor = ? and fkidfornecedor = ?)");
			ps.setLong(1, idMercado);
			ps.setLong(2, idVendedor);
			ps.setLong(3, idFornecedor);

			ps.executeUpdate();

			conn.commit();

			return RetornoPersistencia.OK;
		} catch (Exception ex) {
			Conexao.rollback(conn);

			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			Functions.TratarExcecoes(null, ex.getMessage());

			return RetornoPersistencia.ERRO;
			//ex.printStackTrace();
		} finally {
			Conexao.closeC(conn, ps, rs);
		}
	}
}
