/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.servlets;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.Conexao;

import br.com.sgsistemas.cotacao.cotacaoweb.utils.Impressao;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.LogCotacao;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author Lachhab Ayoub
 */
public class ServletRelatorios extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest rq, HttpServletResponse rp) throws IOException, ServletException {
		Connection conn = null;

		if (rq.getSession() != null) {
			try {
				conn = Conexao.getC();

				final String relatorio = rq.getParameter("relatorio").toString();
				rp.setContentType("application/pdf");

				Map params = new HashMap();
				params.put("TITLE", relatorio + ".title");
				params.put("PATHRELJASPER", getServletContext().getRealPath("/jasper/").replace("\\", "/") + "/");

				Enumeration enumNames = rq.getParameterNames();
				String paramName = null;

				while (enumNames.hasMoreElements()) {
					paramName = enumNames.nextElement().toString();
					params.put(paramName, rq.getParameter(paramName));
				}

				Class classeRelatorio = Class.forName("br.com.sgsistemas.cotacao.cotacaoweb.servlets." + rq.getParameter("relatorio").toUpperCase());
				Impressao rel = (Impressao) classeRelatorio.newInstance();
				rel.setParametros(conn, params);

				InputStream stream = getServletContext().getResourceAsStream("/jasper/" + rel.nomeRelatorio());

				JasperReport relJasper = null;
				JasperPrint impressao = null;

				try {
					relJasper = (JasperReport) JRLoader.loadObject(stream);
					// Preenche o relatorio
					impressao = JasperFillManager.fillReport(relJasper, params, new JRResultSetDataSource(rel.executaRelatorio()));
					//exporta relatorio para o pdf
					JasperExportManager.exportReportToPdfStream(impressao, rp.getOutputStream());
				} catch (JRException ex) {
					LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
					
					rp.getWriter().println("<table width='100%'>");
					rp.getWriter().println("<tr><td width='100%'><font face='Tahoma'><b>Aten��o!! Não foi poss�vel emitir relat�rio</b></font></td></tr>");
					rp.getWriter().println("<tr><td width='100%'><font face='Tahoma'>" + ex.getMessage() + "</font></td></tr>");
					rp.getWriter().println("</table>");
				} catch (Exception ex) {
					LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, ex);
				}

				conn.commit();
			} catch (Exception e) {
				Conexao.rollback(conn);
				
				LogCotacao.escreveLog(LogCotacao.LEVEL_ERROR, null, e);
				
				rp.getWriter().println("<table width='100%'>");
				rp.getWriter().println("<tr><td width='100%'><font face='Tahoma'><b>Aten��o!! Não foi poss�vel emitir relat�rio</b></font></td></tr>");
				rp.getWriter().println("<tr><td width='100%'><font face='Tahoma'>" + e.getMessage() + "</font></td></tr>");
				rp.getWriter().println("</table>");
			} finally {
				Conexao.closeC(conn, null, null);
			}
		}
	}

}
