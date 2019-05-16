/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Pedido;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGDuplicateKeyExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Filtros;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class PedidoDao extends BaseDao {

	public PedidoDao(UsuarioLogado usuarioLogado) {
		super("select p.cnpj_cliente_sg as 'cnpjClienteSG', p.cod_filial as 'codfilial', p.cotacao, p.codigo_pro as 'codigopro', "
				+ "p.desc_pro as 'descpro', p.qtde, p.qtde_emb as 'qtdeemb', p.preco_unit as 'precounit', "
				+ "p.prazo, p.codbarra, p.cpfcnpj as 'cnpjfornecedor', p.fkidfilial, p.id, p.fkidfilial, p.fkiditemcotacao, co.fechamento as 'lancado', "
				+ "co.fechadopor as 'vendedor', cm.nome as 'mercado', f.fantasia as 'nomefornecedor',f.cnpj as 'fornecedorcnpj' "
				+ "from pedido p inner join item_cotacao ic on ic.id = p.fkiditemcotacao "
				+ "inner join cotacao co on co.id = ic.fkidcotacao inner join fornecedor f on f.fkidcadastrao = co.fkidfornecedor "
				+ "inner join mercado m on m.fkidcadastrao = co.fkidmercado inner join cadastrao cm on cm.id = m.fkidcadastrao "
				+ "left join filial fi on fi.fkidmercado = m.fkidcadastrao",
				"sqlLocate",
				"INSERT INTO pedido SET id = null, cnpj_cliente_sg = ?, cod_filial = ?,  cotacao = ?, codigo_pro = ?, "
                                + "desc_pro = ?, qtde = ?, qtde_emb = ?, preco_unit = ?, prazo = ?, codbarra = ?, cpfcnpj = ?, "
                                + "fkidfilial = ?, fkiditemcotacao = ?, novo = 'S'",
                                
				"sqlAlterar",
				"sqlExcluir",
				usuarioLogado);
		
		this.setSqlListar(preparaSqlListar(this.getSqlListar(), usuarioLogado));
	}

	private String preparaSqlListar(String sql, UsuarioLogado usuarioLogado) {
		String tipoUsuario = usuarioLogado.getUsuario().getTipo();

		if (tipoUsuario.equals("ME")) {
			sql += " where m.fkidcadastrao = ?";
		} else if (tipoUsuario.equals("FI")) {
			sql += " where p.fkidfilial = ?";
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
		List<BaseBean> pedidos = new ArrayList<BaseBean>();
		int qtdRegistros = 0;

		try {
			if (filtro == Filtros.NUMCOTACAO.ordinal()) {
				ps = conn.prepareStatement(this.getSqlListar() + " and p.cotacao between ? and ? group by co.id, f.fkidcadastrao, m.fkidcadastrao, vendedor, p.fkidfilial order by p.cotacao, co.fechamento");
				ps.setInt(2, Integer.parseInt(params[1]));
				ps.setInt(3, Integer.parseInt(params[2]));
			} else if (filtro == Filtros.CNPJ.ordinal()) {
				ps = conn.prepareStatement(this.getSqlListar() + " and p.cpfcnpj like ? group by co.id, f.fkidcadastrao, m.fkidcadastrao, vendedor, p.fkidfilial order by p.cotacao, co.fechamento");
				ps.setString(2, "%" + params[1] + "%");
			} else if (filtro == Filtros.NOMEFANTASIA.ordinal()) {
				ps = conn.prepareStatement(this.getSqlListar() + " and f.fantasia like ? group by co.id, f.fkidcadastrao, m.fkidcadastrao, vendedor, p.fkidfilial order by p.cotacao, co.fechamento");
				ps.setString(2, "%" + params[1] + "%");
			} else {
				ps = conn.prepareStatement(this.getSqlListar() + " group by co.id, f.fkidcadastrao, m.fkidcadastrao, vendedor, p.fkidfilial order by p.cotacao, co.fechamento");
			}
			
			ps.setString(1, params[0]);
			
			rs = ps.executeQuery();

			if (numini <= 1) {
				rs.beforeFirst();
			} else {
				rs.absolute(numini - 1);
			}

			while ((rs.next()) && (rs.getRow() <= numfim)) {
				Pedido pedido = new Pedido(conn, rs);
				pedidos.add(pedido);
			}

			rs.last();
			qtdRegistros = rs.getRow();
		} catch (Exception e) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());
			//e.printStackTrace();
			
			return new Listagem(0, null);
		} finally {
			Conexao.closeC(null, ps, rs);
		}

		return new Listagem(qtdRegistros, pedidos);
	}

	public RetornoPersistencia excluirPedido(Listagem excluir) throws SGExceptions, SQLException {
		try {
			conn = Conexao.getC();
			
			ArrayList aux = (ArrayList) excluir.getRegistros();
			
			ps = conn.prepareStatement("delete from pedido where cnpj_cliente_sg = ? and cotacao = ? and cpfcnpj = ? ");

			for (Object pedido : aux) {
				ps.setString(1, ((Pedido) pedido).getCnpjClienteSG());
				ps.setLong(2, ((Pedido) pedido).getCotacao());
				ps.setString(3, ((Pedido) pedido).getCnpjfornecedor());
				
				ps.executeUpdate();
			}
			
			conn.commit();

			return RetornoPersistencia.OK;
		} catch (Exception e) {
			Conexao.rollback(conn);
			
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			Functions.TratarExcecoes(null, e.getMessage());
			
			return RetornoPersistencia.ERRO;
		} finally {
			Conexao.closeC(conn, ps, null);
		}
	}

    public RetornoPersistencia salvarPedido(Listagem pedido) throws SGExceptions, SQLException {
	    try {
                conn = Conexao.getC();
                ArrayList aux = (ArrayList) pedido.getRegistros();
                
                ps = conn.prepareStatement(this.getSqlInserir());
                
                for (Object pedidos : aux) {
                    ps.setString(1, ((Pedido) pedidos).getCnpjClienteSG());
                    ps.setInt(2, ((Pedido) pedidos).getCodfilial());
                    ps.setInt(3, ((Pedido) pedidos).getCotacao());
                    ps.setInt(4, ((Pedido) pedidos).getCodigopro());
                    ps.setString(5, ((Pedido) pedidos).getDescpro());
                    ps.setInt(6, ((Pedido) pedidos).getQtde());
                    ps.setInt(7, ((Pedido) pedidos).getQtdeemb());
                    ps.setDouble(8, ((Pedido) pedidos).getPrecounit());
                    ps.setInt(9, ((Pedido) pedidos).getPrazo());
                    ps.setLong(10, ((Pedido) pedidos).getCodbarra());
                    ps.setString(11, ((Pedido) pedidos).getCnpjfornecedor());
                    ps.setInt(12, ((Pedido) pedidos).getFkidfilial());
                    ps.setInt(13, ((Pedido) pedidos).getFkiditemcotacao());
                    ps.executeUpdate();
                    
                }
                conn.commit();
                
                return RetornoPersistencia.OK;
			
            } catch (SQLException ex) {
				LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
				//gera excecao de chave duplicado
				if (ex.getMessage().toUpperCase().contains("DUPLICATE ENTRY")) {
					throw new SGDuplicateKeyExceptions("PEDIDO já existente: ");
				} else {
					//colocar aqui outros exceptions
					throw new SGExceptions(ex.getMessage());
				}
				
			} catch (SGExceptions ex) {
				Conexao.rollback(conn);
				LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
				Functions.TratarExcecoes(null, ex.getMessage());
				return RetornoPersistencia.ERRO;
			} finally {
                Conexao.closeC(conn, ps, null);
            }
	}

	@Override
	public Long update(Connection conn, BaseBean bean) throws Exception {
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

}
