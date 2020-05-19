package control;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.lang.reflect.Array;
import java.util.HashMap;
import util.MemoryModel;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MARCUS VINICIUS
 */
public class Node extends Thread implements Observable{
    
    public Node(
            Byte idA,
            int memoryAvaliableA,
            int mipsA,
            int maxMemoryAvaliableA,
            int batteryLevelA,
            float signalStrentchA,
            byte typeOfDeviceA,
            double w1A, double w2A, double w3A, double w4A, double w5A,
            int TLEVEL1, int TLEVEL2, int TLEVEL3, int TLEVEL4, int TLEVEL5,
            int PLEVEL1, int PLEVEL2, int PLEVEL3, int PLEVEL4, int PLEVEL5,int PLEVEL6,int PLEVEL7,int PLEVEL8){
            this.ranks = new Hashtable<>();
            this.id=idA;
            this.mips=mipsA;
            this.memoryAvaliable=memoryAvaliableA;
            this.batteryLevel=batteryLevelA;
            tBatterySendTie=this.batteryLevel;
            this.maxMemoryAvaliable=maxMemoryAvaliableA;
            
            this.typeOfDevice=typeOfDeviceA;
            this.distance=headOrTails.nextInt(50)+1;
            this.oldDistance=distance;
            this.signalStrentch=calculateSNR(oldDistance);
            changeMobility();
            currentMobilityValue=getMobilityValue();
            this.W1= w1A;
            this.W2=w2A;
            this.W3=w3A;
            this.W4=w4A;
            this.W5=w5A;
            
            this.TLEVEL1=TLEVEL1;
            this.TLEVEL2=TLEVEL2;
            this.TLEVEL3=TLEVEL3;
            this.TLEVEL4=TLEVEL4;
            this.TLEVEL5=TLEVEL5;
            
            this.PLEVEL1=PLEVEL1;
            this.PLEVEL2=PLEVEL2;
            this.PLEVEL3=PLEVEL3;
            this.PLEVEL4=PLEVEL4;
            this.PLEVEL5=PLEVEL5;
            this.PLEVEL6=PLEVEL6;
            this.PLEVEL7=PLEVEL7;
            this.PLEVEL8=PLEVEL8;
            this.
            calculateRank();
            //Definindo probabilidade de variação de memória
            switch(headOrTails.nextInt(3)){
                case 1: this.probabilityOfMemoryConsumption="low";
                break;
                
                case 2: this.probabilityOfMemoryConsumption="medium";
                break;
                
                default: this.probabilityOfMemoryConsumption="high";
            }
            //Definindo probabilidade de variação de bateria
            switch(headOrTails.nextInt(3)){
                case 1: this.probabilityOfBatteryConsumption="low";
                break;
                
                case 2: this.probabilityOfBatteryConsumption="medium";
                break;
                
                default: this.probabilityOfBatteryConsumption="high";
            }
    }
    
   
    
