package br.com.sgsistemas.cotacao.cotacaoweb.beans;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author Lachhab Ayoub
 */
public class Parametro extends BaseBean {

        private String nomeParametro;
        private String valorParametro;

        public Parametro() {
                super();
        }

        public Parametro(Connection conn, ResultSet rs) throws Exception {
                super(rs);
        }

        public String getNomeParametro() {
                return nomeParametro;
        }

        public void setNomeParametro(String nomeParametro) {
                this.nomeParametro = nomeParametro;
        }

        public String getValorParametro() {
                return valorParametro;
        }

        public void setValorParametro(String valorParametro) {
                this.valorParametro = valorParametro;
        }
}
