package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
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
public class ItensCotacaoDao extends BaseDao {

    public ItensCotacaoDao(UsuarioLogado usuarioLogado) {
        super("select ic.codigo_pro as 'codigopro', ic.cnpj_cliente_sg as 'cnpjClienteSG', ic.desc_pro as 'descpro', "
                + "ic.cgc_forn as 'cnpjfornecedor', f.fantasia as 'fantasiafornecedor', ic.preco, ic.prazo, ic.unidade, ic.quantidade as 'quantidade', ic.qtde_emb as 'qtdeemb', "
                + "ic.preco_emb as 'precoemb', ic.cotacao, ic.perc_desc as 'percdesc', ic.perc_acre as 'percacre', "
                + "ic.codbarra, ic.id, ic.fkidcotacao, ic.itemganho, "
                + "ic.cod_filial as 'codFilial', ic.fkidfilial "
                + "from item_cotacao ic "
                + "INNER JOIN fornecedor f on f.cnpj = ic.cgc_forn "
                + "where ic.fkidcotacao = ? ",
                "select ic.codigo_pro as 'codigopro', ic.cnpj_cliente_sg as 'cnpjClienteSG', ic.desc_pro as 'descpro', "
                + "ic.cgc_forn as 'cnpjfornecedor', f.fantasia as 'fantasiafornecedor', ic.preco, ic.prazo, ic.unidade, ic.quantidade as 'quantidade', ic.qtde_emb as 'qtdeemb', "
                + "ic.preco_emb as 'precoemb', ic.cotacao, ic.perc_desc as 'percdesc', ic.perc_acre as 'percacre', "
                + "ic.codbarra, ic.id, ic.fkidcotacao, ic.itemganho, "
                + "ic.cod_filial as 'codFilial', ic.fkidfilial "
                + "from item_cotacao ic "
                + "INNER JOIN fornecedor f on f.cnpj = ic.cgc_forn "
                + "where ic.id = ?",
                "sqlInserir",
                "update item_cotacao set perc_desc = ?, preco_emb = ?, preco = ? where id = ?",
                "sqlExcluir",
                usuarioLogado);
    }

