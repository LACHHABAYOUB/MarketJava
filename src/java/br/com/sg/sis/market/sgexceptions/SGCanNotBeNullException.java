package br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions;

/**
 *
 * @author Lachhab Ayoub
 */
public class SGCanNotBeNullException extends SGExceptions{

    public SGCanNotBeNullException(String coluna) {
        super("O campo: " + coluna + " não pode ser nulo!");
    }
}
