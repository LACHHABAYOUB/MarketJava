package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public class PermUsuario extends BaseBean {
    private String rotina;
    private String menu;
    private String buscar;
    private String inserir;
    private String alterar;
    private String excluir;

    public PermUsuario() {
        super();
    }
    
    public PermUsuario(ResultSet rs) throws Exception {
        super(rs);
    }

    public PermUsuario(String rotina, String menu, String buscar, String inserir, String alterar, String excluir) {
        this.rotina = rotina;
        this.menu = menu;
        this.buscar = buscar;
        this.inserir = inserir;
        this.alterar = alterar;
        this.excluir = excluir;
    }

    public String getAlterar() {
        return alterar;
    }

    public void setAlterar(String alterar) {
        this.alterar = alterar;
    }

    public String getBuscar() {
        return buscar;
    }

    public void setBuscar(String buscar) {
        this.buscar = buscar;
    }

    public String getExcluir() {
        return excluir;
    }

    public void setExcluir(String excluir) {
        this.excluir = excluir;
    }

    public String getInserir() {
        return inserir;
    }

    public void setInserir(String inserir) {
        this.inserir = inserir;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getRotina() {
        return rotina;
    }

    public void setRotina(String rotina) {
        this.rotina = rotina;
    }
}
