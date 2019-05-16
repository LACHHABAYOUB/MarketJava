package br.com.sgsistemas.cotacao.cotacaoweb.daos;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.ImagemBanner;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ImagemBannerDao extends BaseDao {

    private static final String SQL_LIST = "SELECT id, fkidmercado, descricao, arquivo, nomearquivo FROM imagem_banner WHERE fkidmercado = ?";
    private static final String SQL_LOCATE = "SELECT id, fkidmercado, descricao, arquivo, nomearquivo FROM imagem_banner WHERE id = ?";
    private static final String SQL_INSERT = "INSERT INTO imagem_banner(id, fkidmercado, descricao,  nomearquivo, arquivo) VALUES(null, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE imagem_banner set descricao = ?, arquivo = ?, nomearquivo = ? where id = ?";
    private static final String SQL_DELETE = "delete from imagem_banner where id = ?";
    private static final String SQL_DELETE_REDE = "delete from imagem_banner where fkidmercado = ?";

    public ImagemBannerDao(UsuarioLogado usuarioLogado) {
        super(SQL_LIST, SQL_LOCATE, SQL_INSERT, SQL_UPDATE, SQL_DELETE, usuarioLogado);
    }

    public List<ImagemBanner> SaveList(List<ImagemBanner> list) {
        try {
            for (ImagemBanner imagem : list) {
                this.save(imagem);
            }
        } catch (Exception e) {
            list = null;
        }

        return list;
    }

    public void DeleteRede(Long fkidrede) {

        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement(SQL_DELETE_REDE);
            ps.setLong(1, fkidrede);
            /* executa a exclusao */
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            Conexao.rollback(conn);
        } finally {
            Conexao.closeC(conn, null, null);
        }
    }

    public List<BaseBean> Listar(Integer filtro, String[] params) throws SGExceptions {
        List<BaseBean> resultList = null;

        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement(this.getSqlListar());
            ps.setString(1, params[0]);
            rs = ps.executeQuery();
            
             resultList = new ArrayList<BaseBean>();

            while (rs.next()) {
                resultList.add(new ImagemBanner(rs));
            }
            
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
            Functions.TratarExcecoes(null, e.getMessage());          
        } finally {
            Conexao.closeC(conn, ps, rs);
        }
        return resultList;
    }

    @Override
    public BaseBean locate(Long id)  {
        ImagemBanner imagemBanner = null;

        try {
            conn = Conexao.getC();
            imagemBanner = (ImagemBanner) this.locate(conn, id);
        } catch (Exception e) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
        } finally {
            Conexao.closeC(conn, null, null);
        }

        return imagemBanner;
    }

    @Override
    public BaseBean locate(Connection conn, Long id) throws Exception {
        ImagemBanner imagemBanner = null;

        try {
            ps = conn.prepareStatement(this.getSqlLocate());
            ps.setLong(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                imagemBanner = new ImagemBanner(rs);
            }
        } finally {
            Conexao.closeC(null, ps, rs);
        }

        return imagemBanner;
    }

    @Override
    public RetornoPersistencia save(BaseBean bean) throws Exception {
        ImagemBanner imagemBanner = (ImagemBanner) bean;

        try {
            conn = Conexao.getC();
            ps = conn.prepareStatement(this.getSqlInserir());

            ps.setLong(1, imagemBanner.getFkidmercado());
            ps.setString(2, imagemBanner.getDescricao());
            ps.setString(3, imagemBanner.getNomearquivo());
            ps.setBytes(4, imagemBanner.getArquivo());
          

            ps.executeUpdate();
            conn.commit();

            return RetornoPersistencia.OK;
        } catch (Exception e) {
            Conexao.rollback(conn);
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, e);
            Functions.TratarExcecoes(bean, e.getMessage());
            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, ps, null);
        }

    }

    @Override
    public RetornoPersistencia update(BaseBean bean) throws Exception {
        ImagemBanner imagemBanner = (ImagemBanner) bean;

        try {
            conn = Conexao.getC();

            ps = conn.prepareStatement(this.getSqlAlterar());
            ps.setString(1, imagemBanner.getDescricao());
            ps.setBytes(2, imagemBanner.getArquivo());
            ps.setString(3, imagemBanner.getNomearquivo());
            ps.setLong(4, imagemBanner.getId());
            ps.executeUpdate();
            conn.commit();

            return RetornoPersistencia.OK;
        } catch (Exception e) {
            Conexao.rollback(conn);

            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, e);
            Functions.TratarExcecoes(bean, e.getMessage());
            return RetornoPersistencia.ERRO;
        } finally {
            Conexao.closeC(conn, ps, null);
        }
    }

    @Override
    public Listagem listar(Connection conn, Integer numini, Integer numfim, Integer filtro, String[] params) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long save(Connection conn, BaseBean bean) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Long update(Connection conn, BaseBean bean) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}