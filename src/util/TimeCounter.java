package util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MARCUS VINICIUS
 */
public class TimeCounter {
    
    /**
     * Conta a diferença de tempo em milisegundos
     * @param t1 Tempo inicial
     * @param t2 Tempo final
     * @return diferença de tempo, em milisegundos
     */
    public  static long timeDifference(LocalDateTime t1, LocalDateTime t2) {
        long diff = ChronoUnit.MILLIS.between(t1, t2);
                
 
        return diff;
    }
}
