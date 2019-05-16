
package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.UsuarioLogado;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public class Vendedor extends Cadastrao {
    private String foneResidencial;
    private String foneCelular;
    private String cpf;
    private Fornecedor fornecedor;

    public Vendedor() {
        super();
    }

    public Vendedor(Connection conn, UsuarioLogado usuarioLogado, ResultSet rs) throws Exception {
        super(conn, usuarioLogado, rs);
    }   

    public Vendedor(Connection conn, UsuarioLogado usuarioLogado, ResultSet rs, Fornecedor fornecedor) throws Exception {
        this(conn, usuarioLogado, rs);
        this.setFornecedor(fornecedor);
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getFoneCelular() {
        return foneCelular;
    }

    public void setFoneCelular(String foneCelular) {
        this.foneCelular = foneCelular;
    }

    public String getFoneResidencial() {
        return foneResidencial;
    }

    public void setFoneResidencial(String foneResidencial) {
        this.foneResidencial = foneResidencial;
    }

    public Fornecedor getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Fornecedor fornecedor) {
        this.fornecedor = fornecedor;
    }
}