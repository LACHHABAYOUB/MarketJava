package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import br.com.sgsistemas.cotacao.cotacaoweb.daos.EnderecoDao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Lachhab Ayoub
 */
public class Cadastrao extends BaseBean {
    private String nome;
    private String dataCadastro;
    private String foneComercial;
    private String email;
    private String situacao;
    private Endereco endereco;

    public Cadastrao() {
        super();
    }

    public Cadastrao(Connection conn, UsuarioLogado usuarioLogado, ResultSet rs) throws Exception {
        super(rs);
                
        this.setEndereco(new EnderecoDao(usuarioLogado).locateByCadastrao(conn, this.getId()));
    }   
    
    public String getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(String dataCadastro) {
        SimpleDateFormat sdfmysql = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfBr = new SimpleDateFormat("dd/MM/yyyy");
         try {
            this.dataCadastro = sdfBr.format(sdfmysql.parse(dataCadastro));
        } catch (ParseException ex) {
            this.dataCadastro = dataCadastro;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoneComercial() {
        return foneComercial;
    }

    public void setFoneComercial(String foneComercial) {
        this.foneComercial = foneComercial;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
}
