/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sgsistemas.cotacao.cotacaoweb.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 * @author Lachhab Ayoub
 */
public class UtilitarioService {
    
    public Date dataHoraAtual() {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.getTime();
    }
    
}
