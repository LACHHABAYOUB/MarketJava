/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Versao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;

/**
 *
 * @author Lachhab Ayoub
 */
public class VersaoDao extends BaseDao {

	public VersaoDao(UsuarioLogado usuarioLogado) {
		super("SQLLISTAR",
				"select v.id, v.dataversao, v.baseline from versao v order by v.id desc limit 1",
				"SQLINSERT",
				"SQLUPDATE",
				"SQLDELETE", usuarioLogado);
	}

	public BaseBean locateLastVersao() throws SGExceptions {
		Versao versao = null;
		Connection conn = null;
		
		try {
			/* cria a conexao com o Banco de Dados */
			conn = Conexao.getC();
			
			ps = conn.prepareStatement(this.getSqlLocate());
			/* executa a busca e seta o Resultado em um ResultSet */
			rs = ps.executeQuery();

			/* cria o objeto */
			if (rs.next()) {
				versao = new Versao(conn, rs);
			}
		} catch (Exception e) {
			Functions.TratarExcecoes(null, e.getMessage());
		} finally {
			Conexao.closeC(conn, ps, rs);
		}
		/* retorna o objeto */
		return versao;
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
