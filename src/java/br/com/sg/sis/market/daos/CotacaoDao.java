/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Cotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.LogInfo;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Filtros;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lachhab Ayoub
 */
public class CotacaoDao extends BaseDao {

	private String agrupaCotacao = "";
	private String tipoUsuario = "";

	public CotacaoDao(UsuarioLogado usuarioLogado) {
		super((usuarioLogado.getUsuario().getAgrupaCotacao().equals("S")
				? "select distinct cast(group_concat(distinct co.id) as char) as idCotacaoAgrupado, "
				+ "cast(group_concat(distinct co.cotacao) as char) as cotacaoAgrupado, "
				+ "cast(group_concat(distinct co.cgc_forn) as char) as cnpjFornecedorAgrupado, "
				+ "co.cotacao, co.gerado, co.data as 'datacotacao', co.cnpj_cliente_sg as 'cnpjClienteSG', co.fechamento as 'datafechamento', co.fechadopor, co.cgc_forn as 'cnpjfornecedor', co.prazo as 'prazocotacao', co.lancado, co.data_lanc as 'datadigitacao', co.prazo1, co.prazo2, co.prazo3, co.id, co.fechado, co.dataencerramento, co.observacao, co.cod_filial as codFilial, "
				+ "m.fkidcadastrao as 'idmercado', f.fkidcadastrao as 'idfornecedor', NOW() as 'dataatual', "
				+ "(select exists (select id from pedido p where p.cnpj_cliente_sg = m.cnpj_cliente_sg and p.cotacao = co.cotacao)) as existePedido "
				+ "from cotacao co "
				+ "inner join mercado m on m.fkidcadastrao = co.fkidmercado "
				+ "inner join fornecedor f on f.fkidcadastrao = co.fkidfornecedor "
				+ "left join filial fi on fi.fkidcadastrao = co.fkidfilial "
				: "select null as idCotacaoAgrupado, null as cotacaoAgrupado, null as cnpjFornecedorAgrupado, co.cotacao, co.gerado, co.data as 'datacotacao', co.cnpj_cliente_sg as 'cnpjClienteSG', co.fechamento as 'datafechamento', co.fechadopor, co.cgc_forn as 'cnpjfornecedor', co.prazo as 'prazocotacao', co.lancado, co.data_lanc as 'datadigitacao', co.prazo1, co.prazo2, co.prazo3, co.id, co.fechado, co.dataencerramento, co.observacao, co.cod_filial as codFilial, "
				+ "m.fkidcadastrao as 'idmercado', f.fkidcadastrao as 'idfornecedor', NOW() as 'dataatual', 0 as 'existePedido' from cotacao co "
				+ "inner join mercado m on m.fkidcadastrao = co.fkidmercado "
				+ "inner join fornecedor f on f.fkidcadastrao = co.fkidfornecedor "
				+ "left join filial fi on fi.fkidcadastrao = co.fkidfilial "),
				"select null as idCotacaoAgrupado, null as cotacaoAgrupado, null as cnpjFornecedorAgrupado, co.cotacao, co.gerado, co.data as 'datacotacao', co.cnpj_cliente_sg as 'cnpjClienteSG', co.fechamento as 'datafechamento', co.fechadopor, co.cgc_forn as 'cnpjfornecedor', co.prazo as 'prazocotacao', co.lancado, co.data_lanc as 'datadigitacao', co.prazo1, co.prazo2, co.prazo3, id, co.fechado, co.dataencerramento, co.observacao, co.cod_filial as codFilial, "
				+ "m.fkidcadastrao as 'idmercado', f.fkidcadastrao as 'idfornecedor', NOW() as 'dataatual', 0 as 'existePedido' from cotacao co inner join mercado m on co.fkidmercado = m.fkidcadastrao inner join fornecedor f on co.fkidfornecedor = f.fkidcadastrao where co.id = ?",
				"sqlInserir",
				"sqlAlterar",
				"sqlExcluir",
				usuarioLogado);

		this.setSqlListar(preparaSqlListar(this.getSqlListar(), usuarioLogado));
	}

