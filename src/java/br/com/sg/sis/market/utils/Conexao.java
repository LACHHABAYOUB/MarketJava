package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Lachhab Ayoub
 */
public abstract class Conexao {
    /* string de conexao com o banco de dados */
    private static final String CLASS = "com.mysql.jdbc.Driver";
    /* string que representa a conexao */
    private static final String CONEXAO = "jdbc:mysql://%s:%s/%s";

    /* string que representa o ip */
    private static String IP = "cotacao.sghost.com.br";
    /* string que representa a porta */
    private static String PORTA = "3306";
    /* string que representa o database */
    private static String DATABASE = "cotacao";
    /* string que representa o usuario */
    private static String USUARIO = "programacao51";
    /* string que representa a senha */
    private static String SENHA = "tP25Aje";

/*
    private static String IP = "192.168.1.199";
    private static String SENHA = "senha1";

    private static String IP = "192.168.1.201";
    private static String USUARIO = "cotaweb";
    private static String SENHA = "";
*/
    static {
        try {
            Class.forName(CLASS);
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

    public static String getConexao() {
        return String.format(CONEXAO, IP, PORTA, DATABASE);
    }

    public static void setIp(String strIp) {
        if(strIp!=null && !strIp.equals(""))
            IP = strIp;
    }

    public static void setPorta(String strPorta) {
        if(strPorta!=null && !strPorta.equals(""))
            PORTA = strPorta;
    }

    public static void setDatabase(String strDatabase) {
        if(strDatabase!=null && !strDatabase.equals(""))
            DATABASE = strDatabase;
    }

    public static void setUsuario(String strUsuario) {
        if(strUsuario!=null && !strUsuario.equals(""))
            USUARIO = strUsuario;
    }

    public static void setConexao(String ip) {
        if(ip!=null && !ip.equals(""))
            IP = ip;
    }

    public static void setSenha(String strSenha) {
        if(strSenha!=null && !strSenha.equals(""))
            SENHA = strSenha;
    }

    public static synchronized Connection getC() throws SGExceptions {
        Connection c = null;
        try {
            c = DriverManager.getConnection(getConexao(), USUARIO, SENHA);
            c.setAutoCommit(false);
        } catch (SQLException sqlE) {
            Functions.TratarExcecoes(null, sqlE.getMessage());
        }
        return c;
    }

    public static synchronized void closeC(Connection conn, Statement s, ResultSet rs) {
        try {
            if(rs != null)
                rs.close();
            if(s != null)
                s.close();
            if(conn != null)
                conn.close();
        } catch (SQLException ex) {
            LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
        }
    }
	
	public static synchronized void rollback(Connection conn) {
		try {
			if (conn != null) {
				conn.rollback();
			}
		} catch (SQLException ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
		}
	}
}
