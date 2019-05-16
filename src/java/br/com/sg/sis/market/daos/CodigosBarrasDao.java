package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.CodigoBarra;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Cotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.FornecedorPrecoPrazo;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.ItensCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.LogInfo.TipoOperacao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SgDupEntryException;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Filtros;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lachhab Ayoub
 */
public class CodigosBarrasDao extends BaseDao {

    public CodigosBarrasDao(UsuarioLogado usuarioLogado) {
        super("SELECT id, cliente_sg as clienteSg, cnpj_cliente_sg as cnpjClienteSg, codigo_pro as codigoPro, codbarra as codBarra FROM codigos_barras",
                "", "", "", "", usuarioLogado);
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

    public List<CodigoBarra> findByClienteAndCnpjAndProduto(Integer clienteSg, String cnpjClienteSg, Integer codigoProduto) throws SQLException {
        Connection conn = null;
        List<CodigoBarra> listaCodigosBarras = new ArrayList();
        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement(this.getSqlListar() + " where cliente_sg = ? AND cnpj_cliente_sg = ? AND codigo_pro = ?");
            ps.setInt(1, clienteSg);
            ps.setString(2, cnpjClienteSg);
            ps.setInt(3, codigoProduto);
            ps.executeQuery();
            rs = ps.executeQuery();
            while (rs.next()) {
                listaCodigosBarras.add(new CodigoBarra(rs));
            }
        } catch (Exception ex) {
            Conexao.rollback(conn);
        } finally {
            Conexao.closeC(conn, ps, rs);
        }
        return listaCodigosBarras;
    }
}
