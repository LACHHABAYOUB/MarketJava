/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.LogInfo;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Filtros;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class LogInfoDao extends BaseDao {

	public LogInfoDao(UsuarioLogado usuarioLogado) {
		super("select li.* from loginfo li inner join usuario u on u.login = li.loginusuario where li.data between ? and ? ",
				"sqlLocate",
				"INSERT INTO loginfo(id, loginusuario, descricao, operacao, data, hora, ip) VALUES(0,?,?,?,NOW(), NOW(),?)",
				"sqlAlterar",
				"sqlExcluir",
				usuarioLogado);
	}

	@Override
	public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws Exception {
		List<BaseBean> logs = new ArrayList<BaseBean>();
		int qtdRegistros;
		String sql = this.getSqlListar();
		Long idCadastraoUsuarioLogado = this.getUsuarioLogado().getUsuario().getCadastrao().getId();

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
			
			if (filtro.intValue() == Filtros.OPERACAO.ordinal()) {
				if (!params[2].equals("")) {
					ps = conn.prepareStatement(sql + " and loginusuario = ?  and operacao = ?");
					ps.setString(1, params[0]);
					ps.setString(2, params[1]);
					ps.setString(3, params[2]);
					ps.setString(4, params[3]);
				} else {
					if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME")) {
						sql += " and u.fkidcadastrao in (select id from ("
								+ "(select fkidcadastrao as 'id' from mercado where fkidcadastrao = ?) union all "
								+ "(select fkidcadastrao as 'id' from filial where fkidmercado = ?) union all "
								+ "(select fkidfornecedor as 'id' from mercforn mf where fkidmercado = ?) union all "
								+ "(select fv.fkidvendedor as 'id' from mercfornvend mfv inner join fornvend fv on mfv.fkidfornvend = fv.id where fkidmercado = ?) "
								+ ") usuarios)";

						ps = conn.prepareStatement(sql + " and operacao = ? ");
						ps.setString(1, params[0]);
						ps.setString(2, params[1]);

						ps.setLong(3, idCadastraoUsuarioLogado);
						ps.setLong(4, idCadastraoUsuarioLogado);
						ps.setLong(5, idCadastraoUsuarioLogado);
						ps.setLong(6, idCadastraoUsuarioLogado);

						ps.setString(7, params[3]);
					} else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
						sql += " and u.fkidcadastrao in (select id from ("
								+ "(select fkidcadastrao as 'id' from fornecedor where fkidcadastrao = ?) union all "
								+ "(select fkidvendedor as 'id' from fornvend mf where fkidfornecedor = ?) "
								+ ") usuarios)";

						ps = conn.prepareStatement(sql + " and operacao = ? ");
						ps.setString(1, params[0]);
						ps.setString(2, params[1]);

						ps.setLong(3, idCadastraoUsuarioLogado);
						ps.setLong(4, idCadastraoUsuarioLogado);

						ps.setString(5, params[3]);
					} else if ((this.getUsuarioLogado().getUsuario().getTipo().equals("FI")) || (this.getUsuarioLogado().getUsuario().getTipo().equals("VE"))) {
						sql += " and u.fkidcadastrao = ?";

						ps = conn.prepareStatement(sql + " and operacao = ? ");
						ps.setString(1, params[0]);
						ps.setString(2, params[1]);

						ps.setLong(3, idCadastraoUsuarioLogado);
						ps.setLong(4, idCadastraoUsuarioLogado);

						ps.setString(5, params[3]);
					} else if (this.getUsuarioLogado().getUsuario().getTipo().equals("AD")) {
						sql += " and '0' = ?";

						ps = conn.prepareStatement(sql + " and operacao = ? ");
						ps.setString(1, params[0]);
						ps.setString(2, params[1]);

						ps.setLong(3, idCadastraoUsuarioLogado);
						ps.setLong(4, idCadastraoUsuarioLogado);

						ps.setString(5, params[3]);
					} else {
						throw new Exception("Tipo de usuario desconhecido");
					}
				}
			} else {
				if (!params[2].equals("")) {
					ps = conn.prepareStatement(sql + " and loginusuario = ?");
					ps.setString(1, params[0]);
					ps.setString(2, params[1]);
					ps.setString(3, params[2]);
				} else {
					if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME")) {
						sql += " and u.fkidcadastrao in (select id from ("
								+ "(select fkidcadastrao as 'id' from mercado where fkidcadastrao = ?) union all "
								+ "(select fkidcadastrao as 'id' from filial where fkidmercado = ?) union all "
								+ "(select fkidfornecedor as 'id' from mercforn mf where fkidmercado = ?) union all "
								+ "(select fv.fkidvendedor as 'id' from mercfornvend mfv inner join fornvend fv on mfv.fkidfornvend = fv.id where fkidmercado = ?) "
								+ ") usuarios)";

						ps = conn.prepareStatement(sql);
						ps.setString(1, params[0]);
						ps.setString(2, params[1]);

						ps.setLong(3, idCadastraoUsuarioLogado);
						ps.setLong(4, idCadastraoUsuarioLogado);
						ps.setLong(5, idCadastraoUsuarioLogado);
						ps.setLong(6, idCadastraoUsuarioLogado);
					} else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
						sql += " and u.fkidcadastrao in (select id from ("
								+ "(select fkidcadastrao as 'id' from fornecedor where fkidcadastrao = ?) union all "
								+ "(select fkidvendedor as 'id' from fornvend mf where fkidfornecedor = ?) "
								+ ") usuarios)";
						
						ps = conn.prepareStatement(sql);
						ps.setString(1, params[0]);
						ps.setString(2, params[1]);

						ps.setLong(3, idCadastraoUsuarioLogado);
						ps.setLong(4, idCadastraoUsuarioLogado);
					} else if ((this.getUsuarioLogado().getUsuario().getTipo().equals("FI")) || (this.getUsuarioLogado().getUsuario().getTipo().equals("VE"))) {
						sql += " and u.fkidcadastrao = ?";
						
						ps = conn.prepareStatement(sql);
						ps.setString(1, params[0]);
						ps.setString(2, params[1]);

						ps.setLong(3, idCadastraoUsuarioLogado);
					} else if (this.getUsuarioLogado().getUsuario().getTipo().equals("AD")) {
						sql += " and '0' = ?";
						
						ps = conn.prepareStatement(sql);
						ps.setString(1, params[0]);
						ps.setString(2, params[1]);

						ps.setLong(3, idCadastraoUsuarioLogado);
					} else {
						throw new Exception("Tipo de usuario desconhecido");
					}
				}
			}
			
			rs = ps.executeQuery();

			while ((rs.next()) && (rs.getRow() <= numfim)) {
				LogInfo logInfo = new LogInfo(rs);
				logs.add(logInfo);
			}

			rs.last();
			qtdRegistros = rs.getRow();
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());
			
			return new Listagem(0, null);
		} finally {
			Conexao.closeC(null, ps, null);
		}

		return new Listagem(qtdRegistros, logs);
	}

	@Override
	public BaseBean locate(Connection conn, Long id) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long save(Connection conn, BaseBean bean) throws Exception {
		LogInfo logInfo = (LogInfo) bean;
		
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			logInfo.setIp(thisIp.getHostAddress());
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			logInfo.setIp("");
		}

		/* prepara a busca pela Situacao Selecionada */
		ps = conn.prepareStatement(this.getSqlInserir(), PreparedStatement.RETURN_GENERATED_KEYS);

		/* prepara para inserir registro */
		ps.setString(1, logInfo.getLoginUsuario());
		ps.setString(2, logInfo.getDescricao());
		ps.setString(3, logInfo.getOperacao());
		ps.setString(4, logInfo.getIp());

		/* executa a insercao */
		ps.executeUpdate();
		
		ResultSet resultKey = ps.getGeneratedKeys();
		resultKey.next();
		/* seta as informações para o resultado */
		return resultKey.getLong("GENERATED_KEY");
	}

	@Override
	public Long update(Connection conn, BaseBean bean) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