    @Override
    //LISTAR NORMAL
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws SGExceptions {
        List<BaseBean> itens_cotacao = new ArrayList<>();
        int qtdRegistros;

        try {
            ps = conn.prepareStatement(this.getSqlListar() + "order by ic.desc_pro");
            ps.setLong(1, Long.parseLong(params[0]));

            rs = ps.executeQuery();

            if (numini <= 1) {
                //primeira pagina
                rs.beforeFirst();
            } else {
                rs.absolute(numini - 1);
            }

            while ((rs.next()) && (rs.getRow() <= numfim)) {
                ItensCotacao itemCotacao = new ItensCotacao(rs);
                itens_cotacao.add(itemCotacao);
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
        return new Listagem(qtdRegistros, itens_cotacao);
    }

    //LISTAR AGRUPADO
    public Listagem listar(Integer filtro, Integer numini, Integer numfim, Map cotacoes) throws SGExceptions {
        List<BaseBean> itens_cotacao = new ArrayList<>();
        int qtdRegistros;

        try {
            conn = Conexao.getC();

            String sql = "select ic.codigo_pro as 'codigopro', ic.cnpj_cliente_sg as 'cnpjClienteSG', "
                    + " ic.desc_pro as 'descpro', ic.cgc_forn as 'cnpjfornecedor', f.fantasia as 'fantasiafornecedor', "
                    + " ic.preco, ic.prazo, ic.unidade, sum(ic.quantidade) as 'quantidade', "
                    + " ic.qtde_emb as 'qtdeemb', ic.preco_emb as 'precoemb', "
                    + " ic.cotacao, ic.perc_desc as 'percdesc', ic.perc_acre as 'percacre', "
                    + " ic.codbarra, ic.id, ic.fkidcotacao, ic.itemganho, ic.cod_filial as 'codFilial' "
                    + " from "
                    + " item_cotacao ic "
                    + "INNER JOIN fornecedor f on f.cnpj = ic.cgc_forn "
                    + "where ic.fkidcotacao in (" + cotacoes.get("cotacoes") + ") ";

            if (filtro == Filtros.ITENSGANHOS.ordinal()) {
                sql += " and ic.itemganho = 'P' group by ic.codbarra";
            } else {
                sql += "group by ic.codbarra";
            }

            ps = conn.prepareStatement(sql + " order by ic.desc_pro");

            rs = ps.executeQuery();

            if (numini <= 1) {
                //primeira pagina
                rs.beforeFirst();
            } else {
                rs.absolute(numini - 1);
            }

            while ((rs.next()) && (rs.getRow() <= numfim)) {
                ItensCotacao itemCotacao = new ItensCotacao(rs);
                itens_cotacao.add(itemCotacao);
            }

            rs.last();
            qtdRegistros = rs.getRow();
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            Functions.TratarExcecoes(null, e.getMessage());
            return new Listagem(0, null);

        } finally {
            Conexao.closeC(conn, ps, null);
        }

        return new Listagem(qtdRegistros, itens_cotacao);
    }
    //LISTA ITENS DE FILIAIS DA MESMA REDE

    public Listagem listarPorFilial(Integer numini, Integer numfim, String[] params, Map cotacoes, BaseBean cotacao) throws SGExceptions {

        List<BaseBean> itens_cotacao = new ArrayList<>();
        int qtdRegistros;

        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement("select ic.codigo_pro as 'codigopro', ic.cnpj_cliente_sg as 'cnpjClienteSG', "
                    + " ic.desc_pro as 'descpro', ic.cgc_forn as 'cnpjfornecedor', f.fantasia as 'fantasiafornecedor', "
                    + " ic.preco, ic.prazo, ic.unidade, ic.quantidade as 'quantidade', "
                    + " ic.qtde_emb as 'qtdeemb', ic.preco_emb as 'precoemb', "
                    + " ic.cotacao, ic.perc_desc as 'percdesc', ic.perc_acre as 'percacre', "
                    + " ic.codbarra, ic.id, ic.fkidcotacao, ic.itemganho, ic.cod_filial as 'codFilial' from  item_cotacao ic "
                    + " INNER JOIN fornecedor f on f.cnpj = ic.cgc_forn "
                    + " INNER JOIN cotacao c on c.id = ic.fkidcotacao "
                    + " INNER JOIN mercado m on m.fkidcadastrao = c.fkidmercado "
                    + " where m.fkidcadastrao = ? and c.cotacao = ? and codigo_pro not in (select codigo_pro from item_cotacao "
                    + "where fkidcotacao in (" + cotacoes.get("cotacoes") + ")) group by codigo_pro order by ic.desc_pro");
            ps.setLong(1, ((Cotacao) cotacao).getMercado().getId());
            ps.setInt(2, ((Cotacao) cotacao).getCotacao());
            rs = ps.executeQuery();

            if (numini <= 1) {
                //primeira pagina
                rs.beforeFirst();
            } else {
                rs.absolute(numini - 1);
            }

            while ((rs.next()) && (rs.getRow() <= numfim)) {
                ItensCotacao itemCotacao = new ItensCotacao(rs);
                itens_cotacao.add(itemCotacao);
            }

            rs.last();
            qtdRegistros = rs.getRow();
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            Functions.TratarExcecoes(null, e.getMessage());
            return new Listagem(0, null);

        } finally {
            Conexao.closeC(conn, ps, null);
        }

        return new Listagem(qtdRegistros, itens_cotacao);

    }

    //LISTAR PRÉ-PEDIDO
    public Listagem listarPrePedido(Integer numini, Integer numfim, String[] params) throws SGExceptions {
        Map<Integer, ItensCotacao> itensCotacaoMap = new HashMap<>();
        Map<String, Fornecedor> fornecedores = new HashMap<>();

        int qtdRegistros;

        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement("SELECT "
                    + "ic.codigo_pro as 'codigopro', "
                    + "ic.cnpj_cliente_sg as 'cnpjClienteSG', "
                    + "ic.desc_pro as 'descpro', "
                    + "ic.cgc_forn as 'cnpjfornecedor', "
                    + "f.fantasia as 'fantasiafornecedor', "
                    + "ic.preco, "
                    + "ic.prazo, "
                    + "ic.unidade, "
                    + "ic.quantidade as 'quantidade', "
                    + "ic.qtde_emb as 'qtdeemb', "
                    + "ic.preco_emb as 'precoemb', "
                    + "ic.cotacao, "
                    + "ic.perc_desc as 'percdesc', "
                    + "ic.perc_acre as 'percacre', "
                    + "ic.codbarra, "
                    + "ic.id, "
                    + "ic.fkidcotacao,"
                    + "ic.itemganho, "
                    + "ic.cod_filial as codFilial "
                    + "FROM item_cotacao ic "
                    + "INNER JOIN fornecedor f on f.cnpj = ic.cgc_forn "
                    + "WHERE ic.fkidcotacao in (" + params[0] + ") and ic.preco > 0 "
                    + "order by ic.codigo_pro asc");

            rs = ps.executeQuery();

            if (numini <= 1) {
                //primeira pagina
                rs.beforeFirst();
            } else {
                rs.absolute(numini - 1);
            }

            while ((rs.next()) && (rs.getRow() <= numfim)) {
                ItensCotacao itemCotacao = new ItensCotacao(rs);

                if (fornecedores.get(itemCotacao.getCnpjFornecedor()) == null) {
                    fornecedores.put(itemCotacao.getCnpjFornecedor(), new Fornecedor(itemCotacao.getCnpjFornecedor(), itemCotacao.getFantasiaFornecedor()));
                }

                if (itensCotacaoMap.get(itemCotacao.getCodigoPro()) == null) {
                    itemCotacao.getFornecedorPrecoPrazo().add(new FornecedorPrecoPrazo(itemCotacao));
                    itensCotacaoMap.put(itemCotacao.getCodigoPro(), itemCotacao);
                } else {
                    itensCotacaoMap.get(itemCotacao.getCodigoPro()).getFornecedorPrecoPrazo().add(new FornecedorPrecoPrazo(itemCotacao));
                }
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

        List<ArrayList> retorno = new ArrayList<>();
        retorno.add(new ArrayList<>(itensCotacaoMap.values()));
        retorno.add(new ArrayList<>(fornecedores.values()));

        return new Listagem(qtdRegistros, retorno);
    }

    @Override
    public BaseBean locate(Connection conn, Long id) throws Exception {
        ps = conn.prepareStatement(this.getSqlLocate());
        ps.setLong(1, id);

        rs = ps.executeQuery();

        if (rs.next()) {
            return new ItensCotacao(rs);
        }

        return null;
    }

    @Override
    public Long update(Connection conn, BaseBean itemCotacao) throws Exception {
        try {
            Cotacao cotacao = (Cotacao) new CotacaoDao(this.getUsuarioLogado()).locate(conn, ((ItensCotacao) itemCotacao).getFkidCotacao());

            ItensCotacao itemCotacaoOld = (ItensCotacao) this.locate(conn, itemCotacao.getId());

            if (!cotacao.getDataDigitacao().equals(cotacao.getDataAtual())) {
                Calendar hoje = Calendar.getInstance();
                hoje.setTime(new Date(System.currentTimeMillis()));
                hoje.set(Calendar.AM_PM, 0);
                hoje.set(Calendar.HOUR, 0);
                hoje.set(Calendar.MINUTE, 0);
                hoje.set(Calendar.SECOND, 0);
                hoje.set(Calendar.MILLISECOND, 0);

                cotacao.setDataDigitacao(hoje.getTime());

                new CotacaoDao(this.getUsuarioLogado()).update(conn, cotacao);
            }

            ps = conn.prepareStatement(this.getSqlAlterar());
            ps.setDouble(1, ((ItensCotacao) itemCotacao).getPercDesc());
            ps.setBigDecimal(2, Functions.parsePrecoEmb((ItensCotacao) itemCotacao));
            ps.setBigDecimal(3, Functions.parsePrecoUnit((ItensCotacao) itemCotacao));
            ps.setLong(4, itemCotacao.getId());

            /* executa a atualização */
            ps.executeUpdate();

            new Thread(new LogInfoThread(itemCotacaoOld, itemCotacao, this.getUsuarioLogado())).start();

            return itemCotacao.getId();
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            LogCotacao.escreveLog(LogCotacao.LEVEL_DEBUG, "preco_emb: " + ((ItensCotacao) itemCotacao).getPrecoEmb(), null);
            LogCotacao.escreveLog(LogCotacao.LEVEL_DEBUG, "preco: " + ((ItensCotacao) itemCotacao).getPreco(), null);
            throw e;
        } finally {
            Conexao.closeC(null, ps, null);
        }
    }
    //update agrupado pelo id da cotacao e codbarras

    public RetornoPersistencia update(BaseBean itemCotacao, Map cotacoes) throws Exception {

        try {
            conn = Conexao.getC();

            RetornoPersistencia retorno =  updateConn(itemCotacao,cotacoes,conn);
            conn.commit();
            return retorno;
        } catch (Exception e) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            LogCotacao.escreveLog(LogCotacao.LEVEL_DEBUG, "preco_emb: " + ((ItensCotacao) itemCotacao).getPrecoEmb(), null);
            LogCotacao.escreveLog(LogCotacao.LEVEL_DEBUG, "preco: " + ((ItensCotacao) itemCotacao).getPreco(), null);
            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, ps, null);
        }
    }
    
