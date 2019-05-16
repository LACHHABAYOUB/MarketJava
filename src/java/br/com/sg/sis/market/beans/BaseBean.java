package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.Functions;
import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public abstract class BaseBean {

    private Long id;

    public BaseBean() {
        this.setId(-1L);
    }
    
    public BaseBean(ResultSet rs) throws Exception {
        Functions.copyPropertiesRegistroBancoToFormBean(rs, this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}