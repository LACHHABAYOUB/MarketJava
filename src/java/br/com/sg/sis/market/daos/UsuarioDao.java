package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGDuplicateKeyExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.EnviarEmail;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Filtros;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author Lachhab Ayoub
 */
public class UsuarioDao extends BaseDao {

    public UsuarioDao(UsuarioLogado usuarioLogado) {
        super("select id, fkidcadastrao, login, senha, nome, email, chave, dtexpirachave, tipo, situacao, agrupacotacao from usuario",
                "select id, fkidcadastrao, login, senha, nome, email, chave, dtexpirachave, tipo, situacao, agrupacotacao from usuario where id = ?",
                "insert into usuario (id, fkidcadastrao, login, senha, nome, email, tipo, situacao, agrupacotacao) values (null,?,?,md5(?),?,?,?,?,?)",
                "update usuario set login = ?, nome = ?, email = ?, chave = ?, dtexpirachave = ?, situacao = ?, agrupacotacao = ? where id = ?",
                "delete from usuario where id = ?",
                usuarioLogado);
    }

    @Override
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws Exception {
        List<BaseBean> usuarios = new ArrayList<BaseBean>();
        int qtdRegistros;
        String sql = this.getSqlListar();

        if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME")) {
            sql += " where fkidcadastrao in (select id from ("
                    + "(select fkidcadastrao as 'id' from mercado where fkidcadastrao = ?) union all "
                    + "(select fkidcadastrao as 'id' from filial where fkidmercado = ?) union all "
                    + "(select fkidfornecedor as 'id' from mercforn mf where fkidmercado = ?) union all "
                    + "(select fv.fkidvendedor as 'id' from mercfornvend mfv inner join fornvend fv on mfv.fkidfornvend = fv.id where fkidmercado = ?) "
                    + ") usuarios)";
        } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
            sql += " where fkidcadastrao in (select id from ("
                    + "(select fkidcadastrao as 'id' from fornecedor where fkidcadastrao = ?) union all "
                    + "(select fkidvendedor as 'id' from fornvend mf where fkidfornecedor = ?) "
                    + ") usuarios)";
        } else if ((this.getUsuarioLogado().getUsuario().getTipo().equals("FI")) || (this.getUsuarioLogado().getUsuario().getTipo().equals("VE"))) {
            sql += " where fkidcadastrao = ?";
        } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("AD")) {
          //NAO FAZ NADA  
        } else {
            throw new Exception("Tipo de usuario desconhecido");
        }

        try {
            if (this.getUsuarioLogado().getUsuario().getTipo().equals("ME")) {
                if (filtro == Filtros.NOME.ordinal()) {
                    ps = conn.prepareStatement(sql + " and nome like ? order by login");
                    ps.setString(1, params[0]);
                    ps.setString(2, params[0]);
                    ps.setString(3, params[0]);
                    ps.setString(4, params[0]);
                    ps.setString(5, "%" + params[1] + "%");
                } else if (filtro == Filtros.LOGIN.ordinal()) {
                    ps = conn.prepareStatement(sql + " and login like ? order by login");
                    ps.setString(1, params[0]);
                    ps.setString(2, params[0]);
                    ps.setString(3, params[0]);
                    ps.setString(4, params[0]);
                    ps.setString(5, "%" + params[1] + "%");
                } else {
                    ps = conn.prepareStatement(sql + " order by login");
                    ps.setString(1, params[0]);
                    ps.setString(2, params[0]);
                    ps.setString(3, params[0]);
                    ps.setString(4, params[0]);
                }
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("FO")) {
                if (filtro == Filtros.NOME.ordinal()) {
                    ps = conn.prepareStatement(sql + " and nome like ? order by login");
                    ps.setString(1, params[0]);
                    ps.setString(2, params[0]);
                    ps.setString(3, "%" + params[1] + "%");
                } else if (filtro == Filtros.LOGIN.ordinal()) {
                    ps = conn.prepareStatement(sql + " and login like ? order by login ");
                    ps.setString(1, params[0]);
                    ps.setString(2, params[0]);
                    ps.setString(3, "%" + params[1] + "%");
                } else {
                    ps = conn.prepareStatement(sql + " order by login");
                    ps.setString(1, params[0]);
                    ps.setString(2, params[0]);
                }
            } else if ((this.getUsuarioLogado().getUsuario().getTipo().equals("FI"))
                    || (this.getUsuarioLogado().getUsuario().getTipo().equals("VE"))) {
                if (filtro == Filtros.NOME.ordinal()) {
                    ps = conn.prepareStatement(sql + " and nome like ? order by login");
                    ps.setString(1, params[0]);
                    ps.setString(2, "%" + params[1] + "%");
                } else if (filtro == Filtros.LOGIN.ordinal()) {
                    ps = conn.prepareStatement(sql + " and login like ? order by login ");
                    ps.setString(1, params[0]);
                    ps.setString(2, "%" + params[1] + "%");
                } else {
                    ps = conn.prepareStatement(sql + " order by login");
                    ps.setString(1, params[0]);
                }
            } else if (this.getUsuarioLogado().getUsuario().getTipo().equals("AD")) {
                if (filtro == Filtros.NOME.ordinal()) {
                    ps = conn.prepareStatement(sql + " where nome like ? order by login");
                    ps.setString(1, "%" + params[1] + "%");
                } else if (filtro == Filtros.LOGIN.ordinal()) {
                    ps = conn.prepareStatement(sql + " where login like ? order by login ");
                    ps.setString(1, "%" + params[1] + "%");
                } else {   
                    ps = conn.prepareStatement(sql + " order by login");
                }
            }
            rs = ps.executeQuery();

            if (numini <= 1) {
                rs.beforeFirst();
            } else {
                rs.absolute(numini - 1);
            }

            while ((rs.next()) && (rs.getRow() <= numfim)) {
                usuarios.add(new Usuario(conn, this.getUsuarioLogado(), rs));
            }

            rs.last();
            qtdRegistros = rs.getRow();
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return new Listagem(qtdRegistros, usuarios);
    }

    public BaseBean LocateByLogin(String email) throws SGExceptions {
        Usuario usuarioLocated = null;

        try {
            /* cria a conexao com o Banco de Dados */
            conn = Conexao.getC();

            /* prepara a busca pela Situacao Selecionada */
            ps = conn.prepareStatement("select u.id, u.fkidcadastrao, u.nome, u.email, u.login, u.situacao, u.senha, u.chave, u.dtexpirachave, u.tipo,  u.agrupacotacao from usuario u where u.email = ? ");
            ps.setString(1, email);
            /* executa a busca e seta o Resultado em um ResultSet */
            rs = ps.executeQuery();

            if (rs.next()) {
                usuarioLocated = new Usuario(conn, this.getUsuarioLogado(), rs);
            }

        } catch (Exception ex) {
            /* salva no arquivo de log */
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            throw new SGExceptions(ex.getMessage());
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return usuarioLocated;
    }

    @Override
    public BaseBean locate(Connection conn, Long id) throws Exception {
        Usuario usuario = null;

        try {
            ps = conn.prepareStatement(this.getSqlLocate());
            ps.setLong(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(conn, this.getUsuarioLogado(), rs);
            }
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return usuario;
    }

    public BaseBean locatebyIdCadastrao(Long id) throws SGExceptions {
        Usuario usuario = null;
        Connection conn = null;

        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement("select * from usuario where fkidcadastrao = ?");
            ps.setLong(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(conn, this.getUsuarioLogado(), rs);
            }
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            throw new SGExceptions(ex.getMessage());
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return usuario;
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws Exception {
        Long id = null;
        Usuario usuario = (Usuario) bean;

        try {
            ps = conn.prepareStatement(this.getSqlInserir(), PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, usuario.getCadastrao().getId());
            ps.setString(2, usuario.getLogin());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getNome());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getTipo());
            ps.setString(7, usuario.getSituacao());
            ps.setString(8, usuario.getAgrupaCotacao());

            ps.executeUpdate();

            ResultSet resultKey = ps.getGeneratedKeys();
            resultKey.next();
            id = resultKey.getLong("GENERATED_KEY");

            Conexao.closeC(null, null, resultKey);

            new Thread(new LogInfoThread(usuario, this.getUsuarioLogado())).start();

            new PermUsuarioDao(this.getUsuarioLogado()).save(conn, id, usuario.getPermissoes());
        } catch (SQLException ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            //gera excecao de chave duplicado
            if (ex.getMessage().toUpperCase().contains("DUPLICATE ENTRY")) {
                throw new SGDuplicateKeyExceptions("LOGIN: " + usuario.getLogin());
            } else {
                //colocar aqui outros exceptions
                throw new SGExceptions(ex.getMessage());
            }
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            throw new SGExceptions(e.getMessage());
        } finally {
            Conexao.closeC(null, ps, null);
        }

        return id;
    }

    @Override
    public Long update(Connection conn, BaseBean bean) throws Exception {
        Usuario usuario = (Usuario) bean;
        Usuario usuarioOld = (Usuario) this.locate(conn, usuario.getId());
        SimpleDateFormat sdfMySql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            ps = conn.prepareStatement(this.getSqlAlterar());
            ps.setString(1, usuario.getLogin());
            ps.setString(2, usuario.getNome());
            ps.setString(3, usuario.getEmail());
            if (usuario.getChave() != null) {
                ps.setString(4, usuario.getChave());
            } else {
                ps.setNull(4, Types.NULL);
            }
            if (usuario.getDtExpiraChave() != null) {
                ps.setString(5, sdfMySql.format(usuario.getDtExpiraChave()));
            } else {
                ps.setNull(5, Types.NULL);
            }
            ps.setString(6, usuario.getSituacao());
            ps.setString(7, usuario.getAgrupaCotacao());
            ps.setLong(8, usuario.getId());

            ps.executeUpdate();

            new PermUsuarioDao(this.getUsuarioLogado()).save(conn, usuario.getId(), usuario.getPermissoes());

            new Thread(new LogInfoThread(usuarioOld, usuario, this.getUsuarioLogado())).start();
        } catch (SQLException ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            //gera excecao de chave duplicado
            if (ex.getMessage().toUpperCase().contains("DUPLICATE ENTRY")) {
                throw new SGDuplicateKeyExceptions("LOGIN: " + usuario.getLogin());
            } else {
                //colocar aqui outros exceptions
                throw new SGExceptions(ex.getMessage());
            }
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            throw new SGExceptions(e.getMessage());
        } finally {
            Conexao.closeC(null, ps, null);
        }

        return usuario.getId();
    }

    public RetornoPersistencia alterarSenha(Usuario usuario, String senhaAntiga, Boolean considerarSenhaAntiga) throws Exception {
        Connection conn = null;
        Usuario usuarioOld = null;

        try {
            conn = Conexao.getC();

            usuarioOld = (Usuario) this.locate(conn, usuario.getId());

            if (considerarSenhaAntiga) {
                ps = conn.prepareStatement("update usuario set senha = md5(?), chave = null, dtexpirachave = null where id = ? and senha = md5(?)");
                ps.setString(1, usuario.getSenha());
                ps.setLong(2, usuario.getId());
                ps.setString(3, senhaAntiga);
            } else {
                ps = conn.prepareStatement("update usuario set senha = md5(?), chave = null, dtexpirachave = null where id = ? ");
                ps.setString(1, usuario.getSenha());
                ps.setLong(2, usuario.getId());

            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                conn.commit();

                try {
                    if ( Functions.emailValido(usuario.getEmail()) ) {
                        String ls = System.getProperty("line.separator");
                        String mensagem = "Sua senha no sistema Cotação On-Line foi alterada." + ls + ls
                                + "Data de alteração: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ls
                                + "Senha antiga: " + senhaAntiga + ls
                                + "Senha nova: " + usuario.getSenha() + ls + ls + ls
                                + "Este e-mail é enviado automaticamente, por favor não responda." + ls
                                + "Para maiores informações entre em contato." + ls
                                + "Contatos disponíveis no site: http://www.sgsistemas.com.br" + ls + ls + ls
                                + "Atenciosamente," + ls
                                + "Equipe SG Sistemas - Cotação On-Line.";

                        EnviarEmail enviarEmail = new EnviarEmail();
                        enviarEmail.enviar("Cotação On-Line: Sua senha foi alterada com sucesso!", mensagem, null, null, enviarEmail.getEmail(),usuario.getEmail(), "Cotação On-Line");
                    }
                } catch (Exception ex) {
                    LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
                }

                new Thread(new LogInfoThread(usuarioOld, usuario, this.getUsuarioLogado())).start();

                return RetornoPersistencia.OK;
            } else {
                throw new Exception("Senha antiga invalida");
            }
        } catch (Exception ex) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);

            throw ex;
        } finally {
            Conexao.closeC(conn, ps, null);
        }
    }

    public Usuario locate(Connection conn, String login, String senha) throws SGExceptions, Exception {
        Usuario usuario = null;
        try {

            ps = conn.prepareStatement("select id, fkidcadastrao, login, senha, nome, email, chave, dtexpirachave, tipo, situacao, agrupacotacao from usuario where login = ? and senha = md5(?)");
            ps.setString(1, login);
            ps.setString(2, senha);

            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(conn, this.getUsuarioLogado(), rs);
            }
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return usuario;
    }

    public Usuario locate(String login, String senha) throws SGExceptions {
        Usuario usuario = null;
        Connection conn = null;

        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement("select id, fkidcadastrao, login, senha, nome, email, chave, dtexpirachave, tipo, situacao, agrupacotacao from usuario where login = ? and senha = md5(?)");
            ps.setString(1, login);
            ps.setString(2, senha);

            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(conn, this.getUsuarioLogado(), rs);
            }
        } catch (SGExceptions sgE) {
            throw sgE;
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        return usuario;
    }

    public List<BaseBean> listarUsuarioRecuperaSenha(Integer filtro, String[] params) throws SGExceptions {
        List<BaseBean> resultList = null;

        try {
            /* cria a conexao com o Banco de Dados */
            conn = Conexao.getC();

            ps = conn.prepareStatement(this.getSqlListar());

            /* executa a busca e seta o Resultado em um ResultSet */
            rs = ps.executeQuery();

            /* cria a listagem */
            resultList = new ArrayList<BaseBean>();

            while (rs.next()) {
                resultList.add(new Usuario(conn, this.getUsuarioLogado(), rs));
            }

        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, e);
        } finally {
            Conexao.closeC(conn, ps, rs);
        }

        /* retorna o ArrayList */
        return resultList;
    }

    public Boolean alterarSenhaRecuperacao(String[] params, String novaSenha) throws SGExceptions {
        boolean retorno = false;

        try {
            /* cria a conexao com o Banco de Dados */
            conn = Conexao.getC();

            /* prepara a busca pela Situacao Selecionada */
            ps = conn.prepareStatement("update usuario set senha = ?, chave = null, dtexpirachave = null where id = ?");
            ps.setString(1, novaSenha);
            ps.setString(2, params[0]);
            /* executa a busca e seta o Resultado em um ResultSet */
            ps.executeUpdate();

            /* finaliza a transação */
            conn.commit();

            retorno = true;
        } catch (Exception e) {
            Conexao.rollback(conn);
            /* salva no arquivo de log */
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, e);
        } finally {
            Conexao.closeC(conn, ps, null);
        }

        return retorno;
    }
}