    public RetornoPersistencia updateConn(BaseBean itemCotacao, Map cotacoes, Connection conn) throws Exception {
        Cotacao cotacao = (Cotacao) new CotacaoDao(this.getUsuarioLogado()).locate(conn, ((ItensCotacao) itemCotacao).getFkidCotacao());

        if (!cotacao.getDataDigitacao().equals(cotacao.getDataAtual())) {
            Calendar hoje = Calendar.getInstance();
            hoje.setTime(new Date(System.currentTimeMillis()));
            hoje.set(Calendar.AM_PM, 0);
            hoje.set(Calendar.HOUR, 0);
            hoje.set(Calendar.MINUTE, 0);
            hoje.set(Calendar.SECOND, 0);
            hoje.set(Calendar.MILLISECOND, 0);

            cotacao.setDataDigitacao(hoje.getTime());

            new CotacaoDao(this.getUsuarioLogado()).update(conn, cotacao);
        }

        ps = conn.prepareStatement("update item_cotacao set perc_desc = ?, preco_emb = ?, preco = ? where "
                + " codbarra = ? and fkidcotacao in(" + cotacoes.get("cotacoes") + ") ");

        ps.setDouble(1, ((ItensCotacao) itemCotacao).getPercDesc());
        ps.setBigDecimal(2, Functions.parsePrecoEmb((ItensCotacao) itemCotacao));
        ps.setBigDecimal(3, Functions.parsePrecoUnit((ItensCotacao) itemCotacao));
        ps.setLong(4, ((ItensCotacao) itemCotacao).getCodBarra());
        /* executa a atualização */
        ps.executeUpdate();

        new Thread(new LogInfoThread(TipoOperacao.ALTERACAO,
                "Alteração agrupada pela cotação com ID: " + cotacoes.get("cotacoes")
                + "e com CodBarra:" + ((ItensCotacao) itemCotacao).getCodBarra()
                + ". Dados do item [perc_desc: " + ((ItensCotacao) itemCotacao).getPercDesc().toString()
                + ", preco_emb: " + ((ItensCotacao) itemCotacao).getPrecoEmb().toString()
                + ", preco: " + ((ItensCotacao) itemCotacao).getPreco().toString() + "]", this.getUsuarioLogado())).start();

        return RetornoPersistencia.OK;
    }
    

