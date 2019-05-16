package br.com.sgsistemas.cotacao.cotacaoweb.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Lachhab Ayoub
 */
public class CotacaoContextListener implements ServletContextListener {

    private static EnvioEmailNovosPedidosThread envioEmailNovosPedidos = new EnvioEmailNovosPedidosThread();

	private static EnvioEmailNovasCotacoesThread envioEmailNovasCotacoes = new EnvioEmailNovasCotacoesThread();

    private static EnvioEmailPedidosErroThread envioEmailPedidosErro = new EnvioEmailPedidosErroThread();

    private static EnvioEmailCotacoesErroThread envioEmailCotacoesErro = new EnvioEmailCotacoesErroThread();
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /* cria conexao com o banco de dados atraves do hibernate */
//        HibernateHelper.initSessionFactory();

        /* atualiza a senha da classe de conexão */
        String ipServerBanco = sce.getServletContext().getInitParameter("ipServerBanco");
        String portaServerBanco = sce.getServletContext().getInitParameter("portaServerBanco");
		String databaseServerBanco = sce.getServletContext().getInitParameter("databaseServerBanco");
        String senhaServerBanco = sce.getServletContext().getInitParameter("senhaServerBanco");
        String usuarioServerBanco = sce.getServletContext().getInitParameter("usuarioServerBanco");

        if (ipServerBanco != null) {
            Conexao.setConexao(ipServerBanco);
        }
		
        if (portaServerBanco != null) {
            Conexao.setPorta(portaServerBanco);
        }
		
		if (databaseServerBanco != null) {
            Conexao.setDatabase(databaseServerBanco);
        }
		
        if (senhaServerBanco != null) {
            Conexao.setSenha(senhaServerBanco);
        }
		
        if (usuarioServerBanco != null) {
            Conexao.setUsuario(usuarioServerBanco);
        }

        // Inicia a verificação de novos pedidos, para enviar email aos vendedores
        new Thread(envioEmailNovosPedidos).start();

		// Inicia a verificação de novas cotacoes, para enviar email aos vendedores
        new Thread(envioEmailNovasCotacoes).start();
        
        new Thread(envioEmailPedidosErro).start();
        
        new Thread(envioEmailCotacoesErro).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* fecha conexao com o banco de dados atraves do hibernate */
//        HibernateHelper.closeFactory();

        envioEmailNovosPedidos.interromper();
		envioEmailNovasCotacoes.interromper();
    }
}
