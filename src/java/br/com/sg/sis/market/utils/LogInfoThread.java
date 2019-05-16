/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.BaseBean;
import br.com.sgsistemas.cotacao.cotacaoweb.beans.LogInfo;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.BaseDao;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.LogInfoDao;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Eduardo A. Lachhab Ayoub
 */
public class LogInfoThread implements Runnable {

	private BaseBean BaseBeanOld = null;
	private BaseBean BaseBeanNew = null;
	private String mensagemLog;
	private LogInfo logInfo;
	private LogInfo.TipoOperacao tipoOperacao;
	private UsuarioLogado usuarioLogado;

	/*Usado para definir inserção*/
	public LogInfoThread(BaseBean p, UsuarioLogado usuarioLogado) {
		try {
			this.BaseBeanNew = p;
			this.tipoOperacao = LogInfo.TipoOperacao.INSERCAO;
			this.logInfo = new LogInfo(LogInfo.TipoOperacao.INSERCAO, usuarioLogado);
			this.usuarioLogado = usuarioLogado;
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
		}
	}

	/*Usado para definir alteração*/
	public LogInfoThread(BaseBean pOld, BaseBean pNew, UsuarioLogado usuarioLogado) {
		try {
			if (pOld == null) {
				LogCotacao.escreveLog(LogCotacao.LEVEL_DEBUG, "Foi solicitado Log de alteração de " + pNew.getClass() + " mas não existe BaseBeanOld");
				this.BaseBeanNew = pNew;
				this.tipoOperacao = LogInfo.TipoOperacao.INSERCAO;
				this.logInfo = new LogInfo(LogInfo.TipoOperacao.INSERCAO, usuarioLogado);
				this.usuarioLogado = usuarioLogado;
			} else {
				this.BaseBeanOld = pOld;
				this.BaseBeanNew = pNew;
				this.tipoOperacao = LogInfo.TipoOperacao.ALTERACAO;
				this.logInfo = new LogInfo(LogInfo.TipoOperacao.ALTERACAO, usuarioLogado);
				this.usuarioLogado = usuarioLogado;
			}
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
		}
	}

	/*Usado para definir exclusão*/
	public LogInfoThread(LogInfo.TipoOperacao to, String msg, UsuarioLogado usuarioLogado) {
		try {
			this.mensagemLog = msg;
			this.tipoOperacao = to;
			this.logInfo = new LogInfo(to, usuarioLogado);
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
		}
	}

    @Override
	public void run() {
		try {
			if (tipoOperacao == LogInfo.TipoOperacao.INSERCAO) {
				gravaLog(formataStringInsercao(), usuarioLogado);
			} else if (tipoOperacao == LogInfo.TipoOperacao.ALTERACAO && BaseBeanNew != null && BaseBeanOld != null) {
				gravaLog(formataStringAlteracao(), usuarioLogado);
			} else {
				gravaLog(mensagemLog, usuarioLogado);
			}
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
		}
	}

	private String formataStringInsercao() throws Exception {
		/*
		 * Método que utiliza reflection para identificar os valores das propriedades a serem inseridas
		 */
		Method method;
		String nomeMetodo;
		ArrayList<Field> fields = new ArrayList<>();
		Class classe = BaseBeanNew.getClass();
		Field[] tempFields;
		StringBuilder insercao = new StringBuilder(classe.getSimpleName() + "[ ");

		while (classe != null) {
			tempFields = classe.getDeclaredFields();
            fields.addAll(Arrays.asList(tempFields));
			classe = classe.getSuperclass();
		}

		for (int i = 0; i < fields.size(); i++) {
			try {
				nomeMetodo = "get" + fields.get(i).getName().substring(0, 1).toUpperCase() + fields.get(i).getName().substring(1);
				method = BaseBeanNew.getClass().getMethod(nomeMetodo);
				Object retornoNew = method.invoke(BaseBeanNew);
				if (!(retornoNew instanceof BaseBean) && !(retornoNew instanceof List)) {
					insercao.append(fields.get(i).getName()).append(": '").append(retornoNew).append("'; ");
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
			}
		}

		return insercao.toString() + " ]";
	}

	private String formataStringAlteracao() throws Exception {
		/*
		 * Método que utiliza reflection para identificar as alterações ocorridas
		 * nos objetos BaseBean informados
		 */
		try {
			Method method;
			String nomeMetodo;
			ArrayList<Field> fields = new ArrayList<>();
			Class classe = BaseBeanNew.getClass();
			Field[] tempFields;
			StringBuilder alteracao = new StringBuilder();
            String idObjeto = (classe != null ? classe.getSimpleName() : "ClasseNull") + "[ ID: "+ BaseBeanOld.getId() + "; ";
			alteracao.append(idObjeto);

			if (!BaseBeanOld.getId().equals(BaseBeanNew.getId())) {
				throw new Exception("Objetos informados não são os mesmos!");
			}

			while (classe != null) {
				tempFields = classe.getDeclaredFields();
				fields.addAll(Arrays.asList(tempFields));
				classe = classe.getSuperclass();
			}

			for (int i = 0; i < fields.size(); i++) {
				try {
					nomeMetodo = "get";
					nomeMetodo += fields.get(i).getName().substring(0, 1).toUpperCase();
					nomeMetodo += fields.get(i).getName().substring(1);

					method = BaseBeanNew.getClass().getMethod(nomeMetodo);
					Object retornoNew = method.invoke(BaseBeanNew);
					Object retornoOld = method.invoke(BaseBeanOld);

					if (retornoOld == null) {
						if (retornoNew != null) {
							alteracao.append(fields.get(i).getName());
							alteracao.append(": DE NULO PARA ");
							alteracao.append(retornoNew);
							alteracao.append("; ");
						}
					} else {
						if (!(retornoOld instanceof BaseBean) && !(retornoOld instanceof List)) {
							if (!retornoOld.equals(retornoNew)) {
								alteracao.append(fields.get(i).getName());
								alteracao.append(": DE '");
								alteracao.append(retornoOld);
								alteracao.append("' PARA '");
								alteracao.append(retornoNew);
								alteracao.append("'; ");
							}
						}
					}

				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
				}
			}

			if (alteracao.toString().equals(idObjeto)) {
				return "";
			}

			return alteracao.toString() + " ]";
		} catch (Exception ex) {
			LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, ex.getMessage(), ex);
			return "";
		}
	}

	private void gravaLog(String msg, UsuarioLogado usuarioLogado) throws Exception {
            if (msg == null || msg.equals("")) {
                return;
            }
            
            if (usuarioLogado == null) {
                return;
            }
            
            logInfo.setDescricao(msg);
            logInfo.setId(0L);
            BaseDao dao = new LogInfoDao(usuarioLogado);

            dao.saveOrUpdate(logInfo);
	}

}