    public RetornoPersistencia salvaPrazoCotacao(BaseBean itemCotacao) throws Exception {

        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement("update item_cotacao set prazo = ? where cotacao = ? and fkidcotacao = ?");
            ps.setInt(1, ((ItensCotacao) itemCotacao).getPrazo());
            ps.setInt(2, ((ItensCotacao) itemCotacao).getCotacao());
            ps.setLong(3, ((ItensCotacao) itemCotacao).getFkidCotacao());

            ps.executeUpdate();

            conn.commit();

            return RetornoPersistencia.OK;
        } catch (SGExceptions | SQLException e) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);

            throw e;
        } finally {
            Conexao.closeC(conn, ps, null);
        }
    }

    public RetornoPersistencia salvaPrazoCotacaoAgrupado(BaseBean itemCotacao, Map cotacoes) throws Exception {

        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement("update item_cotacao set prazo = ? where  fkidcotacao in(" + cotacoes.get("cotacoes") + ") ");
            ps.setInt(1, ((ItensCotacao) itemCotacao).getPrazo());
            ps.executeUpdate();
            conn.commit();
            return RetornoPersistencia.OK;

        } catch (SGExceptions | SQLException e) {
            Conexao.rollback(conn);
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            throw e;
        } finally {
            Conexao.closeC(conn, ps, null);
        }
    }

    public RetornoPersistencia alterarItemGanho(BaseBean itemGanho) throws Exception {

        try {
            conn = Conexao.getC();

            Long idItemGanho = 0L;
            ps = conn.prepareStatement("select id from item_cotacao where codigo_pro = ? "
                    + " and cnpj_cliente_sg = ? and cotacao = ? and itemganho = 'P'");
            ps.setInt(1, ((ItensCotacao) itemGanho).getCodigoPro());
            ps.setString(2, ((ItensCotacao) itemGanho).getCnpjClienteSG());
            ps.setInt(3, ((ItensCotacao) itemGanho).getCotacao());
            rs = ps.executeQuery();

            if (rs.next()) {
                idItemGanho = rs.getLong("id");
            }
            //verifica se esta alterando o item ganho caso sim ignora alteracao
            if (((ItensCotacao) itemGanho).getId().longValue() != idItemGanho) {
                //Atualiza item que era P para X 
                ps = conn.prepareStatement("update item_cotacao set itemganho = 'X' "
                        + " where cotacao = ?  and cnpj_cliente_sg = ? and codigo_pro = ? and (itemganho = 'P')");
                ps.setInt(1, ((ItensCotacao) itemGanho).getCotacao());
                ps.setString(2, ((ItensCotacao) itemGanho).getCnpjClienteSG());
                ps.setInt(3, ((ItensCotacao) itemGanho).getCodigoPro());
                /* executa a atualização */
                ps.executeUpdate();

                //atualia para P o item selecionado
                ps = conn.prepareStatement("update item_cotacao set itemganho = 'P' "
                        + " where cotacao = ? and cgc_forn = ? and cnpj_cliente_sg = ? and codigo_pro = ? ");
                ps.setInt(1, ((ItensCotacao) itemGanho).getCotacao());
                ps.setString(2, ((ItensCotacao) itemGanho).getCnpjFornecedor());
                ps.setString(3, ((ItensCotacao) itemGanho).getCnpjClienteSG());
                ps.setInt(4, ((ItensCotacao) itemGanho).getCodigoPro());
                /* executa a atualização */
                ps.executeUpdate();

                //atualiza todos itens diferentes do fornecedor para N diferentes de X
                ps = conn.prepareStatement("update item_cotacao set itemganho = 'N' where codigo_pro = ? "
                        + " and cnpj_cliente_sg = ? and cotacao = ? and cgc_forn != ? and itemganho != 'X'");
                ps.setInt(1, ((ItensCotacao) itemGanho).getCodigoPro());
                ps.setString(2, ((ItensCotacao) itemGanho).getCnpjClienteSG());
                ps.setInt(3, ((ItensCotacao) itemGanho).getCotacao());
                ps.setString(4, ((ItensCotacao) itemGanho).getCnpjFornecedor());
                /* executa a atualização */
                ps.executeUpdate();

                ps = conn.prepareStatement("update item_cotacao set itemganho = 'S' where itemganho = 'X'"
                        + " and codigo_pro = ? "
                        + " and cnpj_cliente_sg = ? and cotacao = ? ");
                ps.setInt(1, ((ItensCotacao) itemGanho).getCodigoPro());
                ps.setString(2, ((ItensCotacao) itemGanho).getCnpjClienteSG());
                ps.setInt(3, ((ItensCotacao) itemGanho).getCotacao());
                /* executa a atualização */
                ps.executeUpdate();
                conn.commit();
            }

            new Thread(new LogInfoThread(itemGanho, this.getUsuarioLogado())).start();
            return RetornoPersistencia.OK;

        } catch (SGExceptions | SQLException e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            throw e;
        } finally {
            Conexao.closeC(null, ps, null);
        }
    }

    public RetornoPersistencia alterarQtde(BaseBean item) throws Exception {
        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement("update item_cotacao set quantidade = ? where codigo_pro = ? "
                    + " and cnpj_cliente_sg = ? and cotacao = ? and cod_filial = ?");
            ps.setInt(1, ((ItensCotacao) item).getQuantidade());
            ps.setInt(2, ((ItensCotacao) item).getCodigoPro());
            ps.setString(3, ((ItensCotacao) item).getCnpjClienteSG());
            ps.setInt(4, ((ItensCotacao) item).getCotacao());
            ps.setInt(5, ((ItensCotacao) item).getCodFilial());

            /* executa a atualização */
            ps.executeUpdate();

            conn.commit();
            new Thread(new LogInfoThread(item, this.getUsuarioLogado())).start();
            return RetornoPersistencia.OK;

        } catch (SGExceptions | SQLException e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            throw e;
        } finally {
            Conexao.closeC(null, ps, null);
        }
    }

    public RetornoPersistencia salvaItemCotacao(ItensCotacao itensCotacao, Map cotacoes) throws SGExceptions, Exception {
        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement("insert into item_cotacao "
                    + " select ic.codigo_pro, ic.cnpj_cliente_sg, ic.desc_pro, ic.cgc_forn, ic.preco, ic.marca, ic.prazo, ic.unidade, "
                    + " ic.quantidade, ic.qtde_emb, ic.preco_emb, ic.cotacao, ic.perc_desc, ic.perc_acre, ic.prazo1, ic.prazo2, "
                    + " ic.prazo3, ic.codbarra, null, -1, ic.itemganho, ?, -1 "
                    + " from  item_cotacao ic "
                    + " INNER JOIN fornecedor f on f.cnpj = ic.cgc_forn "
                    + " INNER JOIN cotacao c on c.id = ic.fkidcotacao "
                    + " INNER JOIN mercado m on m.fkidcadastrao = c.fkidmercado  where c.cnpj_cliente_sg = ? and c.cotacao = ? and codigo_pro "
                    + " not in (select codigo_pro from item_cotacao where fkidcotacao in (" + cotacoes.get("cotacoes") + ")) group by codigo_pro, cgc_forn ");

            ps.setInt(1, itensCotacao.getCodFilial());
            ps.setString(2, itensCotacao.getCnpjClienteSG());
            ps.setInt(3, itensCotacao.getCotacao());

            ps.executeUpdate();
            conn.commit();

            return RetornoPersistencia.OK;

        } catch (SGExceptions | SQLException ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            //gera excecao de chave duplicado

            if (ex != null && ex.getMessage().contains("Duplicate entry")) {
                throw new SgDupEntryException("Já existe relacionamento com este Item de Cotação" + itensCotacao.getId() + " ");
            } else {
                //colocar aqui outros exceptions
                throw new SgDupEntryException(ex.getMessage());
            }
        } finally {
            Conexao.closeC(null, ps, null);
        }
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
