/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MARCUS VINICIUS
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import util.InverseGammaDistribution;
public class Test {
    public static void main(String args[]) throws IOException{
InverseGammaDistribution invgamma=new InverseGammaDistribution(0.5, 10);
//System.out.println(invgamma.pdf(0.1d));
//System.exit(1);
FileWriter csvWriter = new FileWriter("cdf.csv");
csvWriter.append("X");
csvWriter.append(";");
csvWriter.append("Y");
csvWriter.append("\n");
int i=1;
while(i<1000){
    Random randomSNR = new Random();
    csvWriter.append(i+";"+ invgamma.pdf(randomSNR.nextDouble()));
    csvWriter.append("\n");
    i++;
}

//for (int i=1; i<10000; i++) {
//  csvWriter.append(i*0.01+","+ invgamma.cdf(i*0.01));
//    csvWriter.append("\n");
//}

csvWriter.flush();
csvWriter.close();
        
        
    }
}
