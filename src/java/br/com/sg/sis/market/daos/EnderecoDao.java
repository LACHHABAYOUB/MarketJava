package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Endereco;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogInfoThread;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;

/**
 *
 * @author Lachhab Ayoub
 */
public class EnderecoDao extends BaseDao {

    private Long fkIdCadastrao;

    public EnderecoDao(UsuarioLogado usuarioLogado) {
        super("sqlListar",
              "select * from endereco where id = ?",
              "insert into endereco (id, fkidcadastrao, endereco, numero, cep, cidade, bairro, uf) values (null,?,?,?,?,?,?,?)",
              "update endereco set endereco = ?, numero = ?, cep = ?, cidade = ?, bairro = ?, uf = ? where id = ? ",
              "sqlExcluir",
              usuarioLogado);
    }

     public EnderecoDao(UsuarioLogado usuarioLogado, Long fkIdCadastrao) {
         this(usuarioLogado);
         this.fkIdCadastrao = fkIdCadastrao;
    }

    @Override
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BaseBean locate(Connection conn, Long id) {
        Endereco endereco = null;
		
        try {
            ps = conn.prepareStatement(this.getSqlLocate());
            ps.setLong(1, id);
			
			rs = ps.executeQuery();

            if (rs.next()){
                endereco = new Endereco(rs);
            }
        } catch(Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
        } finally {
            Conexao.closeC(null, ps, rs);
        }
		
        return endereco;
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws Exception {
        Endereco endereco = (Endereco) bean;
        
        ps = conn.prepareStatement(this.getSqlInserir());
		
        try {
            ps.setLong(1,fkIdCadastrao);
            ps.setString(2, endereco.getEndereco());
            ps.setString(3, endereco.getNumero());
            ps.setString(4, endereco.getCep());
            ps.setString(5, endereco.getCidade());
            ps.setString(6, endereco.getBairro());
            ps.setString(7, endereco.getUf());

            /* executa a insercao */
            ps.executeUpdate();

            new Thread(new LogInfoThread(endereco, this.getUsuarioLogado())).start();
        } finally {
            Conexao.closeC(null, ps, null);
        }

        return null;
    }

    @Override
    public Long update(Connection conn, BaseBean bean) throws Exception {
        Endereco endereco = ((Endereco)bean);
        Endereco enderecoOld = (Endereco) this.locate(conn, endereco.getId());
		
        try {
            ps = conn.prepareStatement(this.getSqlAlterar());
            ps.setString(1, endereco.getEndereco());
            ps.setString(2, endereco.getNumero());
            ps.setString(3, endereco.getCep());
            ps.setString(4, endereco.getCidade());
            ps.setString(5, endereco.getBairro());
            ps.setString(6, endereco.getUf());
            ps.setLong(7, endereco.getId());

            /* executa a alteracao */
            ps.executeUpdate();
			
            new Thread(new LogInfoThread(enderecoOld, endereco, this.getUsuarioLogado())).start();
        } finally {
            Conexao.closeC(null, ps, null);
        }

        return endereco.getId();
    }

    public Endereco locateByCadastrao(Connection conn, Long idCadastrao) throws SGExceptions {
        Endereco endereco = null;

        try {
            ps = conn.prepareStatement("SELECT e.id, e.fkidcadastrao, e.endereco, e.numero, e.cep, e.cidade, e.bairro, e.uf FROM endereco e where e.fkidcadastrao = ?");            
            ps.setLong(1,  idCadastrao);
            
			rs = ps.executeQuery();

            if (rs.next()){
                endereco = new Endereco(rs);
            }
        } catch (Exception ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
            endereco = null;
            Functions.TratarExcecoes(endereco, ex.getMessage());
        } finally {
            Conexao.closeC(null, ps, rs);
        }
        
        return endereco;
    }

}
