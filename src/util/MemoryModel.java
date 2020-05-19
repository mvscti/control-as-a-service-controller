/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author MARCUS VINICIUS
 * Classe que gera valores utilizando a distibuição Gamma Inverso com
 * função PDF. Alfa=0.5 e Beta=10
 */
public final class MemoryModel {
    private static float[] memoryValues=new float[1000];    
    private static Random randomMemoryPosition=new Random();
    private static MemoryModel instance=null;
    static{
        BufferedReader br = null;
        int i=0;
        try {
            br = new BufferedReader(new FileReader("src/resources/gama-dist.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] value = line.split(";");
                memoryValues[i]=Float.parseFloat(value[0]);
                ++i;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MemoryModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MemoryModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static MemoryModel getInstance(){
        if (instance==null) instance=new MemoryModel();
        return instance;
    }
    private MemoryModel(){
        
    }
    
    synchronized public float getMemoryVariate(){
        return memoryValues[randomMemoryPosition.nextInt(300)+randomMemoryPosition.nextInt(700)];
    }

   
    
}
