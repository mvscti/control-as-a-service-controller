
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import control.Manager;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MARCUS VINICIUS
 */
public class Main {
    
     private static void getVariables(BufferedReader buffer){
        String line;
         try {
             HashMap<String, Number> parameters=new HashMap<>();
             while ((line = buffer.readLine()) != null) {
                 if (line.startsWith("#") || line.trim().isEmpty()) continue;
                 else{
                    String[] value = line.split("=");
                    if (value.length==2 && isNumericAndPositive(value[1])){
                        switch(value[0]){
                            case "scenario":
                                parameters.put("scenario", Integer.parseInt(value[1]));
                                Manager.scenario=Integer.parseInt(value[1]);
                                break;
                            case "n":
                                parameters.put("n", Integer.parseInt(value[1]));
                                break;
                            case "requisitions":
                                parameters.put("requisitions",Integer.parseInt(value[1]));
                                break;
                            case "type1":
                                parameters.put("type1", Integer.parseInt(value[1]));
                                break;
                            case "type2":
                                parameters.put("type2", Integer.parseInt(value[1]));
                                break;
                            case "type3":
                                parameters.put("type3", Integer.parseInt(value[1]));
                                break;
                            case "mips_min":
                                parameters.put("mips_min", Integer.parseInt(value[1]));
                                break;
                            case "mips_max":
                                parameters.put("mips_max", Integer.parseInt(value[1]));
                                break;    
                            case "max_memory":
                                parameters.put("max_memory", Integer.parseInt(value[1]));
                                break; 
                            case "w1":
                                parameters.put("w1", Float.parseFloat(value[1]));
                                break;
                            case "w2":
                                parameters.put("w2", Float.parseFloat(value[1]));
                                break;
                            case "w3":
                                parameters.put("w3", Float.parseFloat(value[1]));
                                break;
                            case "w4":
                                parameters.put("w4", Float.parseFloat(value[1]));
                                break;
                            case "w5":
                                parameters.put("w5", Float.parseFloat(value[1]));
                                break;
                                
                            case "plevel1":
                                parameters.put("plevel1", Integer.parseInt(value[1]));
                                break;
                            case "plevel2":
                                parameters.put("plevel2", Integer.parseInt(value[1]));
                                break;
                            case "plevel3":
                                parameters.put("plevel3", Integer.parseInt(value[1]));
                                break;
                            case "plevel4":
                                parameters.put("plevel4", Integer.parseInt(value[1]));
                                break;
                            case "plevel5":
                                parameters.put("plevel5", Integer.parseInt(value[1]));
                                break;
                            case "plevel6":
                                parameters.put("plevel6", Integer.parseInt(value[1]));
                                break;
                            case "plevel7":
                                parameters.put("plevel7", Integer.parseInt(value[1]));
                                break;
                            case "plevel8":
                                parameters.put("plevel8", Integer.parseInt(value[1]));
                                break;
                            
                            case "tlevel1":
                                parameters.put("tlevel1", Integer.parseInt(value[1]));
                                break;
                            case "tlevel2":
                                parameters.put("tlevel2", Integer.parseInt(value[1]));
                                break;
                            case "tlevel3":
                                parameters.put("tlevel3", Integer.parseInt(value[1]));
                                break;
                            case "tlevel4":
                                parameters.put("tlevel4", Integer.parseInt(value[1]));
                                break;
                            case "tlevel5":
                                parameters.put("tlevel5", Integer.parseInt(value[1]));
                                break;
                            case "low_battery_level":
                                parameters.put("low_battery_level", Integer.parseInt(value[1]));
                                break;
                            case "low_signal_strength":
                                parameters.put("low_signal_strength", Integer.parseInt(value[1]));
                                break;
                                
                        }
                    }else{
                        System.out.println("Error in parsing file simulations.conf");
                        System.exit(1);
                    }
                 }
            }
            //insere os parâmetros para simulação dos nós
            if (verifyParamteters(parameters)) Manager.getInstance().setParameters(parameters);
         } catch (IOException ex) {
             Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
         
    }
    private static boolean verifyParamteters(HashMap<String, Number> parameters){
        if (parameters.size()!=29){
                            System.out.println("Error: wrong parameters numbers in simulations.conf file");
                            System.exit(-1);
        }
        if ((parameters.get("type1").intValue()
                +parameters.get("type2").intValue()+
                parameters.get("type3").intValue())!=100){
            System.out.println("Error: type's node sum is not equal to 100 in simulations.conf file");
            System.exit(-1);
        }
        if((parameters.get("scenario").intValue()<=0) || (parameters.get("scenario").intValue()>=4)){
            System.out.println("Error: scenario given it's no valid in simulations.conf file");
            System.exit(-1);
        }
        //Distribuição de tipos de dispostivos para cenários
        if ((parameters.get("scenario").intValue()==1 && (parameters.get("type1").intValue()+parameters.get("type2").intValue()!=100))
                ||
            (parameters.get("scenario").intValue()==2 && (parameters.get("type3").intValue()!=50))
                ||
            (parameters.get("scenario").intValue()==3 && (parameters.get("type3").intValue()!=100))){
            System.out.println("Error: wrong devices' types values for scenario "+parameters.get("scenario").intValue()+" in simulations.conf file.");
            System.exit(-1);
        }
        if ((parameters.get("w1").floatValue()+
                parameters.get("w2").floatValue()+
                parameters.get("w3").floatValue()+
                parameters.get("w4").floatValue()+
                parameters.get("w5").floatValue())!=1){
            System.out.println("Error: weigths' sum is not equal to 1 in simulations.conf file");
            System.exit(-1);
        }
        if ((parameters.get("plevel1").intValue()>parameters.get("plevel2").intValue())
                ||
                (parameters.get("plevel2").intValue()>parameters.get("plevel3").intValue())
                ||
                (parameters.get("plevel3").intValue()>parameters.get("plevel4").intValue())
                ||
                (parameters.get("plevel4").intValue()>parameters.get("plevel5").intValue())
                ||
                (parameters.get("plevel5").intValue()>parameters.get("plevel6").intValue())
                ||
                (parameters.get("low_battery_level").intValue()>parameters.get("plevel8").intValue())
                ||
                (parameters.get("low_signal_strength").intValue()>parameters.get("plevel8").intValue())){
            System.out.println("Error: check PLEVELs, low_battery_level and low_signal_strength in simulations.conf file");
            System.exit(-1);
        }
            
        if ((parameters.get("tlevel1").intValue()>parameters.get("tlevel2").intValue())
                ||
                (parameters.get("tlevel2").intValue()>parameters.get("tlevel3").intValue())
                ||
                (parameters.get("tlevel3").intValue()>parameters.get("tlevel4").intValue())
                ||
                (parameters.get("tlevel4").intValue()>parameters.get("tlevel5").intValue())){
            System.out.println("Error: check TLEVELs in simulations.conf file");
            System.exit(-1);
        }    
        if (parameters.get("mips_min").intValue()>parameters.get("mips_max").intValue()){
            System.out.println("Error: mips_min is higher than mips_max in simulations.conf file");
            System.exit(-1);
        }
        return true;
    } 
     
    private static boolean isNumericAndPositive(String s) {
        try { 
            if ((Float.parseFloat(s)<0)) return false; 
            
        } catch(NumberFormatException | NullPointerException e) { 
            return false; 
        }
        return true;
    } 
    
    public static void main(String args[]){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("src/simulations.conf"));
            getVariables(br);
            //Inicia a simulação
            Manager.getInstance().start();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
