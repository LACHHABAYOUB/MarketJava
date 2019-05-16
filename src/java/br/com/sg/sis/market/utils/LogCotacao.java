package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import org.apache.log4j.Logger;

/**
 *
 * @author Eduardo A. Lachhab Ayoub
 */
public class LogCotacao {

    /*Usadas para controlar o nível do log*/
    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_ERROR = 2;

    /*Usado para mensagens de debug durante a execução. Mensagens do tipo: "passei por aqui"*/
    private static Logger logDebug = null;
    /*Usado para mensagens de controle do sistema. Mensagens do tipo: "Iniciada a operação de fechamento"*/
    private static Logger logInfo = null;
    /*Usado para as exceções do sistema*/
    private static Logger logError = null;

    static{
        logDebug = Logger.getLogger("cotacaodebug");
        logInfo = Logger.getLogger("cotacaoinfo");
        logError = Logger.getLogger("cotacaoerror");
    }

    public static void escreveLog(int nivel, String msg, Throwable ex){
        switch (nivel){
            case LEVEL_DEBUG:
                logDebug.debug(msg, ex);
            break;
            case LEVEL_INFO:
                logInfo.info(msg, ex);
            break;
            case LEVEL_ERROR:
                logError.error(msg, ex);
            break;
        }
    }

    public static void escreveLog(int nivel, String msg){
        switch (nivel){
            case LEVEL_DEBUG:
                logDebug.debug(msg);
            break;
            case LEVEL_INFO:
                logInfo.info(msg);
            break;
            case LEVEL_ERROR:
                logError.error(msg);
            break;
        }
    }

    public static void escreveLog(int nivel, Throwable ex ){
        switch (nivel){
            case LEVEL_DEBUG:
                logDebug.debug("", ex);
            break;
            case LEVEL_INFO:
                logInfo.info("", ex);
            break;
            case LEVEL_ERROR:
                logError.error("", ex);
            break;
        }
    }
}