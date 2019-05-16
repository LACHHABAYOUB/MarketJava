/*
 * MensagemDao.java
 *
 * Created on 39 de Novembro de 2013, 16:46
 *
 */
package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mensagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub 
 */
public class MensagemDao extends BaseDao {

	public MensagemDao(UsuarioLogado usuarioLogado) {
		super("SELECT id, mensagem FROM mensagem",
			  "SELECT id, mensagem FROM mensagem limit 1",
			  "INSERT INTO mensagem SET mensagem = ?",
			  "UPDATE mensagem SET mensagem = ? WHERE id = ?",
			  "DELETE FROM mensagem WHERE id = ?",
			  usuarioLogado);
	}

	@Override
	public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws Exception {
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
				Mensagem mensagem = new Mensagem(conn, rs);
				parametros.add(mensagem);
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

	public Mensagem locate() throws Exception {
		Mensagem mensagem = null;
		Connection conn = null;
        
        try {
            conn = Conexao.getC();
			ps = conn.prepareStatement(this.getSqlLocate());
            rs = ps.executeQuery();

            if (rs.next()) {
                mensagem = new Mensagem(conn, rs);
            }
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
        } finally {
            Conexao.closeC(conn, ps, rs);
        }
        return mensagem;
	}

	@Override
	public Long save(Connection conn, BaseBean bean) throws Exception {
		try {
			ps = conn.prepareStatement(this.getSqlInserir());
			ps.setString(1, ((Mensagem) bean).getMensagem());
			ps.executeUpdate();
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
			Functions.TratarExcecoes(null, ex.getMessage());
		} finally {
			Conexao.closeC(null, ps, null);
		}

		return ((Mensagem) bean).getId();
	}

	@Override
	public Long update(Connection conn, BaseBean bean) throws Exception {
		Mensagem mensagem = (Mensagem) bean;
        Mensagem mensagemOld = (Mensagem) this.locate();
		
		ps = conn.prepareStatement(this.getSqlAlterar());
        ps.setString(1, ((Mensagem) bean).getMensagem());
        ps.setLong(2, ((Mensagem) bean).getId());

        /* executa a alteracao */
        ps.executeUpdate();

        new Thread(new LogInfoThread(mensagemOld, mensagem, this.getUsuarioLogado())).start();

        return ((Mensagem) bean).getId();
	}

	@Override
	public BaseBean locate(Connection conn, Long id) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

		

}