	private String preparaSqlListar(String sql, UsuarioLogado usuarioLogado) {
		tipoUsuario = usuarioLogado.getUsuario().getTipo();
		agrupaCotacao = usuarioLogado.getUsuario().getAgrupaCotacao();

		if (tipoUsuario.equals("ME")) {
			sql += " where m.fkidcadastrao = ?";
		} else if (tipoUsuario.equals("FI")) {
			sql += " where fi.fkidcadastrao = ?";
		} else if (tipoUsuario.equals("FO")) {
			sql += " where f.fkidcadastrao = ?";
		} else if (tipoUsuario.equals("VE")) {
			sql += " inner join mercfornvend mfv on mfv.fkidmercado = m.fkidcadastrao"
					+ " inner join fornvend fv on fv.id = mfv.fkidfornvend and fv.fkidfornecedor = f.fkidcadastrao"
					+ " where fv.fkidvendedor = ?";
		} else if (tipoUsuario.equals("AD")) {
			// Se for ADM, o id do mercado será 0(zero)
			sql += " where '0' = ?";
		} else {
			// Se o usuario for de tipo desconhecido, retornar a sql vazia
			sql = "";
		}

		return sql;
	}

	@Override
	public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws SGExceptions {
		List<BaseBean> cotacoes = new ArrayList<>();
		int qtdRegistros;
		String sql;
		String sqlAgrupa = "";

		if (agrupaCotacao.equals("S")) {
			if (tipoUsuario.equals("ME") || tipoUsuario.equals("FI")) {
				//Se for mercado ou filial, quando a cotacao está fechada, traz tudo agrupado, quando está aberta, traz separado por fornecedor
				sqlAgrupa = " group by cotacao, DATE_FORMAT(co.dataencerramento, '%d/%m/%y'), fechado, (case when fechado='N' then co.cgc_forn else 1 end) ";
			} else {
				sqlAgrupa = " group by cotacao, DATE_FORMAT(co.dataencerramento, '%d/%m/%y'), fechado ";
			}
		}

		try {
			if (filtro == Filtros.STATUS.ordinal()) {
				sql = this.getSqlListar() + " and (case when 'G' = ? then co.gerado = 'S' else (case when 'F' = ? then co.fechado = 'S' else ( case when 'A' = ? then (NOW()<= co.dataencerramento and co.fechado != 'S') else(case when 'E'= ? then (NOW()> co.dataencerramento and co.fechado != 'S') else co.fechado='X' end) end) end) end) " + sqlAgrupa + " order by co.data, co.cotacao";

				ps = conn.prepareStatement(sql);
				ps.setString(2, params[1]);
				ps.setString(3, params[1]);
				ps.setString(4, params[1]);
				ps.setString(5, params[1]);
			} else if (filtro == Filtros.NUMCOTACAO.ordinal()) {
				sql = this.getSqlListar() + " and co.cotacao between ? and ?  " + sqlAgrupa + " order by co.data, co.cotacao";

				ps = conn.prepareStatement(sql);
				ps.setInt(2, Integer.valueOf(params[1]));
				ps.setInt(3, Integer.valueOf(params[2]));
			} else {
				sql = this.getSqlListar() + "  " + sqlAgrupa + " order by co.data, co.cotacao";

				ps = conn.prepareStatement(sql);
			}

			ps.setString(1, params[0]);

			rs = ps.executeQuery();

			if (numini <= 1) {
				//primeira pagina
				rs.beforeFirst();
			} else {
				rs.absolute(numini - 1);
			}

			while ((rs.next()) && (rs.getRow() <= numfim)) {
				Cotacao cotacao = new Cotacao(conn, rs);

				cotacao.setMercado((Mercado) new MercadoDao(this.getUsuarioLogado()).locate(rs.getLong("idmercado")));
				cotacao.setFornecedor((Fornecedor) new FornecedorDao(this.getUsuarioLogado()).locate(rs.getLong("idfornecedor")));

				cotacoes.add(cotacao);
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

		return new Listagem(qtdRegistros, cotacoes);
	}

	public RetornoPersistencia fechamentoCotacoes(Listagem fechar) throws SGExceptions {
		try {
			conn = Conexao.getC();

			ArrayList aux = (ArrayList) fechar.getRegistros();

			for (Object cotacao : aux) {
				if (((Cotacao) cotacao).getIdCotacaoAgrupado() == null || ((Cotacao) cotacao).getIdCotacaoAgrupado().equals("")) {
					ps = conn.prepareStatement("update cotacao set fechado = 'S', fechamento = NOW(), fechadopor = ? where id = " + ((Cotacao) cotacao).getId());
				} else {
					ps = conn.prepareStatement("update cotacao set fechado = 'S', fechamento = NOW(), fechadopor = ? where id in (" + ((Cotacao) cotacao).getIdCotacaoAgrupado() + ")");
				}

				ps.setString(1, this.getUsuarioLogado().getUsuario().getNome());

				ps.executeUpdate();
			}

			conn.commit();

			for (Object cotacao : aux) {
				if (((Cotacao) cotacao).getIdCotacaoAgrupado() == null || ((Cotacao) cotacao).getIdCotacaoAgrupado().equals("")) {
					new Thread(new LogInfoThread(LogInfo.TipoOperacao.FECHAMENTO, this.getClass().getSimpleName().replaceAll("Dao", "") + "[ Fechamento da Cotação de Id = " + ((Cotacao) cotacao).getId().toString() + " ]", this.getUsuarioLogado())).start();
				} else {
					new Thread(new LogInfoThread(LogInfo.TipoOperacao.FECHAMENTO, this.getClass().getSimpleName().replaceAll("Dao", "") + "[ Fechamento das Cotações de Id = " + ((Cotacao) cotacao).getIdCotacaoAgrupado() + " ]", this.getUsuarioLogado())).start();
				}
			}

			return RetornoPersistencia.OK;
		} catch (SGExceptions | SQLException e) {
			Conexao.rollback(conn);

			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());

			return RetornoPersistencia.ERRO;
		} finally {
			Conexao.closeC(conn, ps, null);
		}
	}

	public RetornoPersistencia reabrirCotacoes(Listagem fechar) throws SGExceptions {
		try {
			conn = Conexao.getC();

			ArrayList aux = (ArrayList) fechar.getRegistros();



			for (Object cotacao : aux) {
				if (((Cotacao) cotacao).getIdCotacaoAgrupado() == null || ((Cotacao) cotacao).getIdCotacaoAgrupado().equals("")) {
					ps = conn.prepareStatement("update cotacao set fechado = 'N', fechamento = '1901-01-01', fechadopor = '' where id = " + ((Cotacao) cotacao).getId());
				} else {
					ps = conn.prepareStatement("update cotacao set fechado = 'N', fechamento = '1901-01-01', fechadopor = '' where id in (" + ((Cotacao) cotacao).getIdCotacaoAgrupado() + ")");
				}

				ps.executeUpdate();
			}

			conn.commit();

			for (Object cotacao : aux) {
				if (((Cotacao) cotacao).getIdCotacaoAgrupado() == null || ((Cotacao) cotacao).getIdCotacaoAgrupado().equals("")) {
					new Thread(new LogInfoThread(LogInfo.TipoOperacao.REABERTURA, this.getClass().getSimpleName().replaceAll("Dao", "") + "[ Reabertura da Cotação de Id = " + ((Cotacao) cotacao).getId().toString() + " ]", this.getUsuarioLogado())).start();
				} else {
					new Thread(new LogInfoThread(LogInfo.TipoOperacao.REABERTURA, this.getClass().getSimpleName().replaceAll("Dao", "") + "[ Reabertura das Cotações de Id = " + ((Cotacao) cotacao).getIdCotacaoAgrupado() + " ]", this.getUsuarioLogado())).start();
				}
			}

			return RetornoPersistencia.OK;
		} catch (SGExceptions | SQLException e) {
			Conexao.rollback(conn);

			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());

			return RetornoPersistencia.ERRO;
		} finally {
			Conexao.closeC(conn, ps, null);
		}
	}

