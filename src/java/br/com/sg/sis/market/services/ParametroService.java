package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.Parametro;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.ParametroDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class ParametroService extends ServiceOperations {

        public ParametroService() throws SGExceptions {
                this.setDao(new ParametroDao(this.getUsuarioLogado()));
        }

        public String Save(List<Parametro> parametros) throws Exception {
                ((ParametroDao) getDao()).deletarTodos();

                RetornoPersistencia rp = null;
                for (Parametro parametro : parametros) {
                        rp = ((ParametroDao) getDao()).save(parametro);

                        if (rp != RetornoPersistencia.OK) {
                                break;
                        }
                }

                return ((rp == RetornoPersistencia.OK) ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
        }
}
