package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import br.com.sgsistemas.cotacao.cotacaoweb.daos.FilialDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.FornecedorDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.MercadoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.PermUsuarioDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.VendedorDao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class Usuario extends BaseBean {    
    private Cadastrao cadastrao;
    private String login;
    private String senha;
    private String nome;
    private String email;
    private String tipo;
    private String situacao;
    private List<PermUsuario> permissoes;
    private String agrupaCotacao;
    private String chave;
    private Date dtExpiraChave;

    public Usuario() {
        super();
    }
    
    public Usuario(Connection conn, UsuarioLogado usuarioLogado, ResultSet rs) throws Exception {
        super(rs);

        this.setPermissoes(new PermUsuarioDao(usuarioLogado).listar(conn, this.getId()));

        if (this.getTipo().equals("AD")){
            // Se for Administrador, o cadastrão será default 0(zero)
            Mercado mercadoAdm = new Mercado();
            mercadoAdm.setId(0L);
            mercadoAdm.setNome("Administrador");
            mercadoAdm.setClienteSgs(0);
            this.setCadastrao(mercadoAdm);
        }else if (this.getTipo().equals("ME")) {
            this.setCadastrao((Cadastrao) new MercadoDao(usuarioLogado).locate(conn, rs.getLong("fkidcadastrao")));
        } else if (this.getTipo().equals("FO")) {
            this.setCadastrao((Cadastrao) new FornecedorDao(usuarioLogado).locate(conn, rs.getLong("fkidcadastrao")));
        } else if (this.getTipo().equals("FI")) {
            this.setCadastrao((Cadastrao) new FilialDao(usuarioLogado).locate(conn, rs.getLong("fkidcadastrao")));
        } else if (this.getTipo().equals("VE")) {
            this.setCadastrao((Cadastrao) new VendedorDao(usuarioLogado).locate(conn, rs.getLong("fkidcadastrao")));
        }        
    }

    public Usuario(Cadastrao cadastrao, String login, String senha, String nome, String email, String tipo, String situacao, List<PermUsuario> permissoes, String agrupaCotacao, String chave, Date dtExpiraChave) {
        super();
        this.cadastrao = cadastrao;
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.situacao = situacao;
        this.permissoes = permissoes;
        this.agrupaCotacao = agrupaCotacao;
        this.chave = chave;
        this.dtExpiraChave = dtExpiraChave;
    }

    public Cadastrao getCadastrao() {
        return cadastrao;
    }

    public void setCadastrao(Cadastrao cadastrao) {
        this.cadastrao = cadastrao;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<PermUsuario> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(List<PermUsuario> permissoes) {
        this.permissoes = permissoes;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAgrupaCotacao() {
        return agrupaCotacao;
    }

    public void setAgrupaCotacao(String agrupaCotacao) {
        this.agrupaCotacao = agrupaCotacao;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Date getDtExpiraChave() {
        return dtExpiraChave;
    }

    public void setDtExpiraChave(Date dtExpiraChave) {
        this.dtExpiraChave = dtExpiraChave;
    }
	
}