	public RetornoPersistencia excluiCotacoes(Listagem excluir) throws SGExceptions {
		try {
			conn = Conexao.getC();

			ArrayList aux = (ArrayList) excluir.getRegistros();

			for (Object cotacao : aux) {
				if (((Cotacao) cotacao).getIdCotacaoAgrupado() == null || ((Cotacao) cotacao).getIdCotacaoAgrupado().equals("")) {
					ps = conn.prepareStatement("delete from cotacao where id = " + ((Cotacao) cotacao).getId());
				} else {
					ps = conn.prepareStatement("delete from cotacao where id in (" + ((Cotacao) cotacao).getIdCotacaoAgrupado() + ")");
				}

				ps.executeUpdate();
			}

			conn.commit();

			for (Object cotacao : aux) {
				if (((Cotacao) cotacao).getIdCotacaoAgrupado() == null || ((Cotacao) cotacao).getIdCotacaoAgrupado().equals("")) {
					new Thread(new LogInfoThread(LogInfo.TipoOperacao.DELECAO, this.getClass().getSimpleName().replaceAll("Dao", "") + "[ Exclusão da Cotação de Id = " + ((Cotacao) cotacao).getId().toString() + " ]", this.getUsuarioLogado())).start();
				} else {
					new Thread(new LogInfoThread(LogInfo.TipoOperacao.DELECAO, this.getClass().getSimpleName().replaceAll("Dao", "") + "[ Exclusão das Cotações de Id = " + ((Cotacao) cotacao).getIdCotacaoAgrupado() + " ]", this.getUsuarioLogado())).start();
				}
			}

			return RetornoPersistencia.OK;
		} catch (SGExceptions | SQLException e) {
			Conexao.rollback(conn);

			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(new Cotacao(), e.getMessage());

			return RetornoPersistencia.ERRO;
		} finally {
			Conexao.closeC(conn, ps, null);
		}
	}

