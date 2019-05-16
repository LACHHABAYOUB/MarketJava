package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGDuplicateKeyExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SgDupEntryException;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Filtros;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import br.com.sgsistemas.sgmobile.criptografia.SGCripto;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class MercadoDao extends BaseDao {

    public MercadoDao(UsuarioLogado usuarioLogado) {
        super("select c.id, c.nome, c.datacadastro, c.fonecomercial, c.email, c.situacao, m.contato, m.clientesgs, m.cnpj_cliente_sg as 'cnpjClienteSG', m.trabalhaTresCasasDecimais from mercado m inner join cadastrao c on c.id = m.fkidcadastrao",
              "select c.id, c.nome, c.datacadastro, c.fonecomercial, c.email, c.situacao, m.contato, m.clientesgs, m.cnpj_cliente_sg as 'cnpjClienteSG', m.trabalhaTresCasasDecimais from mercado m inner join cadastrao c on c.id = m.fkidcadastrao and c.id = ?",
              "INSERT INTO mercado(fkidcadastrao, contato, clientesgs, cnpj_cliente_sg, trabalhaTresCasasDecimais) VALUES(?,?,?,?,?)",
              "update mercado set contato = ?, clientesgs = ?, trabalhaTresCasasDecimais = ? where fkidcadastrao = ?",
              "delete from cadastrao where id = ?",
              usuarioLogado);
    }

    @Override
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws SGExceptions {
        List<BaseBean> mercados = new ArrayList<BaseBean>();
        int qtdRegistros;

        try {
            if (filtro.intValue() == Filtros.DESCRICAO.ordinal()) {
                ps = conn.prepareStatement(this.getSqlListar() + " where c.id between ? and ? and c.nome like ? order by m.clientesgs");
                ps.setString(3, "%" + params[2] + "%");
            } else if (filtro.intValue() == Filtros.CODIGOSGS.ordinal()) {
                ps = conn.prepareStatement(this.getSqlListar() + " where c.id between ? and ? and m.clientesgs between ? and ? order by m.clientesgs");
                ps.setInt(3, Integer.parseInt(params[2]));
                ps.setInt(4, Integer.parseInt(params[3]));
            } else if (filtro.intValue() == Filtros.CIDADE.ordinal()) {
                ps = conn.prepareStatement(this.getSqlListar() + " inner join endereco e on c.id = e.fkidcadastrao where c.id between ? and ? and e.cidade like ? order by m.clientesgs");
                ps.setString(3, "%" + params[2] + "%");
            } else if (filtro.intValue() == Filtros.CNPJ.ordinal()) {
                ps = conn.prepareStatement(this.getSqlListar() + " where c.id between ? and ? and m.cnpj_cliente_sg = ?");
                ps.setString(3, params[2]);
            } else if (filtro.intValue() == Filtros.FORNECEDOR.ordinal()) {
                ps = conn.prepareStatement(this.getSqlListar() + " inner join mercforn mf where c.id between ? and ? and mf.fkidfornecedor = ? and mf.fkidmercado = m.fkidcadastrao");
                ps.setString(3, params[2]);
            } else {
                ps = conn.prepareStatement(this.getSqlListar() + " where c.id between ? and ? order by m.clientesgs");
            }

            ps.setString(1, params[0]);
            ps.setString(2, params[1]);

            rs = ps.executeQuery();

            if (numini <= 1) {
                //primeira pagina
                rs.beforeFirst();
            } else {
                rs.absolute(numini - 1);
            }

            while ((rs.next()) && (rs.getRow() <= numfim)) {
                Mercado mercado = new Mercado(conn, this.getUsuarioLogado(), rs);
                mercados.add(mercado);
            }

            rs.last();
            qtdRegistros = rs.getRow();
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            //tratamento para SQLException
            Functions.TratarExcecoes(null, e.getMessage());

            return new Listagem(0, null);
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return new Listagem(qtdRegistros, mercados);
    }

    public Boolean locateByCnpj(String cnpj) throws SGExceptions {
        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement("select * from mercado where cnpj_cliente_sg = ?");
            ps.setString(1, cnpj);

            rs = ps.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SGExceptions | SQLException e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            //tratamento para SQLException
            Functions.TratarExcecoes(null, e.getMessage());
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return null;
    }

    @Override
    public BaseBean locate(Connection conn, Long id) throws SGExceptions {
        Mercado mercado = null;

        try {
            ps = conn.prepareStatement(this.getSqlLocate());
            ps.setLong(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                mercado = new Mercado(conn, this.getUsuarioLogado(), rs);
            }
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            //tratamento para SQLException
            Functions.TratarExcecoes(null, e.getMessage());
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return mercado;
    }

    @Override
    public BaseBean locate(Long id) {
        Mercado mercado = null;
        try {
            conn = Conexao.getC();

            mercado = (Mercado) this.locate(conn, id);
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return mercado;
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws SGExceptions {
        try {
            Long fkidcadastrao = new CadastraoDao(this.getUsuarioLogado()).save(conn, bean);

            ps = conn.prepareStatement(this.getSqlInserir());
            ps.setLong(1, fkidcadastrao);
            ps.setString(2, ((Mercado) bean).getContato());
            ps.setInt(3, ((Mercado) bean).getClienteSgs());
            ps.setString(4, ((Mercado) bean).getCnpjClienteSG());

            if (((Mercado) bean).getTrabalhaTresCasasDecimais() == null) {
                ps.setNull(5, Types.NULL);
            } else {
                ps.setString(5, ((Mercado) bean).getTrabalhaTresCasasDecimais());
            }

            /* executa a insercao */
            ps.executeUpdate();

            bean.setId(fkidcadastrao);

            String loginSenha = ((Mercado) bean).getCnpjClienteSG();
            Usuario usuario = new Usuario(((Mercado) bean), loginSenha, loginSenha, ((Mercado) bean).getNome(), ((Mercado) bean).getEmail(), "ME", "A", Functions.getPermissoesMercado(), "N", "", null);

            new UsuarioDao(this.getUsuarioLogado()).save(conn, usuario);

            new Thread(new LogInfoThread(((Mercado) bean), this.getUsuarioLogado())).start();

            if (((Mercado) bean).getFilial() != null) {
                new FilialDao(this.getUsuarioLogado()).saveOrUpdate(conn, ((Mercado) bean).getFilial(), (Mercado) bean);
            }

            return fkidcadastrao;
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            //gera excecao de chave duplicado

            if (ex instanceof SGDuplicateKeyExceptions){
                throw (SGDuplicateKeyExceptions) ex;
            } else if (ex != null && ex.getMessage().contains("Duplicate entry")) {
                throw new SgDupEntryException("Já existe relacionamento com este CNPJ " + ((Mercado) bean).getCnpjClienteSG() + " ");
            } else {
                //colocar aqui outros exceptions
                throw new SGExceptions("Falha ao salvar registro de Mercado...", ex);
            }
        }
    }

    @Override
    public Long update(Connection conn, BaseBean bean) throws Exception {
        Mercado mercado = ((Mercado) bean);
        Mercado mercadoOld = (Mercado) this.locate(conn, mercado.getId());
        Long fkidcadastrao = new CadastraoDao(this.getUsuarioLogado()).update(conn, bean);

        ps = conn.prepareStatement(this.getSqlAlterar());
        ps.setString(1, ((Mercado) bean).getContato());
        ps.setInt(2, ((Mercado) bean).getClienteSgs());

        if (((Mercado) bean).getTrabalhaTresCasasDecimais() == null) {
            ps.setNull(3, Types.NULL);
        } else {
            ps.setString(3, ((Mercado) bean).getTrabalhaTresCasasDecimais());
        }

        ps.setLong(4, fkidcadastrao);

        /* executa a insercao */
        ps.executeUpdate();

        new Thread(new LogInfoThread(mercadoOld, mercado, this.getUsuarioLogado())).start();

        return fkidcadastrao;
    }

}
