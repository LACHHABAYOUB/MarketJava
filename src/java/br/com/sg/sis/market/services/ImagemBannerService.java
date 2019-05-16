/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.services;

import br.com.sgsistemas.cotacao.cotacaoweb.beans.ImagemBanner;
import br.com.sgsistemas.cotacao.cotacaoweb.daos.ImagemBannerDao;
import br.com.sgsistemas.cotacao.cotacaoweb.sgexceptions.SGExceptions;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.Listagem;
import br.com.sgsistemas.cotacao.cotacaoweb.utils.RetornoPersistencia;
import flex.messaging.io.ArrayCollection;
import java.util.List;

/**
 *
 * @author Lachhab Ayoub
 */
public class ImagemBannerService extends ServiceOperations {

    public ImagemBannerService() throws SGExceptions {
        this.setDao(new ImagemBannerDao(this.getUsuarioLogado()));
    }

    
     public ArrayCollection ListarNormal(Integer filtro, String[] params) throws SGExceptions {
         ImagemBannerDao imagemBannerDao = new ImagemBannerDao(this.getUsuarioLogado());
        ArrayCollection listaItens = new ArrayCollection();
        listaItens.addAll(imagemBannerDao.Listar(filtro, params));
        return listaItens;
    }
    
    public String SaveOrUpdate(ImagemBanner imagemBanner) throws Exception {
        return ((new ImagemBannerDao(this.getUsuarioLogado()).saveOrUpdate(imagemBanner) == RetornoPersistencia.OK) ? MSGSAVEUPOK : MSGSAVEUPNAOOK);
    }
    

    public List<ImagemBanner> SaveList(List<ImagemBanner> list) {

		
		if (!list.isEmpty()){
			ImagemBanner imagem = (ImagemBanner) list.get(0);
			(new ImagemBannerDao(this.getUsuarioLogado())).DeleteRede(imagem.getFkidmercado());
			return (new ImagemBannerDao(this.getUsuarioLogado())).SaveList(list);
		}else{
			List<ImagemBanner> resultList =  list;
			return resultList ;
		}
    }

    @Override
     public String Excluir(Long id) throws Exception {
        return ((new ImagemBannerDao(this.getUsuarioLogado()).delete(id) == RetornoPersistencia.OK) ? MSGDELETEOK : MSGDELETENAOOK);
    }
    
    @Override
     public ImagemBanner Locate(Long id) {
        return (ImagemBanner) new ImagemBannerDao(this.getUsuarioLogado()).locate(id);
    }
}
