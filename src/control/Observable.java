/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

/**
 *
 * @author MARCUS VINICIUS
 */
public interface Observable {
    void receive(String msg);
    void receive(String msg, Byte nodeId);
    Byte getNodeId();
    void stopNode();
    void manageCharacteristics();
    boolean isConnected();
    int getBatteryLevel();
    double getRank();
    int getMemoryAvaliable();
    int getAmountMemory();
    void sendCharactheristicsToController();
    double getW1();
    double getW2();
    double getW3();
    double getW4();
    double getW5();
    int getMobilityValue();
    float getSignalStrentch();
    int getMips();
    int getMipsValue();
    void setW1(double aW1);
    void setW2(double aW2);
    void setW3(double aW3);
    void setW4(double aW4);
    void setW5(double aW5);
    void setDiffRank(double diff);
    Number getCI(Byte ci,Byte nodeId);
}