    private final Byte id;
    private volatile double W1, W2, W3, W4, W5;
    private volatile int batteryLevel;
    private volatile boolean mobility=false;
    private volatile float oldDistance;
    private volatile int currentMobilityValue;
    private volatile int nNodesLowBattery=0; //nós com bateria abaixo da média
    private volatile int nNodesWeakSignal=0; //nós com sinal baixo
    private volatile int nTimesAsController=0; //número de vezes que o nó foi controlador
    private volatile double rankDiffAcc=0d; //acumula a diferença entre ranks de controladores
    private volatile int nRoundsLeftController=0; //número de rodadas que um nó deixou de ser controlador
    private volatile int nRoundsAsController=0; //número de rodadas que foi controlador da última vez
    private volatile int memoryAvaliable;
    private final int TLEVEL1, TLEVEL2, TLEVEL3, TLEVEL4, TLEVEL5; 
    private final int PLEVEL1, PLEVEL2, PLEVEL3, PLEVEL4, PLEVEL5,PLEVEL6,PLEVEL7,PLEVEL8; 
    private int maxMemoryAvaliable; //quantidade (em MB) total de memória do Nó
    private volatile float signalStrentch;
    private volatile int mips;
    //0-em constante movimento, 1- movimenta-se pouco, 2- estacionado
    private final byte typeOfDevice;
    private volatile double rank;
    private volatile boolean isHigherRank=true; //flag que indica se é o maior rank de todos
    private volatile boolean isController=false;
    //private volatile boolean chargingBaterry=false;    
    private String received;
    private volatile Hashtable<Byte, Double> ranks;//lista de ranks calculados pelo controlador
    private volatile Byte higherRankId=-1; //armazena a referência de um rank maior do que o atual do controlador
    private volatile List<Byte> higherRanksForTie=new Vector<Byte>(); //armazena a referência de um rank maior do que o atual do controlador para empates
    private volatile boolean calculateDBForControllerFlag=false; //flag que indica se foi contabilizado o cálculo do DB de caraterptyisica no controlador 
    private final Random headOrTails=new Random(); //gerador aleatório de verdadeiro ou falso
    private volatile boolean verifyNewControllerFlag=false;//flag que determina se a verificação por um noco controlador já teve início
    private volatile boolean  tieFlag=false; //flag que verifica o empate entre ranks
    private final int randomNumberTie=headOrTails.nextInt(1000); //número aleatório usado para desempates
    private volatile int tBatterySendTie=0; //utilizado para informado qual o status da bateria no momento do envio
    private volatile boolean terminate=false; //flag utilizada para finalizar a thread
    private static final HashMap<String, Integer> typesOfConsumption=new HashMap<String, Integer>(); //probabilidade de consumo de características de interesse
    private volatile String probabilityOfMemoryConsumption;
    private volatile String probabilityOfBatteryConsumption;
    private volatile Map<Byte, Number[]> charactheristicsOfInterest=new ConcurrentHashMap<Byte, Number[]>(); //características de interesse dos nós
    //private volatile boolean experimen6NodeFlag=false;
    private volatile float distance;
    static{
        typesOfConsumption.put("low", 40);
        typesOfConsumption.put("medium", 60);
        typesOfConsumption.put("high", 80);
        typesOfConsumption.put("total", 100);
    }
    
   
    /**
     * Libera as varivaéis inerentes ao controlador
     */
    public void releaseControllerRole(){
        calculateDBForControllerFlag=false;
        isController=false;
        tieFlag=false;
        ranks.clear();
        charactheristicsOfInterest.clear();
    }
    
    /**
     * Recebe uma mensagem e envia novamente unicast
     * @param msg Mensagem enviad
     * @param nodeIdSender Id dos nós
     */
    @Override
    public void receive(String msg, Byte nodeIdSender){
        if (!isConnected() && !"new_election".equals(msg)) return;
        if (isController && ("join".equals(msg) || msg.startsWith("rank"))) unicast(nodeIdSender, "controller "+getId());
    }
    