	public BaseBean locateRelatorio(String[] params) throws SGExceptions {
		Cotacao cotacao = null;

		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement(this.getSqlListar()
					+ " and co.cotacao = ?"
					+ " and (case when 'T' = ? then co.fechado = co.fechado"
					+ " else (case when 'F' = ? then co.fechado = 'S'"
					+ " else (case when 'A' = ? then (NOW()<= co.dataencerramento and co.fechado != 'S')"
					+ " else (case when 'E'= ? then (NOW()> co.dataencerramento and co.fechado != 'S')"
					+ " end) end) end) end)");

			ps.setString(1, params[0]);
			ps.setString(2, params[1]);
			ps.setString(3, params[2]);
			ps.setString(4, params[2]);
			ps.setString(5, params[2]);
			ps.setString(6, params[2]);

			rs = ps.executeQuery();

			if (rs.next()) {
				cotacao = new Cotacao(conn, rs);
			}
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());
		} finally {
			Conexao.closeC(conn, ps, rs);
		}

		return cotacao;
	}

	public BaseBean locateRelatorioNaoGanhos(String[] params) throws SGExceptions {
		Cotacao cotacao = null;

		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement(
					preparaSqlListar("select co.id as 'id', co.cotacao as 'cotacao', null as idCotacaoAgrupado, null as cotacaoAgrupado, null as cnpjFornecedorAgrupado, co.gerado as 'gerado', co.data_lanc as 'dataCotacao',"
					+ " co.cnpj_cliente_sg as 'cnpjClienteSG', co.fechamento as 'dataFechamento', co.fechadopor as 'fechadoPor', "
					+ " co.cgc_forn as 'cnpjFornecedor', co.prazo as 'prazoCotacao', co.data as 'dataDigitacao', co.prazo1 as 'prazo1',"
					+ " co.prazo2 as 'prazo2', co.prazo3 as 'prazo3', co.fechado as 'fechado',"
					+ " co.dataencerramento as 'dataEncerramento', co.fkidmercado as 'mercado', "
					+ " co.fkidfornecedor as 'fornecedor', co.observacao, co.cod_filial as codFilial, "
					+ " NOW() as 'dataAtual',"
					+ " cadme.nome as 'nomemercado', "
					+ " cadfo.nome as 'nomefornecedo', "
					+ " (case when co.fechado = 'S' then 'Fechado'else "
					+ " (case when (NOW()<= co.dataencerramento and co.fechado != 'S') then 'Aberto' else "
					+ " (case when (NOW()> co.dataencerramento and co.fechado != 'S') then 'Encerrado' "
					+ " else '-' end) end) end) as 'situacao', "
					+ " ip.codigo_pro as 'codproduto', ip.desc_pro as 'descproduto', "
					+ " ip.preco as 'precoun', ip.perc_desc as 'desconto', "
					+ " ip.qtde_emb as 'qtdeemb', ip.preco_emb as 'precoemb', 0 as 'existePedido' "
					+ " from cotacao co "
					+ " inner join mercado m on m.fkidcadastrao = co.fkidmercado "
					+ " left join filial fi on fi.fkidcadastrao = co.fkidfilial "
					+ " inner join cadastrao cadme on m.fkidcadastrao = cadme.id "
					+ " inner join fornecedor f on f.fkidcadastrao = co.fkidfornecedor "
					+ " inner join cadastrao cadfo on f.fkidcadastrao = cadfo.id "
					+ " inner join item_cotacao ip on co.id = ip.fkidcotacao ", this.getUsuarioLogado())
					+ " and co.cotacao = ? "
					+ " and co.cgc_forn  = ? "
					+ " and ip.codigo_pro not in(select codigo_pro from pedido where cotacao = ? and cpfcnpj =? ) "
					+ " order by co.cotacao, cadfo.nome, ip.desc_pro");

			ps.setString(1, params[0]);
			ps.setString(2, params[1]);
			ps.setString(3, params[2]);
			ps.setString(4, params[1]);
			ps.setString(5, params[2]);

			rs = ps.executeQuery();

			if (rs.next()) {
				cotacao = new Cotacao(conn, rs);
			}
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());
		} finally {
			Conexao.closeC(conn, ps, rs);
		}

		return cotacao;
	}

	@Override
	public BaseBean locate(Connection conn, Long id) throws Exception {
		Cotacao cotacao = null;

		try {
			ps = conn.prepareStatement(this.getSqlLocate());
			ps.setLong(1, id);

			rs = ps.executeQuery();

			if (rs.next()) {
				cotacao = new Cotacao(conn, rs);

				cotacao.setMercado((Mercado) new MercadoDao(this.getUsuarioLogado()).locate(rs.getLong("idmercado")));
				cotacao.setFornecedor((Fornecedor) new FornecedorDao(this.getUsuarioLogado()).locate(rs.getLong("idfornecedor")));
			}
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());
		} finally {
			Conexao.closeC(null, ps, rs);
		}

		return cotacao;
	}

	public BaseBean locateAgrupado(Map cotacoes) throws SGExceptions {
		Cotacao cotacao = null;

		try {
			conn = Conexao.getC();

			ps = conn.prepareStatement("select "
					+ "cast(group_concat(distinct co.id) as char) as idCotacaoAgrupado, "
					+ "cast(group_concat(distinct co.cotacao) as char) as cotacaoAgrupado, "
					+ "cast(group_concat(distinct co.cgc_forn) as char) as cnpjFornecedorAgrupado, "
					+ "co.gerado as 'gerado', co.cotacao as 'cotacao' , co.data as 'datacotacao', "
					+ "co.cnpj_cliente_sg as 'cnpjClienteSG', co.fechamento as 'datafechamento', co.fechadopor, co.cgc_forn as 'cnpjfornecedor', "
					+ "co.prazo as 'prazocotacao', co.lancado, co.data_lanc as 'datadigitacao', "
					+ "co.prazo1, co.prazo2, co.prazo3, id, co.fechado, co.dataencerramento, co.observacao, co.cod_filial as codFilial, "
					+ "m.fkidcadastrao as 'idmercado', f.fkidcadastrao as 'idfornecedor', NOW() as 'dataatual', 0 as 'existePedido' "
					+ "from cotacao co "
					+ "inner join mercado m on co.fkidmercado = m.fkidcadastrao "
					+ "inner join fornecedor f on co.fkidfornecedor = f.fkidcadastrao "
					+ "where co.id in (" + cotacoes.get("cotacoes") + ") group by DATE_FORMAT(co.dataencerramento, '%d/%m/%y')");

			rs = ps.executeQuery();

			if (rs.next()) {
				cotacao = new Cotacao(conn, rs);

				cotacao.setMercado((Mercado) new MercadoDao(this.getUsuarioLogado()).locate(rs.getLong("idmercado")));
				cotacao.setFornecedor((Fornecedor) new FornecedorDao(this.getUsuarioLogado()).locate(rs.getLong("idfornecedor")));
			}
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());
		} finally {
			Conexao.closeC(conn, ps, rs);
		}

		return cotacao;
	}

	@Override
	public Long save(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long update(Connection conn, BaseBean cotacao) throws Exception {
		try {
			Cotacao cotacaoNew = (Cotacao) cotacao;
			Cotacao cotacaoOld = (Cotacao) this.locate(conn, cotacaoNew.getId());

			if (cotacaoNew.getIdCotacaoAgrupado() == null || cotacaoNew.getIdCotacaoAgrupado().equals("")) {
				ps = conn.prepareStatement("update cotacao set data_lanc = ?, dataencerramento = ?, prazo = datediff(?, data) where id = " + cotacaoNew.getId());
			} else {
				ps = conn.prepareStatement("update cotacao set data_lanc = ?, dataencerramento = ?, prazo = datediff(?, data) where id in (" + cotacaoNew.getIdCotacaoAgrupado() + ")");
			}
			ps.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(cotacaoNew.getDataDigitacao()));
			ps.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cotacaoNew.getDataEncerramento()));
			ps.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cotacaoNew.getDataEncerramento()));

			/* executa a atualiza??o */
			ps.executeUpdate();

			if (cotacaoNew.getIdCotacaoAgrupado() != null && !cotacaoNew.getIdCotacaoAgrupado().equals("")) {
				new Thread(new LogInfoThread(LogInfo.TipoOperacao.ALTERACAO, "Alteração a seguir é referente a alteração múltipla de Cotação. ID`s: " + cotacaoNew.getIdCotacaoAgrupado(), this.getUsuarioLogado())).start();
			}
			new Thread(new LogInfoThread(cotacaoOld, cotacaoNew, this.getUsuarioLogado())).start();

			return cotacao.getId();
		} catch (SQLException ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			//tratamento para SQLException
			throw ex;
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			//tratamento para Exception
			throw e;
		} finally {
			Conexao.closeC(null, ps, null);
		}
	}

	public Long updateObservacao(BaseBean cotacao) throws Exception {
		try {
			conn = Conexao.getC();

			Cotacao cotacaoNew = (Cotacao) cotacao;
			Cotacao cotacaoOld = (Cotacao) this.locate(conn, cotacaoNew.getId());

			if (cotacaoNew.getIdCotacaoAgrupado() == null || cotacaoNew.getIdCotacaoAgrupado().equals("")) {
				ps = conn.prepareStatement("update cotacao set observacao = ? where id = " + cotacaoNew.getId());
			} else {
				ps = conn.prepareStatement("update cotacao set observacao = ? where id in (" + cotacaoNew.getIdCotacaoAgrupado() + ")");
			}

			ps.setString(1, cotacaoNew.getObservacao());

			/* executa a atualizacao da observacao */
			ps.executeUpdate();

			conn.commit();

			if (cotacaoNew.getIdCotacaoAgrupado() != null && !cotacaoNew.getIdCotacaoAgrupado().equals("")) {
				new Thread(new LogInfoThread(LogInfo.TipoOperacao.ALTERACAO, "Alteração a seguir é referente a alteração múltipla de Cotação. ID`s: " + cotacaoNew.getIdCotacaoAgrupado(), this.getUsuarioLogado())).start();
			}
			new Thread(new LogInfoThread(cotacaoOld, cotacaoNew, this.getUsuarioLogado())).start();

			return cotacao.getId();
		} catch (SQLException ex) {
			Conexao.rollback(conn);
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			//tratamento para SQLException
			throw ex;
		} catch (Exception e) {
			Conexao.rollback(conn);
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			//tratamento para Exception
			throw e;
		} finally {
			Conexao.closeC(conn, ps, null);
		}
	}

	public RetornoPersistencia gerarPrePedido(BaseBean cotacao) throws Exception {
		//Apenas para cotacao Agrupada
		try {
			conn = Conexao.getC();
			Cotacao cotacaoNew = (Cotacao) cotacao;

			//Marca todos os itens das cotacoes como Não Ganho
			ps = conn.prepareStatement("update item_cotacao set itemganho = 'N' where "
					+ "fkidcotacao in (" + cotacaoNew.getIdCotacaoAgrupado() + ")");
			ps.executeUpdate();

			//Marca a cotação como Gerada
			ps = conn.prepareStatement("update cotacao co set co.gerado = 'S' where "
					+ "co.fechado = 'S' and id in (" + cotacaoNew.getIdCotacaoAgrupado() + ")");
			ps.executeUpdate();

			/* 
			 * Marca o item: Melhor Item
			 * Busca os itens de cotacao das cotações selecionadas
			 * Ordena os itens pelo código do produto, preco mais baixo e prazo mais alto.
			 * Coloca em uma variável o primeiro ID do item_cotacao para cada produto e filial(menor preço e maior prazo)
			 * Atualiza o item como Ganho para os ids da variavel
			 */
			ps = conn.prepareStatement("select id, fkidfilial, codigo_pro from item_cotacao "
					+ "where fkidcotacao in (" + cotacaoNew.getIdCotacaoAgrupado() + ") "
					+ "and preco > 0 order by fkidfilial, codigo_pro, preco, prazo desc");
			rs = ps.executeQuery();
			Integer ultimoCodigoPro = 0;
			Long ultimoIdFilial = 0L;
			StringBuilder idsItemCotacao = new StringBuilder();
			while (rs.next()) {
				if (!ultimoCodigoPro.equals(rs.getInt("codigo_pro")) || !ultimoIdFilial.equals(rs.getLong("fkidfilial"))) {
					idsItemCotacao.append(rs.getString("id")).append(",");
					ultimoCodigoPro = rs.getInt("codigo_pro");
					ultimoIdFilial = rs.getLong("fkidfilial");
				}
			}
			if (idsItemCotacao.length() > 0) {
				idsItemCotacao.delete(idsItemCotacao.length() - 1, idsItemCotacao.length());
			}

			ps = conn.prepareStatement("update item_cotacao ic set itemganho = 'P' where id in (" + idsItemCotacao.toString() + ")");
			ps.executeUpdate();

			conn.commit();

			/* 
			 * Marca o item: Segundo Melhor Item
			 * Busca os itens de cotacao das cotações selecionadas
			 * Ordena os itens pelo código do produto, preco mais baixo e prazo mais alto.
			 * Coloca em uma variável o primeiro ID do item_cotacao para cada produto e filial(menor preço e maior prazo)
			 * Atualiza o item como Ganho em Segundo para os ids da variavel
			 */
			ps = conn.prepareStatement("select id, fkidfilial, codigo_pro from item_cotacao "
					+ "where fkidcotacao in (" + cotacaoNew.getIdCotacaoAgrupado() + ") "
					+ "and preco > 0 AND itemganho != 'P' order by fkidfilial, codigo_pro, preco, prazo desc");
			rs = ps.executeQuery();
			ultimoCodigoPro = 0;
			ultimoIdFilial = 0L;
			idsItemCotacao = new StringBuilder();
			while (rs.next()) {
				if (!ultimoCodigoPro.equals(rs.getInt("codigo_pro")) || !ultimoIdFilial.equals(rs.getLong("fkidfilial"))) {
					idsItemCotacao.append(rs.getString("id")).append(",");
					ultimoCodigoPro = rs.getInt("codigo_pro");
					ultimoIdFilial = rs.getLong("fkidfilial");
				}
			}
			if (idsItemCotacao.length() > 0) {
				idsItemCotacao.delete(idsItemCotacao.length() - 1, idsItemCotacao.length());
			}

			ps = conn.prepareStatement("update item_cotacao ic set itemganho = 'S' where id in (" + idsItemCotacao.toString() + ")");
			ps.executeUpdate();

			conn.commit();

			new Thread(new LogInfoThread(cotacaoNew, this.getUsuarioLogado())).start();
			return RetornoPersistencia.OK;

		} catch (SQLException ex) {
			Conexao.rollback(conn);
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			return RetornoPersistencia.ERRO;
		} catch (Exception e) {
			Conexao.rollback(conn);
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			return RetornoPersistencia.ERRO;
		} finally {
			Conexao.closeC(conn, ps, null);
		}

	}

}
