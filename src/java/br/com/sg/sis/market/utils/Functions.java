package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Cotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Filial;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Fornecedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.ItensCotacao;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Mercado;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.PermUsuario;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Vendedor;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.Usuario;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGCanNotBeNullException;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SgDupEntryException;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SgFkException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 *
 * @author Lachhab Ayoub
 */
public abstract class Functions {

    /**
     * Monta objetos atraves de ResultSet.
     *
     * @param rs ResultSet utilizado como fonte dos dados.
     * @param obj Objeto a ser montado.
     */
    public static void copyPropertiesRegistroBancoToFormBean(ResultSet rs, Object obj) throws Exception {
        Method method = null;
        Object valorParam = null;
        ArrayList<Field> fields = new ArrayList<Field>();

        Class classe = obj.getClass();
        Field[] tempFields = null;
        while (classe != null) {
            tempFields = classe.getDeclaredFields();
            fields.addAll(Arrays.asList(tempFields));
            classe = classe.getSuperclass();
        }

        for (int i = 0; i < fields.size(); i++) {
            try {
                String nomeMetodo = "set" + fields.get(i).getName().substring(0, 1).toUpperCase() + fields.get(i).getName().substring(1);

                if (fields.get(i).getType() == String.class) {
                    valorParam = rs.getString(fields.get(i).getName().toLowerCase());
                    method = obj.getClass().getMethod(nomeMetodo, String.class);
                } else if (fields.get(i).getType() == Character.class) {
                    valorParam = new Character(rs.getString(fields.get(i).getName().toLowerCase()).charAt(0));
                    method = obj.getClass().getMethod(nomeMetodo, Character.class);
                } else if (fields.get(i).getType() == Integer.class) {
                    valorParam = rs.getInt(fields.get(i).getName().toLowerCase());
                    method = obj.getClass().getMethod(nomeMetodo, Integer.class);
                } else if (fields.get(i).getType() == Date.class) {
                    valorParam = rs.getTimestamp(fields.get(i).getName().toLowerCase());
                    method = obj.getClass().getMethod(nomeMetodo, Date.class);
                } else if (fields.get(i).getType() == Double.class) {
                    valorParam = rs.getDouble(fields.get(i).getName().toLowerCase());
                    method = obj.getClass().getMethod(nomeMetodo, Double.class);
                } else if (fields.get(i).getType() == Float.class) {
                    valorParam = rs.getFloat(fields.get(i).getName().toLowerCase());
                    method = obj.getClass().getMethod(nomeMetodo, Float.class);
                } else if (fields.get(i).getType() == Time.class) {
                    valorParam = rs.getTime(fields.get(i).getName().toLowerCase());
                    method = obj.getClass().getMethod(nomeMetodo, Time.class);
                } else if (fields.get(i).getType() == Long.class) {
                    valorParam = rs.getLong(fields.get(i).getName().toLowerCase());
                    method = obj.getClass().getMethod(nomeMetodo, Long.class);
                } else if (fields.get(i).getType() == Boolean.class) {
                    if (rs.getString(fields.get(i).getName().toLowerCase()).toUpperCase().equals("S")) {
                        valorParam = true;
                    } else {
                        valorParam = false;
                    }
                    method = obj.getClass().getMethod(nomeMetodo, Boolean.class);
                } else if (fields.get(i).getType() == byte[].class) {
                    valorParam = rs.getBytes(fields.get(i).getName().toLowerCase());
                    method = obj.getClass().getMethod(nomeMetodo, byte[].class);
                }

                if (method != null) {
                    method.invoke(obj, valorParam);
                }
            } catch (Exception e) {
                LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, e);
            }
            
            valorParam = method = null;
        }
    }

    public static List<PermUsuario> getPermissoesMercado() {
        List<PermUsuario> permissoes = new ArrayList<PermUsuario>();

        permissoes.add(new PermUsuario("Mercado", "S", "S", "N", "S", "N"));
        permissoes.add(new PermUsuario("Filial", "S", "S", "S", "S", "S"));
        permissoes.add(new PermUsuario("Fornecedor", "S", "S", "S", "S", "S"));
        permissoes.add(new PermUsuario("Vendedor", "S", "S", "S", "S", "S"));
        permissoes.add(new PermUsuario("Usuario", "S", "S", "S", "S", "S"));
        permissoes.add(new PermUsuario("ListarCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ImprimirCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ExcluirCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("FecharCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ReabrirCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("DigitarCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarPedido", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarPedidonaoGanho", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ImprimirPedido", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarSenha", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarSenhaUsuarios", "S", "N", "N", "N", "N"));

        return permissoes;
    }

    public static List<PermUsuario> getPermissoesFornecedor() {
        List<PermUsuario> permissoes = new ArrayList<PermUsuario>();

        permissoes.add(new PermUsuario("Mercado", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Filial", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Fornecedor", "S", "S", "N", "S", "N"));
        permissoes.add(new PermUsuario("Vendedor", "S", "S", "S", "S", "S"));
        permissoes.add(new PermUsuario("Usuario", "S", "S", "S", "S", "S"));
        permissoes.add(new PermUsuario("ListarCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ImprimirCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ExcluirCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("FecharCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ReabrirCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("DigitarCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarPedido", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarPedidonaoGanho", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ImprimirPedido", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarSenha", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarSenhaUsuarios", "S", "N", "N", "N", "N"));

        return permissoes;
    }

    public static List<PermUsuario> getPermissoesFilial() {
        List<PermUsuario> permissoes = new ArrayList<PermUsuario>();

        permissoes.add(new PermUsuario("Mercado", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Filial", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Fornecedor", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Vendedor", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Usuario", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ImprimirCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ExcluirCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("FecharCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ReabrirCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("DigitarCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarPedido", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarPedidonaoGanho", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ImprimirPedido", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarSenha", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarSenhaUsuarios", "N", "N", "N", "N", "N"));

        return permissoes;
    }

    public static List<PermUsuario> getPermissoesVendedor() {
        List<PermUsuario> permissoes = new ArrayList<PermUsuario>();

        permissoes.add(new PermUsuario("Mercado", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Filial", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Fornecedor", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("Vendedor", "S", "S", "N", "S", "N"));
        permissoes.add(new PermUsuario("Usuario", "S", "S", "N", "S", "N"));
        permissoes.add(new PermUsuario("ListarCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ImprimirCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ExcluirCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("FecharCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ReabrirCotacao", "N", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("DigitarCotacao", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarPedido", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ListarPedidonaoGanho", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("ImprimirPedido", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarSenha", "S", "N", "N", "N", "N"));
        permissoes.add(new PermUsuario("AlterarSenhaUsuarios", "N", "N", "N", "N", "N"));

        return permissoes;
    }

    public static void TratarExcecoes(BaseBean bean, String msg) throws SGExceptions {

        if (msg.contains(Constants.FOREIGNKEYVIOLATION)) {
            if (bean instanceof Cotacao) {
                throw new SGExceptions("Uma ou Mais Cotações Selecionadas Possui Pedido Para Ela. \n Deve Apagar o Pedido para Conseguir Apagar a Cotação");
            } else if (bean instanceof Filial) {
                if (msg.contains("fk_usuarifkidcadas")) {
                    throw new SGExceptions("Não é possível excluir a filial. \nExiste usuário cadastrado para a filial selecionada");
                }
            } else if (bean instanceof Mercado) {
                if (msg.contains("fk_usuarifkidcadas")) {
                    throw new SGExceptions("Não é possível excluir o mercado. \nExiste usuário cadastrado para o mercado selecionado");
                } else if (msg.contains("fk_filialfkidmerca")) {
                    throw new SGExceptions("Não é possível excluir o mercado. \nExiste filial cadastrada para o mercado selecionado");
                }
            }

            throw new SgFkException(getTabelaForeignKeyViolation(msg), getCampoForeignKeyViolation(msg));
        } else if (msg.contains(Constants.CANNOTBENULL)) {
            throw new SGCanNotBeNullException(getCampoCanNotBeNull(msg));
        } else if (msg.contains(Constants.UNIQUEKEYVIOLATION)) {
            String complementoMsg = "";
            if (msg.contains("key 2")) {
                complementoMsg = " Chave duplicada!";
            } else if (msg.contains("key 1")) {
                complementoMsg = " Chave primária duplicada!";
            } else {
                if (bean instanceof Mercado) {
                    /*Trata exceção de unique key do associado e da rede*/
                    complementoMsg = " Campos referente ao Mercado esta duplicado!";
                } else if (bean instanceof Filial) {
                    if (msg.contains("uk_filialcodfilial")) {
                        throw new SGExceptions("Já existe filial usando este código.");
                    } else {		
                        /*Trata exceção de unique key do associap*/
                        complementoMsg = " Campos referente a Filial esta duplicado!";
                    }
                } else if (bean instanceof Fornecedor) {
                    /*Trata exceção de unique key do item pedido*/
                    complementoMsg = " Campos referente ao Fornecedor esta duplicado!";
                } else if (bean instanceof Vendedor) {
                    /*Trata exceção de unique key do pedido rede*/
                    complementoMsg = " Campos referente ao Vendedor esta duplicado!";
                } else if (bean instanceof Usuario) {
                    /*Trata exceção de unique key do perm usuario*/
                    complementoMsg = " Campos referente ao Usuário esta duplicados!";
                } else if (bean instanceof PermUsuario) {
                    /*Trata exceção de unique key do usuario*/
                    complementoMsg = " Campo referente ao Permissão do usuário está duplicado!";
                }

                throw new SgDupEntryException("Falha ao manipular tabela de " + bean.getClass().getSimpleName() + "." + complementoMsg);
            }
        } else if (msg.contains(Constants.CONNECTIONFAIL)) {
            throw new SGExceptions("Porta de Conexão Incorreta! \nContate o administrador da rede para que os parâmetros de conexão sejam corrigidos!");
        } else if (msg.contains(Constants.CONNECTIONADRESSFAIL)) {
            throw new SGExceptions("Endereço de Conexão Inválido! \nContate o administrador da rede para que os parâmetros de conexão sejam corrigidos!");
        } else if (msg.contains(Constants.CONNECTIONACESSFAIL)) {
            throw new SGExceptions("Usuário ou Senha de conexão incorreta! \nContate o administrador da rede para que os parâmetros de conexão sejam corrigidos!");
        } else {
            throw new SGExceptions(msg);
        }
    }

    private static String getCampoCanNotBeNull(String msg) {
        return msg.substring(msg.indexOf(" '") + 1, msg.indexOf("' ")).toUpperCase();
    }

    private static String getTabelaForeignKeyViolation(String msg) {
        return msg.substring(msg.indexOf("/") + 1, msg.indexOf("`,")).toUpperCase();
    }

    private static String getCampoForeignKeyViolation(String msg) {
        return msg.substring(msg.indexOf("` (`") + 4, msg.indexOf("`))")).toUpperCase();
    }
    
    public static boolean emailValido(String email){
        boolean result = true;
        try {
            InternetAddress[] emailAddr = InternetAddress.parse(email);
            for(int i=0; i<emailAddr.length;i++){
                emailAddr[i].validate();
            }
        } catch (Exception ex) {
           result = false;
        }
        return result;
    }
    
    public static String stack2String(Exception e){
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
    
    public static boolean precoEmbValido(BigDecimal preco){
        final Integer QUANT_MAX_DIGITOS_PRECO_EMB = 7;
        return Long.toString(preco.longValue()).length() <= QUANT_MAX_DIGITOS_PRECO_EMB;
    }
    
    public static boolean precoUnitValido(BigDecimal preco){
        final Integer QUANT_MAX_DIGITOS_PRECO_UNIT = 3;
        return Long.toString(preco.longValue()).length() <= QUANT_MAX_DIGITOS_PRECO_UNIT;
    }
    
    public static BigDecimal parsePrecoEmb(ItensCotacao itemCotacao) throws SGExceptions{
        BigDecimal precoEmb = new BigDecimal(itemCotacao.getPrecoEmb().replace(",", ".").replaceAll("[^\\d.]+", ""));

        if (!Functions.precoEmbValido(precoEmb)) {
            throw new SGExceptions("Preço de embalagem inválido: " + precoEmb.toString());
        }

        return precoEmb.setScale(3, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal parsePrecoUnit(ItensCotacao itemCotacao) throws SGExceptions{
        BigDecimal precoUnit = new BigDecimal(itemCotacao.getPreco().replace(",", ".").replaceAll("[^\\d.]+", ""));

        if (!Functions.precoUnitValido(precoUnit)) {
            throw new SGExceptions("Preço de unitário inválido: " + precoUnit.toString());
        }

        return precoUnit.setScale(3, RoundingMode.HALF_UP);
    }
}