    /**
     * Recebe uma mensagem
     * @param messeger Mensagem enviad
     */
    @Override
    public void receive(String messeger) {
        if (!isConnected() && !"new_election".equals(messeger)) return;
        this.received=messeger;
        if(isController && received.startsWith("mips")){
            String[] msg=received.split("&");
            int tMips=-1, tMemoryAvaliable=-1,tBattery=-1;
            float tSignalStrentch=-1;
            int tMobility=-1;
            double tDecreaseFactor=0;
            Byte tId=-1;
            for (String msg1 : msg) {
                String[] aux = msg1.split("=");
                if (aux.length==2){
                    if (aux.length>0){
                        switch (aux[0]) {
                            case "mips":
                                tMips=Integer.parseInt(aux[1]);
                                break;
                            case "signal_strentch":
                                tSignalStrentch=Float.parseFloat(aux[1]);
                                break;
                            case "memory_avaliable":
                                tMemoryAvaliable=Integer.parseInt(aux[1]);
                                break;
                            case "mobility":
                                tMobility=Integer.parseInt(aux[1]);
                                break;
                            case "battery":
                                tBattery=Integer.parseInt(aux[1]);
                                if (tBattery<=Manager.getInstance().lowBatteryLevel) ++nNodesLowBattery;
                                break;
                            case "id":
                                tId=Byte.parseByte(aux[1]);
                                break;
                            case "decrease_factor":  
                                tDecreaseFactor=Double.parseDouble(aux[1]);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (msg.length>1) storageCharactheristicsOfInterest(tMips, tSignalStrentch, tBattery, tMemoryAvaliable, tMobility, tId, tDecreaseFactor);
            //inicia o tempo para verificar a possibilidade de troca do nó
            if (!verifyNewControllerFlag){
                Thread t=new TimeToVerifyNewControllerThread();
                t.start();
                verifyNewControllerFlag=true;
            }
        }else if("change_controller".equals(received)){ //recebe solcitação para mudar o controlador
            isController=true;
            nRoundsAsController=0;
            broadcast(this.getNodeId(),"new_controller");
        }else if(received.startsWith("new_controller")){
            if (isController) releaseControllerRole();
            isHigherRank=false;
            tieFlag=false;
        }else if (received.startsWith("rank=")){
            String[] msg= received.split("=");
            if (msg.length>0){
                float receivedRank=Float.parseFloat(msg[1]);
                if (Double.compare(receivedRank, this.rank)>0) isHigherRank=false;
                if(isHigherRank && Double.compare(receivedRank, this.rank)==0) tieFlag=true;
            }
        }else if ("quit".equals(received)){
            broadcast(getNodeId(),"leave");
            stopNode();
        }else if("stop".equals(messeger)){
            System.out.println("Stop request...leaving");
            broadcast(getNodeId(),"leave");
            stopNode();
        }else if("controller".equals(this.received)){ //já existe um controlador
            isHigherRank=false; 
        }else if(received.startsWith("battery") && isHigherRank && tieFlag){ //nós candidatos a controlador empatados
            String[] msg= received.split("&");
            if (msg.length==2){
                for (String msg1: msg){
                    String aux[]=msg1.split("=");
                    if ("battery".equals(aux[0])){
                        int battery=Integer.parseInt(aux[1]);
                        if (battery>tBatterySendTie) isHigherRank=false;
                        if(battery!=tBatterySendTie) break;
                   }else if("random".equals(aux[0]) && Integer.parseInt(aux[1])>randomNumberTie) isHigherRank=false; 
                   
                }
            }  
        }else if("new_election".equals(received)){
            if(isController) releaseControllerRole();
            isHigherRank=true;
            tieFlag=false;
            if (this.getBatteryLevel()>10){ //apenas nós com bateria superior a 10% podem ser candidatos a controlador
                calculateRank();
                Thread t=new DispatcherThread();
                t.start();
                Thread t2=new ControllerElectionThread();
                t2.start();
            }
        }
    }

    @Override
    public int getMemoryAvaliable() {
        return this.memoryAvaliable;
    }
    

    
    //Altera as capacidades computacionais de interesse
    @Override
    public void manageCharacteristics(){
            changeMemoryLevel();
            //****BATERIA****
            changeBatteryLevel();
            //****MOBILIDADE E SNR
            changeMobility();
            if (Manager.getInstance().getCurrentRound()%5==0) {
                oldDistance-=distance;
                if (oldDistance<0) oldDistance*=-1;
                currentMobilityValue=getMobilityValue();
            }
            changeSNR();
            //Nó tem baixa bateria e deve sair (controladores são removidos pela classe Manager)
            if (batteryLevel<=10 && !isController){
                Manager.getInstance().broadcast(getNodeId(), "leave");
                stopNode();
            }
    }

    @Override
    public int getMipsValue() {
        if (getMips()>this.PLEVEL2 && getMips()<=this.PLEVEL3) return 50; //nível 2
        else if (getMips()>this.PLEVEL3 && getMips()<=this.PLEVEL4) return 75; //nível 3
        else if (getMips()>this.PLEVEL4 && getMips()<=this.PLEVEL5) return 100; //nível 4
        else if(getMips()>this.PLEVEL5 && getMips()<=this.PLEVEL6) return 90; //nível 5
        else if(getMips()>this.PLEVEL6 && getMips()<=this.PLEVEL7) return 60; //nível 6
        else if(getMips()>this.PLEVEL7 && getMips()<=this.PLEVEL8) return 60; //nível 7
        else return 0; //nível 1 ou nível 8
    }

    /**
     * Diferença de rank entre o atual controlador e o novo
     * @param diff 
     */
    @Override
    public void setDiffRank(double diff) {
        rankDiffAcc+=diff;
        ++nTimesAsController;
    }
    /**
     * Retorna uma Característica de interesse do controlador
     * @param ci 1-Bateria;2-Memória;3-Mobilidade;4-Força do sinal 
     * @param nodeId id de um nó
     * @return ci
     */
    @Override
    public Number getCI(Byte ci, Byte nodeId) {
        /*Number[] charactheristics={
        0 mipsFactor,
        1 signalStrentchNormalized,
        2 aBattery,
        3 memoryFactor, 
        4 aMobility, 
        5 aDecreaseFactor, 
        6 aSignalStrentch, 
        7 aMemoryAvaliable, 
        */
        if (isController){
            //Number[] charactheristics={mipsFactor,signalStrentchNormalized,aBattery, memoryFactor, aMobility, aDecreaseFactor, aSignalStrentch, aMemoryAvaliable};
            //Bateria
            if (Byte.compare(ci,(byte) 1)==0) return (Number) Array.get(this.charactheristicsOfInterest.get(nodeId), 2) ;
            //memória
            else if (Byte.compare(ci,(byte) 2)==0) return (Number) Array.get(this.charactheristicsOfInterest.get(nodeId), 7) ;
            //mobilidade
            else if (Byte.compare(ci,(byte) 3)==0) return (Number) Array.get(this.charactheristicsOfInterest.get(nodeId), 4) ;
            //força do sinal
            else if (Byte.compare(ci,(byte) 4)==0) return (Number) Array.get(this.charactheristicsOfInterest.get(nodeId), 6) ;
            
        }
        return null;
    }

        
    //Envia o rank em broadcast
    public class DispatcherThread extends Thread{
        @Override
        public void run() {
            broadcast(getNodeId(),"rank="+rank);
        }
    }
    
    
    /**
    * Computa o valor final de memória a ser contabilizado
    * @param n uma porcentagem de memória
    * @param m uma porcentagem de memória
    * @param sum false: subtrair/ true: somar
    * @return Retorna o valor final de memória a ser contabilizado
    */
    private int getMemoryResult(int n, int m, boolean sum){
        if (sum){
            if (n+m>100) return n;
            return n+m;
        }
        else{
            int maior=(n>m)?n:m;
            int menor=(maior==n)?m:n;
            if (maior-menor<0) return n;
            return maior-menor;
        }
    }
    /**
     * Conta um tempo para verificar se é controlador
     */
    public class ControllerElectionThread extends Thread{
        private final int time=500;
        @Override
        public void run(){
            try {
                sleep(time);
                if (isHigherRank){
                    if (!tieFlag){
                        isController=true;
                        broadcast(getNodeId(), "new_controller");
                    }else{ //se houver um empate, o nó empatado já envia bateria e um número aleatório
                        System.out.println("A tie occours. Sending battery="+tBatterySendTie+" and random="+randomNumberTie);
                        broadcast(getNodeId(), "battery="+batteryLevel+"&random="+randomNumberTie);
                        Thread t=new BreakTieControllerElectionThread();
                        t.start();
                    }
                }
                //System.out.println(getNodeId()+" tem isHigherRank="+isHigherRank);
            } catch (InterruptedException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Thread que contabiliza tempo para desempates na eleição de controladores
     */
    private class BreakTieControllerElectionThread extends Thread{
        private final int time=100;
        @Override
        public void run(){
            try {
                sleep(time);
                tieFlag=false;
                if (isHigherRank){
                    isController=true;
                    broadcast(getNodeId(), "new_controller");
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Thread que verifica a necessidade de trocar um controlador após um tempo pré-determinado
     */
    public class TimeToVerifyNewControllerThread extends Thread{

        @Override
        public void run() {
            try {
                Thread.sleep(400);
                verifyNewController();
                higherRankId=-1;
                nNodesLowBattery=0;
                nNodesWeakSignal=0;
                verifyNewControllerFlag=false;
                higherRanksForTie.clear();
                
                //Contabiliza o espaço necessário para armazenar o DB de caracterísitcas
                if (!calculateDBForControllerFlag){
                    //21=1+7+7+3+3 bits
                    int DBSize=Math.round((float)(((float)((float)(ranks.size()*21)/8)/Math.pow(1024, 2))*100)/maxMemoryAvaliable);
                    //acrescenta a cota do banco de dados interno do controlador
                    if (DBSize+memoryAvaliable<=100) memoryAvaliable+=DBSize;
                    calculateDBForControllerFlag=true;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
  
    
    @Override
    public void sendCharactheristicsToController(){
        if (getBatteryLevel()>10 && !isController){ //apenas nós com mais de 10% de sua capacidade podem ser candidatos
            double decreasePunishment=0d;
            if (this.nRoundsAsController!=0){
                
                ++this.nRoundsLeftController;
                double mean=((double) this.rankDiffAcc/this.nTimesAsController);
                decreasePunishment=(((double) 1/mean)*100)-
                    ((double)(this.nRoundsAsController+this.nRoundsLeftController));
                if (Double.compare(decreasePunishment, 0)<=0) decreasePunishment=0;
            }    
            //if (this.nRoundsAsController!=0) System.out.println("ID: "+getId()+"/ D: "+ decreasePunishment);
            unicast("mips="+getMips()+
                    "&signal_strentch="+getSignalStrentch()+
                    "&memory_avaliable="+Math.round(((float) memoryAvaliable/100)*maxMemoryAvaliable)+
                    "&battery="+batteryLevel+
                    "&mobility="+currentMobilityValue+
                    "&id="+getNodeId()+
                    "&decrease_factor="+decreasePunishment);
        }
    }
   
   
    /**
     * Calcula um rank de um nó não controlador
     */
    private void  calculateRank(){
        
        int memoryFactor=20;
        int mipsFactor=0;
        //Fator da memória
        int tMemoryAvaliable=Math.round(((float)memoryAvaliable/100) * maxMemoryAvaliable);
        if (tMemoryAvaliable>this.TLEVEL2 && tMemoryAvaliable<=this.TLEVEL3) memoryFactor=40; //nível 2
        else if (tMemoryAvaliable>this.TLEVEL3 && tMemoryAvaliable<=this.TLEVEL4) memoryFactor=60; //nível 3
        else if (tMemoryAvaliable>this.TLEVEL4 && tMemoryAvaliable<=this.TLEVEL5) memoryFactor=80; //nível 4
        else if (tMemoryAvaliable>this.TLEVEL5) memoryFactor=100; //nível 5
        
         //Fator de MIPS
        if (getMips()>this.PLEVEL2 && getMips()<=this.PLEVEL3) mipsFactor=50; //nível 2
        else if (getMips()>this.PLEVEL3 && getMips()<=this.PLEVEL4) mipsFactor=75; //nível 3
        else if (getMips()>this.PLEVEL4 && getMips()<=this.PLEVEL5) mipsFactor=100; //nível 4
        else if(getMips()>this.PLEVEL5 && getMips()<=this.PLEVEL6) mipsFactor=90; //nível 5
        else if(getMips()>this.PLEVEL6 && getMips()<=this.PLEVEL7) mipsFactor=60; //nível 6
        else if(getMips()>this.PLEVEL7 && getMips()<=this.PLEVEL8) mipsFactor=60; //nível 7
        else mipsFactor=0; //nível 1 ou nível 8
        
        //Normalização de SNR
        float signalStrentchNormalized= (100/20)* getSignalStrentch();
        if (signalStrentchNormalized>100) signalStrentchNormalized=100;
        rank=((this.getW1()* getMobilityValue())+
                (this.getW2() * batteryLevel)+
                (this.getW3() * memoryFactor)+
                (this.getW4() * signalStrentchNormalized)+
                (this.getW5() * mipsFactor));
    }

     /**
     * Armazena o de cada nó para posterior cáculo do rank
     * @param aMips MIPS de um nó
     * @param aSignalStrentch Força do Sinal, em SNR
     * @param aBattery nível da bateria
     * @param aMemoryAvaliable quantidade de memória disponível
     * @param aMobility mobilidade
     * @param aId id do Nó
     * @param aDecreaseFactor fator de penalidade de um nó
     */
    private void storageCharactheristicsOfInterest(
            int aMips, 
            float aSignalStrentch,
            int aBattery,
            int aMemoryAvaliable,
            int aMobility,
            Byte aId,
            double aDecreaseFactor){
        if (aBattery<=10 || aId==getNodeId()) return;

        int memoryFactor=20;
        int mipsFactor=0;
        //Fator da memória
        if (aMemoryAvaliable>TLEVEL2 && aMemoryAvaliable<=this.TLEVEL3) memoryFactor=40; //nível 2
        else if (aMemoryAvaliable>this.TLEVEL3 && aMemoryAvaliable<=this.TLEVEL4) memoryFactor=60; //nível 3
        else if (aMemoryAvaliable>this.TLEVEL4 && aMemoryAvaliable<=this.TLEVEL5) memoryFactor=80; //nível 4
        else if (aMemoryAvaliable>this.TLEVEL5) memoryFactor=100; //nível 5
         //Fator de MIPS
        if (aMips>this.PLEVEL2 && aMips<=this.PLEVEL3) mipsFactor=50; //nível 2
        else if (aMips>this.PLEVEL3 && aMips<=this.PLEVEL4) mipsFactor=75; //nível 3
        else if (aMips>this.PLEVEL4 && aMips<=this.PLEVEL5) mipsFactor=100; //nível 4
        else if (aMips>this.PLEVEL5 && aMips<=this.PLEVEL6) mipsFactor=90; //nível 5
        else if (aMips>this.PLEVEL6 && aMips<=this.PLEVEL7) mipsFactor=60; //nível 6
        else if (aMips>this.PLEVEL7 && aMips<=this.PLEVEL8) mipsFactor=20; //nível 7
        else mipsFactor=0; //nível 8 ou nível 1
        float signalStrentchNormalized= (100/20)* aSignalStrentch;
        if (signalStrentchNormalized>100) signalStrentchNormalized=100;
        if(Float.compare(signalStrentchNormalized, Manager.getInstance().lowSignalStrentch)<=0) ++nNodesWeakSignal;
        Number[] charactheristics={mipsFactor,signalStrentchNormalized,aBattery, memoryFactor, aMobility, aDecreaseFactor, aSignalStrentch, aMemoryAvaliable};
        this.charactheristicsOfInterest.put(aId, charactheristics);
    }
    
    /**
     * Calcula o rank dos nós
     */
    public void  calculateNodesRank(){
        //mobilidade
        this.setW1(0.10d+
                (((double)nNodesWeakSignal/100) * 0.05d));  
        //bateria
        this.setW2(0.35d+
                (((double)nNodesLowBattery/100) * 0.05d));
        //memória disponível
        this.setW3(0.15d+(0.2d-this.W1));
        //força do sinal
        this.setW4(0.1d+(0.40d-this.getW2()));
        //procesamento
        this.setW5(1d-(this.W1+this.W2+this.W3+this.W4));
        calculateRank(); //calcula o próprio rank
        double tmpHigherRank=this.rank;
        for (Byte nodeId:this.charactheristicsOfInterest.keySet()){
            Number[] ci=this.charactheristicsOfInterest.get(nodeId);
            double nodeRank=((this.getW1()*ci[4].intValue())
                    +(this.getW2()*ci[2].intValue())+
                    (this.getW3()*ci[3].intValue())+
                    (this.getW4()*ci[1].floatValue())+
                    (this.getW5()*ci[0].intValue()))-ci[5].doubleValue();
            this.ranks.put(nodeId, nodeRank);
            if (Double.compare(tmpHigherRank,nodeRank)<0){
                tmpHigherRank=nodeRank;
                this.higherRankId=nodeId;
                this.higherRanksForTie.clear();
            }else if(this.higherRankId!=-1 &&
                    Double.compare(tmpHigherRank,nodeRank)==0){
                //este nó tem mesmo rank do maior, mas com maior bateria
                if (Manager.getInstance().getBatteryLevel(this.higherRankId)<
                    Manager.getInstance().getBatteryLevel(nodeId))
                    this.higherRankId=nodeId;
                //Existe um empate em rank e bateria
                else if(Manager.getInstance().getBatteryLevel(this.higherRankId)==
                    Manager.getInstance().getBatteryLevel(nodeId))
                this.higherRanksForTie.add(nodeId);
            }
        }
    }
    
    /**
     * Verifica a necessidade de trocar o controlador
     */
    public void verifyNewController(){  
        if (isController){
            ++this.nRoundsAsController;
            //nível de bateria do controlador está muito baixa e um novo deve assumir
            calculateNodesRank();
            if(getBatteryLevel()<=10){ 
                this.nRoundsLeftController=0;
                System.out.println("Bateria de "+getId()+" baixa");
                if (higherRankId==-1){
                    unicast(max(this.ranks),"change_controller"); 
                    System.out.println("Contactando nó "+max(this.ranks)+" e "+this.charactheristicsOfInterest.size());
                }else{
                    unicast(higherRankId, "change_controller");
                    System.out.println("contactando nó "+higherRankId);
                }
                return;
            }
            if (higherRankId!=-1 && 
                    !this.higherRanksForTie.isEmpty()){ //Existem nós empatados com maior rank
                System.out.println("Tie break for new controller");
                Byte newControllerId=higherRankId;
                for (Byte entry: this.higherRanksForTie){ //Sorteia quem deve assumir como novo controlador (empatados em rank e bateria)
                    newControllerId=headOrTails.nextBoolean()? newControllerId:entry;
                }
                higherRankId=newControllerId;
            }
            //Se existe um rank maior do que o atual controlador e a diferença é maior do que 1.0
            if (higherRankId!=-1 
                    &&
                    ((this.nRoundsAsController>=2 && Double.compare(this.ranks.get(higherRankId)-this.rank, 1d)>0))){ 
                //System.out.println("Old:"+this.rank+" new "+this.ranks.get(higherRankId));
                this.nRoundsLeftController=0;
                setDiffRank((this.ranks.get(higherRankId)-this.rank));
                unicast(higherRankId,"change_controller");
            }
        }
    }
    
    /**
     * Obtém o índice com maior valor de rank
     * @param values tabela de ranks
     * @return índice com o maior rank
     */
    private Byte max(Hashtable<Byte, Double> values){
        double maior=-1d;
        Byte index=0;
        for (Byte v: values.keySet()){
            if (Double.compare(maior, this.ranks.get(v))<0) {
                maior=this.ranks.get(v);
                index=v;
            }
        }
        return index;
    }
    
    public double[] toArray(Hashtable<Byte, Float> values){
        double[] array=new double[Manager.getInstance().getNodesNumber()]; 
        if (isController){
            for (Byte v: values.keySet()){
                 array[v]=values.get(v);
            }
            array[getNodeId()]=this.rank; //controlador
        }
        return array;
    }
    /**
     * Dispara uma mensagem de broadcast
     * @param nodeId Id do nó emissor da mensagem
     * @param broadcastMessage mensagem a ser enviada
    */
    public void broadcast(Byte nodeId, String broadcastMessage)  {
        if (!isConnected()) return;
        try {
            sleep(headOrTails.nextInt(5)+1);
            Manager.getInstance().broadcast(nodeId,broadcastMessage);
        } catch (InterruptedException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
    
    /**
     * Envia uma mensagem ao controlador
     * @param unicastMessage mensagem a ser enviada
     */
    public void unicast(String unicastMessage) {
        if (!isConnected()) return;
        try {
            sleep(headOrTails.nextInt(5)+1);
            Manager.getInstance().unicast(unicastMessage);
        } catch (InterruptedException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Envia uma mensagem específica a um nó
     * @param nodeId referência a um objeto de Node
     * @param unicastMessage mensagem a ser enviada
    */
    public void unicast(Byte nodeId, String unicastMessage) {
        if (!isConnected()) return;
        try {
            sleep(headOrTails.nextInt(5)+1);
            Manager.getInstance().unicast(nodeId,unicastMessage);
        } catch (InterruptedException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    
    /**
     * Informa o id de um nó
     * @return valor numérico de um nó
     */
    @Override
    public Byte getNodeId(){
        return this.id;
    }

    @Override
    public void run(){
        try {
            //Thread mc=new ManageCharacteristicsThread();
            //mc.start();
            broadcast(getNodeId(), "join");
            Thread.sleep(100);
            Thread dt=new DispatcherThread();
            dt.start();
            Thread electionThread=new ControllerElectionThread();
            electionThread.start(); //inicia a thread que avalia o controlador
            //while(!terminate){}
        } catch (InterruptedException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Calcula a variação de memória disponível
     */
    private void changeMemoryLevel(){
        if (headOrTails.nextBoolean()){ //variar ou não a memória
            //Se estiver a menos de 81% de memória disponível, tem "probabilityOfMemoryConsumption"% chance de subir
            if (memoryAvaliable<=80){
                if (headOrTails.nextInt(100)<typesOfConsumption.get(probabilityOfMemoryConsumption))
                    memoryAvaliable=getMemoryResult(memoryAvaliable, Math.round(MemoryModel.getInstance().getMemoryVariate()), true);
                else //caso contrário, diminiu ainda mais sua memória disponível
                    memoryAvaliable=getMemoryResult(memoryAvaliable, Math.round(MemoryModel.getInstance().getMemoryVariate()), false);
            }else{ //Se estiver a mais de 80% de memória disponível, tem 80% chance de baixar
                if (headOrTails.nextInt(100)<typesOfConsumption.get(probabilityOfMemoryConsumption))
                    memoryAvaliable=getMemoryResult(memoryAvaliable, Math.round(MemoryModel.getInstance().getMemoryVariate()), false);
                else //caso contrário, aumenta sua memória
                    memoryAvaliable=getMemoryResult(memoryAvaliable, Math.round(MemoryModel.getInstance().getMemoryVariate()), true);
            }
        }
    }
    
    /**
     * Calcula a variação de memória disponível
     */
    private void changeBatteryLevel(){
        if (typeOfDevice<2){
            if (!isController && headOrTails.nextInt(10)==1 && headOrTails.nextInt(100)<typesOfConsumption.get(probabilityOfBatteryConsumption)) batteryLevel--; //nós não controladores têm 10% de chance de diminuir sua bateria
            else if(isController && headOrTails.nextInt(5)==3 && headOrTails.nextInt(100)<typesOfConsumption.get(probabilityOfBatteryConsumption)) batteryLevel--;  //nós controladores têm 20% de chance de diminuir sua bateria
        }
    }
    
    /**
     * Determina se um nó pode estar em movimento ou não
     */
    private void changeMobility(){
        //50% de chance de estar móvel
        if (typeOfDevice==0) mobility=headOrTails.nextBoolean();
        //10% de chance de estar móvel
        else if(typeOfDevice==1) mobility=((headOrTails.nextInt(10))==5) ? true : false;
        //5% de chance de estar móvel
        else mobility=((headOrTails.nextInt(20))==5) ? true : false;
    }
    
    /**
    * Modifica SNR de um nó
     */
    private void changeSNR(){
        //se movimentou e se afastou/aproximou do ponto de acesso
        if (mobility){
            //se afastou do AP
            if (headOrTails.nextBoolean())
                distance+=headOrTails.nextFloat()*10;
            else
                distance-=headOrTails.nextFloat()*10;
        }
        
        if (Float.compare(distance, 0f)<0) distance=0.5f;    
       calculateSNR();
    }
    
    
    /**
     * @return status de conexão
     */
    @Override
    public boolean isConnected() {
        return (signalStrentch>=1);
    }
    
    @Override
    public void stopNode(){
        this.terminate=true;
    }
    
    /**
     * 
     * @return força do sinal
     */
    @Override
    public float getSignalStrentch(){
        return (signalStrentch>0)? signalStrentch: 0f;
    }
    
    /**
     * Obtém o nível de bateria
     * @return inteiro
     */
    @Override
    public int getBatteryLevel(){
        return this.batteryLevel;
    }
    
    /**
     * Obtém o valor de mobilidade utilizando na função do Rank
     * @return inteiro que representa o valor de mobilidade
     */
    @Override
    public int getMobilityValue(){
        return (min(100,100-(int) oldDistance));
    }
    
    @Override
    public double getRank(){
        return this.rank;
    }    
    
    @Override
    public int getAmountMemory(){
        return Math.round(((float) this.memoryAvaliable/100) * this.maxMemoryAvaliable);
    }

    /**
     * @return the W1
     */
    @Override
    public double getW1() {
        return W1;
    }

    /**
     * @return the W2
     */
    public double getW2() {
        return W2;
    }

    /**
     * @return the W3
     */
    @Override
    public double getW3() {
        return W3;
    }

    /**
     * @return the W4
     */
    @Override
    public double getW4() {
        return W4;
    }

    /**
     * @return the W5
     */
    @Override
    public double getW5() {
        return W5;
    }

    /**
     * @param W1 the W1 to set
     */
    @Override
    public void setW1(double W1) {
        this.W1 = W1;
    }

    /**
     * @param W2 the W2 to set
     */
    @Override
    public void setW2(double W2) {
        this.W2 = W2;
    }

    /**
     * @param W3 the W3 to set
     */
    @Override
    public void setW3(double W3) {
        this.W3 = W3;
    }

    /**
     * @param W4 the W4 to set
     */
    @Override
    public void setW4(double W4) {
        this.W4 = W4;
    }

    /**
     * @param W5 the W5 to set
     */
    @Override
    public void setW5(double W5) {
        this.W5 = W5;
    }

    /**
     * @return the mips
     */
    @Override
    public int getMips() {
        return mips;
    }
    /**   
	 * Calcula a variação de SNR de acordo com deslocamento
	     * Referência: https://doi.org/10.1109/JPROC.2003.821910
    */
    private float calculateSNR(float distance){
        double l=1;
        double gama=2;
        float snr=(float) (-41-(-108)-l-20*(Math.log10(4*Math.PI*2400000000d)
        - Math.log10(300000000d))-(gama*10*Math.log10(distance)));
        return snr;
    }
    
    public int min(int a, int b){
        if (a<b) return a;
        else return b;
    }
    
}
