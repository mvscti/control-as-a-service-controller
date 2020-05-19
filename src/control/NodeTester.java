/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MARCUS VINICIUS
 */
public class NodeTester implements Observable{
    private static Node node1, node2;
    private static final Scanner sc1 = new Scanner(System.in); 
    private static final Byte id=2;
    public NodeTester(){
       Byte id=0;
       float w1=(float) 0.2, w2=(float)0.2, w3=(float) 0.2, w4=(float) 0.2, w5=(float) 0.2;
       node1=new Node(id,
       90, 1035,
               4096, 70, (float) 15.0, (byte) 0, w1,w2,w3, w4, w5,
               0, 256, 512, 768, 1024,
               0,4375,8750,13125,17500,21875, 26250, 30625 );
       id++;
       /*node2=new Node(id,
       true,30, 4091,
               4096, 53, (float) 10.0, (byte) 0, w1,w2,w3, w4, w5,
               0, 256, 512, 768, 1024,
               0,4375,8750,13125,17500,21875, 26250, 30625 );*/
        /*node2=new Node(id,
       true,90, 1035,
               4096,70, (float) 15.0, (byte) 0, w1,w2,w3, w4, w5,
               0, 256, 512, 768, 1024,
               0,4375,8750,13125,17500,21875, 26250, 30625 );*/
       Manager.getInstance().addNode(node1);
       Manager.getInstance().addNode(node2);
       Manager.getInstance().addNode(this);
       Thread t1= node1;
       Thread t2=node2;
        try {
            Thread.sleep(100);
            t1.start();
            //t2.start();
            Manager.getInstance().broadcast(getNodeId(), "rank=1.0");
            Thread.sleep(100);
            //Manager.getInstance().broadcast(getNodeId(), "new_controller");
        } catch (InterruptedException ex) {
            Logger.getLogger(NodeTester.class.getName()).log(Level.SEVERE, null, ex);
        }
       
       
       
       
    }
    
    @Override
    public void receive(String msg) {
        System.out.println("Recebido "+msg);
    }

    @Override
    public void receive(String msg, Byte nodeId) {
        System.out.println("Recebido "+msg+" de "+nodeId);
    }

    @Override
    public Byte getNodeId() {
        return id;
    }

   
    public void start() {
        
    }
    
    
    
//    public static void main(String args[]){
//       Byte id=0;
//       float w1=(float) 0.2, w2=(float)0.2, w3=(float) 0.2, w4=(float) 0.2, w5=(float) 0.2;
//       node1=new Node(id,
//       true,90, 1035,
//               4096, 70, (float) 1.0, (byte) 0, w1,w2,w3, w4, w5,
//               128, 256, 512, 768, 1024,
//               1500,4375,8750,13125,17500,21875, 26250, 30625 );
//    
//       node1.calculateRank(4376, (float) 19, 40, 3800, true, new Byte("0"));
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(NodeTester.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        node1.calculateRank(4376, (float) 20, 40, 3800, true, new Byte("0"));
//        String text="";
//        while(!"quit".equals(text)){
//            
//        }
//    }
    
    

    
    public static void main(String args[]){
        NodeTester node=new NodeTester();
        String text="";
        while(!"quit".equals(text)){
            text=sc1.nextLine();
            switch(text){
                case "r":
                    System.out.println("Nó "+node1.getNodeId()+": "+node1.getRank());
                    //System.out.println("Nó "+node2.getNodeId()+": "+node2.getRank());
                    break;  
                case "new_controller":
                    Manager.getInstance().broadcast(id, "new_controller");
                    break;
                case "change_controller":
                    Manager.getInstance().unicast((byte) 0 , "change_controller");
                    break;
                case "stop":
                    Manager.getInstance().broadcast(id,"stop");
                    break;
                case "join":
                    Manager.getInstance().unicast((byte) 0,"join");
                    break;    
                default:
                    Manager.getInstance().unicast(text);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public int getBatteryLevel() {
        return 75;
    }

    @Override
    public int getMemoryAvaliable() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stopNode() {
        System.out.println("Solicitou parar");
    }

    @Override
    public void manageCharacteristics() {
        
    }

    @Override
    public double getRank() {
        return -1;
    }

    @Override
    public int getAmountMemory() {
        return -1;
    }

    @Override
    public void sendCharactheristicsToController() {
        
    }

    @Override
    public double getW1() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getW2() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getW3() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getW4() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getW5() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setW1(double aW1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setW2(double aW2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setW3(double aW3) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setW4(double aW4) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setW5(double aW5) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMobilityValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float getSignalStrentch() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMips() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMipsValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDiffRank(double diff) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Number getCI(Byte ci, Byte nodeId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
