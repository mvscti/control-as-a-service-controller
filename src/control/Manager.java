/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.TimeCounter;
/**
 *
 * @author MARCUS VINICIUS
 */
public final class Manager {
    private static Manager managerInstance=null;
    private static volatile List<Observable>  nodes=new CopyOnWriteArrayList<Observable>();
    private static Observable controller=null;
    public static final int BATTERY_DISCHARGE=1;
    public static final int NO_CONNECTION=2;
    private static volatile int requisitions=-1;
    private static int n=-1, type1=-1, type2=-2, type3=-1;
    private static int mips_min=-1, mips_max=-1, max_memory=-1;
    private static double w1=-1,w2=-1, w3=-1, w4=-1, w5=-1;
    private static int plevel1=-1, plevel2=-1, plevel3=-1, plevel4=-1;
    private static int plevel5=-1, plevel6=-1,plevel7=-1, plevel8=-1;
    private static int tlevel1=-1, tlevel2=-1, tlevel3=-1, tlevel4=-1, tlevel5=-1;
    public static int lowBatteryLevel=-1, lowSignalStrentch=-1;
    public static volatile int scenario=-1;
    private static volatile boolean controllerFailureFlag=false; //flag que indica falha do controlador    
    private static volatile LocalDateTime t1;
    private static volatile  String failureString; //armazena uma string com estatísticas de erro de um controlador
    private static volatile FileWriter experiment1, experiment2, experiment3, experiment4, experiment5Memory, experiment5Battery, experiment5Mobility, experiment5SignalStrentch, experiment;
    private static volatile boolean startRequestsFlag=false; //flag que indica se a Thread do nó requisitante deve começar
    //private static volatile boolean sendCIToControllerMCCFlag=false; //flag que indica se um nó observado já enviou as características uma vez
    private static volatile int counter=1; //contador de número de requisições
    private static volatile boolean simulationsDoneFlag=false; //flag que indica se as simulações foram completas
    private static volatile List<Observable> observablesNodes=new ArrayList<Observable>(); //lista de nós a se observar
    //private static volatile int experiment6RandomValue; //armazena a leitura aleatória em uma rodada para o experimento 6
    //private static volatile Observable experiment6Node=null; //nó a ser observado no experimento 6
    private static volatile Random randomValue=new Random();
    private Manager(){
        Date date=new Date();
        long docId=date.getTime();
        //experiment6RandomValue=randomValue.nextInt(10)+1;//sorteia a primeira rodada para ler uma CI
        try {
            experiment1=new FileWriter("src/results/Experiment_1/scenario_"+scenario+"/experiment1_"+docId+".csv",true );
            experiment2=new FileWriter("src/results/Experiment_2/scenario_"+scenario+"/experiment2_"+docId+".csv",true);
            experiment3=new FileWriter("src/results/Experiment_3/scenario_"+scenario+"/experiment3_"+docId+".csv",true);
            experiment4=new FileWriter("src/results/Experiment_4/scenario_"+scenario+"/experiment4_"+docId+".csv",true);
            experiment5Battery=new FileWriter("src/results/Experiment_5/battery/battery_"+docId+".csv",true);
            experiment5Memory=new FileWriter("src/results/Experiment_5/memory/memory_"+docId+".csv",true);
            experiment5Mobility=new FileWriter("src/results/Experiment_5/mobility/mobility_"+docId+".csv",true);
            experiment5SignalStrentch=new FileWriter("src/results/Experiment_5/signal_strentch/signal_"+docId+".csv",true);
            /*experiment6Battery=new FileWriter("src/results/Experiment_6/battery/battery_"+docId+".csv",true);
            experiment6Memory=new FileWriter("src/results/Experiment_6/memory/memory_"+docId+".csv",true);
            experiment6Mobility=new FileWriter("src/results/Experiment_6/mobility/mobility_"+docId+".csv",true);
            experiment6SignalStrentch=new FileWriter("src/results/Experiment_6/signal_strentch/signal_"+docId+".csv",true);*/
            experiment1.write("controller;round;mobility;battery;memory_avaliable;signal_strentch;processing\n");
            experiment2.write("controller;cause;time_failure\n");
            experiment3.write("controller;battery;memory_avaliable;total_memory_avaliable\n");
            experiment4.write("round;w1;w2;w3;w4;w5\n");
            experiment5Battery.write("node 1;node 2;node 3;node 4;node 5\n");
            experiment5Memory.write("node 1;node 2;node 3;node 4;node 5\n");
            experiment5SignalStrentch.write("node 1;node 2;node 3;node 4;node 5\n");
            experiment5Mobility.write("node 1;node 2;node 3;node 4;node 5\n");
            /*experiment6Battery.write("value;proposed_method;mcc\n");
            experiment6Memory.write("value;proposed_method;mcc\n");
            experiment6SignalStrentch.write("value;proposed_method;mcc\n");
            experiment6Mobility.write("value;proposed_method;mcc\n");*/
        } catch (IOException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread t=new RequesterNodeThread();
        t.start();
    }
    
    /**
     * Reecebe os parâmetros para inicar as threads
     * @param v Mapa de valores de acordo com o arquivo simulations.conf
     */
    public void setParameters(HashMap<String, Number> v){
       deserializeParameters(v);
    }
    
    /**
     * Inicia as threads, configurando o limite das caracterísiticas de cada nó
     * @param aNumberNodes número de nós para simulação
     * @param aNumberRequests limite de requisições para a simulação
     * @param nTypeOneNodes inteiro que representa a porcentagem pelo número de nós do tipo 1
     * @param nTypeTwoNodes inteiro que representa a porcentagem pelo número de nós do tipo 2
     * @param nTypeThreeNodes inteiro que representa a porcentagem pelo número de nós do tipo 3
     */
    
    /**
     * Desserializa o Mapa de valores de incialização dos nós
     */
    private void deserializeParameters(HashMap<String, Number> parameters){
        for (Iterator<Map.Entry<String, Number>> it = parameters.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Number> entry = it.next();
            switch(entry.getKey()){
                case "n":
                    n=entry.getValue().intValue();
                    break;
                case "requisitions":
                    requisitions=entry.getValue().intValue();
                    break;
                case "type1":
                    type1=entry.getValue().intValue();
                    break;
                case "type2":
                    type2=entry.getValue().intValue();
                    break;
                case "type3":
                    type3=entry.getValue().intValue();
                    break;
                case "mips_min":
                    mips_min=entry.getValue().intValue();
                    break;
                case "mips_max":
                    mips_max=entry.getValue().intValue();
                    break;    
                case "max_memory":
                    max_memory=entry.getValue().intValue();
                    break; 
                case "w1":
                    w1=entry.getValue().doubleValue();
                    break;
                case "w2":
                    w2=entry.getValue().doubleValue();
                    break;
                case "w3":
                    w3=entry.getValue().doubleValue();
                    break;
                case "w4":
                    w4=entry.getValue().doubleValue();
                    break;
                case "w5":
                    w5=entry.getValue().doubleValue();
                    break;

                case "plevel1":
                    plevel1=entry.getValue().intValue();
                    break;
                case "plevel2":
                    plevel2=entry.getValue().intValue();
                    break;
                case "plevel3":
                    plevel3=entry.getValue().intValue();
                    break;
                case "plevel4":
                    plevel4=entry.getValue().intValue();
                    break;
                case "plevel5":
                    plevel5=entry.getValue().intValue();
                    break;
                case "plevel6":
                    plevel6=entry.getValue().intValue();
                    break;
                case "plevel7":
                    plevel7=entry.getValue().intValue();
                    break;
                case "plevel8":
                    plevel8=entry.getValue().intValue();
                    break;
                case "tlevel1":
                    tlevel1=entry.getValue().intValue();
                    break;
                case "tlevel2":
                    tlevel2=entry.getValue().intValue();
                    break;
                case "tlevel3":
                    tlevel3=entry.getValue().intValue();
                    break;
                case "tlevel4":
                    tlevel4=entry.getValue().intValue();                    
                    break;
                case "tlevel5":
                    tlevel5=entry.getValue().intValue();
                    break;
                case "low_battery_level":
                    lowBatteryLevel=entry.getValue().intValue();
                    break;
                case "low_signal_strength":
                    lowSignalStrentch=entry.getValue().intValue();
                    break;
                case "scenario":
                    scenario=entry.getValue().intValue();
                    break;
            }
        }
        
        
    }
    /**
     * Inicia a simulação
     */
    public void start(){
        if (n==-1 || requisitions==-1  || type1==-1  || type2==-1
                 || type3==-1  || mips_min==-1  || mips_max==-1
                 || max_memory==-1  || w1==-1.0  || w2==-1.0  || w3==-1.0
                 || w4==-1.0  || w5==-1.0  || plevel1==-1  || plevel2==-1
                 || plevel3==-1  || plevel4==-1  || plevel5==-1  || plevel6==-1
                 || plevel7==-1  || plevel8==-1  || tlevel1==-1  || tlevel2==-1
                 || tlevel3==-1  || tlevel4==-1  || tlevel5==-1 || lowBatteryLevel==-1 ||
                lowSignalStrentch==-1 || scenario==-1){
                 System.out.println("Could not initialize simulations: too few parameters");
                 System.exit(1);
        }
        Byte i=0;
        Random random=new Random();
        int tMips=0, tBatteryLevel=0, tSignalStreentch=0, tMaxMemory=max_memory+1;
        byte type=0;
        //Nós do tipo 1
        for (int j=0;j<(int)
                (((float) type1/100)*n);j++){
                while(tMips<mips_min){
                    tMips=random.nextInt(mips_max)+1;
                }
                tBatteryLevel=random.nextInt(60)+41;
                while(tSignalStreentch==0){
                    tSignalStreentch=random.nextInt(20)+1;
                }
                tMaxMemory=max_memory+1;
                while(tMaxMemory>max_memory){
                    tMaxMemory=(int) Math.pow(2, random.nextInt(6)+7);
                }    
                nodes.add(i,new Node(i,
                random.nextInt(70)+31, //memória disponível (míninimo de 31%)
                random.nextInt(tMips), //mips
                tMaxMemory, //máximo de memória do dispositivo
                tBatteryLevel,
                tSignalStreentch,
                type, //tipo do Dispositivo
                w1,w2,w3,w4,w5,
                tlevel1,tlevel2,tlevel3,tlevel4,tlevel5,
                plevel1,plevel2,plevel3,plevel4,plevel5,plevel6,plevel7,plevel8        
                ));
                ++i;
        }
       
        //Nós do tipo 2
        tMips=0; tBatteryLevel=0; tSignalStreentch=0;
        type=1;
        for (int j=0;j<(int)
                (((float) type2/100)*n);j++){
                while(tMips<mips_min){
                    tMips=random.nextInt(mips_max)+1;
                }
                tBatteryLevel=random.nextInt(60)+41;
                while(tSignalStreentch==0){
                    tSignalStreentch=random.nextInt(20)+1;
                }
                tMaxMemory=max_memory+1;
                while(tMaxMemory>max_memory){
                    tMaxMemory=(int) Math.pow(2, random.nextInt(6)+7);
                }
                nodes.add(i,new Node(i,
                random.nextInt(70)+31, //memória disponível (míninimo de 31%)
                random.nextInt(tMips), //mips
                tMaxMemory,
                tBatteryLevel,
                tSignalStreentch,
                type, //tipo do Dispositivo
                w1,w2,w3,w4,w5,
                tlevel1,tlevel2,tlevel3,tlevel4,tlevel5,
                plevel1,plevel2,plevel3,plevel4,plevel5,plevel6,plevel7,plevel8        
                ));
                ++i;
        }
        /*
        //Nó que não pode ser controlador (experimento 6). Comentar se não deseja realizar este experimento
        while(tMips<mips_min){
            tMips=random.nextInt(mips_max)+1;
        }
        tBatteryLevel=random.nextInt(60)+41;
        while(tSignalStreentch==0){
            tSignalStreentch=random.nextInt(20)+1;
        }
        tMaxMemory=max_memory+1;
        while(tMaxMemory>max_memory){
            tMaxMemory=(int) Math.pow(2, random.nextInt(6)+7);
        }
        experiment6Node=new Node(i,
        random.nextInt(70)+31, //memória disponível (míninimo de 31%)
        random.nextInt(tMips), //mips
        tMaxMemory,
        tBatteryLevel,
        18,
        (byte) 0, //tipo do Dispositivo
        w1,w2,w3,w4,w5,
        tlevel1,tlevel2,tlevel3,tlevel4,tlevel5,
        plevel1,plevel2,plevel3,plevel4,plevel5,plevel6,plevel7,plevel8,
        true
        );
        nodes.add(i, experiment6Node);
        ++i;
        */
        //Nós do tipo 3
        tMips=0; tBatteryLevel=0; tSignalStreentch=0;
        type=2;
        for (int j=0;j<(int)
                (((float)type3/100)*n);j++){
                while(tMips<mips_min){
                    tMips=random.nextInt(mips_max)+1;
                }
                tBatteryLevel=random.nextInt(60)+41;
                tMaxMemory=max_memory+1;
                while(tMaxMemory>max_memory){
                    tMaxMemory=(int) Math.pow(2, random.nextInt(6)+7);
                }
                while(tSignalStreentch==0){
                    tSignalStreentch=random.nextInt(20)+1;
                }
                nodes.add(i,new Node(i,
                random.nextInt(70)+31, //memória disponível (míninimo de 31%)
                random.nextInt(tMips), //mips
                tMaxMemory, 
                tBatteryLevel,
                tSignalStreentch,
                type, //tipo do Dispositivo
                w1,w2,w3,w4,w5,
                tlevel1,tlevel2,tlevel3,tlevel4,tlevel5,
                plevel1,plevel2,plevel3,plevel4,plevel5,plevel6,plevel7,plevel8        
                ));
                ++i;
        }
        if (type1>=5){
            observablesNodes.add(getNode((byte) 0));
            observablesNodes.add(getNode((byte) 1));
            observablesNodes.add(getNode((byte) 2));
            observablesNodes.add(getNode((byte) 3));
            observablesNodes.add(getNode((byte) 4));
        }
        //Iniciando as threads
        for (Observable o: nodes){
            Node n= (Node) o;
            Thread t=n;
            t.start();
        }
        //Incia a thread que ordena que os nós alterem sua características
        Thread t=new ManageNodesCharacteristicsThread();
        t.start();
    }
    
    public static Manager getInstance(){
        if (managerInstance==null) managerInstance=new Manager();
        return managerInstance;
    }
    /**
     * Envia uma mensagem para todos os nós
     * @param nodeId Id do nó que enviou a mensagem
     * @param msg Mensagem a ser enviada
     */
    synchronized public void broadcast(Byte nodeId,String msg){
        if (("quit".equals(msg) || ("leave").equals(msg)) && nodes.get(nodeId)!=null) nodes.remove(nodeId);
        else if("join".equals(msg) && controller!=null) controller.receive(msg, nodeId);//nós que entram após a existência de um controlador
        else{
            if("new_controller".equals(msg)){
                startRequestsFlag=true;
                //Informa os pesos correntes ao novo controlador
                if (controller!=null){
                    Observable newController=nodes.get(nodeId);
                    newController.setW1(controller.getW1());
                    newController.setW2(controller.getW2());
                    newController.setW3(controller.getW3());
                    newController.setW4(controller.getW4());
                    newController.setW5(controller.getW5());
                }
                controller=nodes.get(nodeId);
                
                try {
                    experiment1.append("controller "+controller.getNodeId()+";"+counter+";"+controller.getMobilityValue()+";"+controller.getBatteryLevel()+
                            ";"+controller.getMemoryAvaliable()+";"+
                                    controller.getSignalStrentch()+";"+
                                    controller.getMipsValue()+"\n");
                } catch (IOException ex) {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                }
                //Estatísticas de falha do controlador
                if (controllerFailureFlag){
                    long time=TimeCounter.timeDifference(t1, LocalDateTime.now());
                    failureString+=time+"\n";
                    try {
                        experiment2.append(failureString);
                    } catch (IOException ex) {
                        Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    controllerFailureFlag=false;
                }  
            }
            nodes.forEach((Observable o) -> {       
            if (o!=null && Byte.compare(o.getNodeId(),nodeId)!=0) o.receive(msg);
            });
        }
    }
    /**
     * Obtém o nível de bateria de um nó
     * @param nodeId Id do nó que se deseja conhecer o nível de bateria
     * @return inteiro com o nível de bateria
     */
    synchronized public int getBatteryLevel(Byte nodeId){
        if (nodes.get(nodeId)!=null){ 
            Observable n=nodes.get(nodeId);
            return n.getBatteryLevel();
        }
        return -1;    
    }
    
    synchronized public void unicast(String msg){
        //Falha de controlador
        if (controller==null) return;
        if(msg.startsWith("mips") && (!controllerFailureFlag)
                &&
                (controller.getBatteryLevel()<=0 
                || !controller.isConnected())
                ){
            controllerFailureFlag=true;
            t1=LocalDateTime.now();
            if (controller.getBatteryLevel()<=0){
                failureString=controller.getNodeId()+";BATTERY_DISCHARGE;";
                nodes.remove(controller.getNodeId());
                controller.stopNode();
            }else if(!controller.isConnected()) failureString=controller.getNodeId()+";NO_CONNECTION;";
            controller=null;
            nodes.forEach((Observable o) -> {       
                o.receive("new_election");
            });
        }else controller.receive(msg);
    }
    
    synchronized public Observable getNode(Byte nodeId){
        return nodes.get(nodeId);
    }
    
    /**
     * Envia uma mensagem a um nó específico
     * @param nodeId id de um nó
     * @param msg mensagem a ser enviada
     */
    synchronized public void unicast(Byte nodeId, String msg){
        if (nodes.get(nodeId)!=null) nodes.get(nodeId).receive(msg);
    }
   
    /**
     * Thread que representa o nó requisitante
     */
    private class RequesterNodeThread extends Thread{
        @Override
        public void run(){
            //Aguarda o primeiro controlador se anunciar
            while(!startRequestsFlag){}
            Thread t=new NonControllersNodesThread();
            t.start();
            while(counter<=requisitions){
                
                try {
                    if (controller!=null){
                        experiment3.append("controller "+controller.getNodeId()+";"
                            +controller.getBatteryLevel()+";"+controller.getMemoryAvaliable()+";"+controller.getAmountMemory()+"\n");
                        experiment4.append(counter+";"+
                                controller.getW1()+";"+
                                controller.getW2()+";"+
                                controller.getW3()+";"+
                                controller.getW4()+";"+
                                controller.getW5()+"\n");
                        
                    }
                    if (observablesNodes.size()>0){
                        experiment5Battery.append(observablesNodes.get(0).getBatteryLevel()+
                                ";"+observablesNodes.get(1).getBatteryLevel()+
                                ";"+observablesNodes.get(2).getBatteryLevel()+
                                ";"+observablesNodes.get(3).getBatteryLevel()+
                                ";"+observablesNodes.get(4).getBatteryLevel()+"\n");
                        experiment5Memory.append(observablesNodes.get(0).getAmountMemory() +
                                ";"+observablesNodes.get(1).getAmountMemory()+
                                ";"+observablesNodes.get(2).getAmountMemory()+
                                ";"+observablesNodes.get(3).getAmountMemory()+
                                ";"+observablesNodes.get(4).getAmountMemory()+"\n");
                        experiment5Mobility.append(observablesNodes.get(0).getMobilityValue()+
                                ";"+observablesNodes.get(1).getMobilityValue()+
                                ";"+observablesNodes.get(2).getMobilityValue()+
                                ";"+observablesNodes.get(3).getMobilityValue()+
                                ";"+observablesNodes.get(4).getMobilityValue()+"\n");
                        experiment5SignalStrentch.append(observablesNodes.get(0).getSignalStrentch()+
                                ";"+observablesNodes.get(1).getSignalStrentch()+
                                ";"+observablesNodes.get(2).getSignalStrentch()+
                                ";"+observablesNodes.get(3).getSignalStrentch()+
                                ";"+observablesNodes.get(4).getSignalStrentch()+"\n");
                        
                    }
                    
                    Thread.sleep(1000);
                    ++counter;
                }catch (IOException ex) {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            simulationsDoneFlag=true;//finaliza gerenciamento das características dos nós
            //Despacha uma mensagem para finalizar todas as threads e finalizar as simulações
            nodes.forEach((o) -> {
                        o.receive("quit");
            });
            try {
                System.out.println("Finished simulations");
                System.out.println("Writing results in csv files...");
                Thread.sleep(250);
                experiment1.flush();
                experiment2.flush();
                experiment3.flush();
                experiment4.flush();
                experiment5Battery.flush();
                experiment5Memory.flush();
                experiment5Mobility.flush();
                experiment5SignalStrentch.flush();
                /*experiment6Memory.flush();
                experiment6Battery.flush();
                experiment6SignalStrentch.flush();
                experiment6Mobility.flush();*/
                experiment1.close();
                experiment2.close();
                experiment3.close();
                experiment4.close();
                experiment5Battery.close();
                experiment5Memory.close();
                experiment5Mobility.close();
                experiment5SignalStrentch.close();
                /*experiment6Memory.close();
                experiment6Battery.close();
                experiment6SignalStrentch.close();
                experiment6Mobility.close();*/
                System.out.println("Simulations were done");
                System.exit(1);
            } catch (IOException ex) {
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    
    private class ManageNodesCharacteristicsThread extends Thread{
        @Override
        public void run(){
            while(!simulationsDoneFlag){
                try {
                    sleep(1000);
                    nodes.forEach((o) -> {
                        o.manageCharacteristics();
                    });
                    /*String ciBatteryString="",ciMemoryString="",ciMobilityString="",ciSignalStrentchString="";
                    if (experiment6Node!=null){
                        ciBatteryString=experiment6Node.getBatteryLevel()+";";
                        ciMemoryString=experiment6Node.getAmountMemory()  +";";
                        ciMobilityString=experiment6Node.getMobilityValue()+";";
                        ciSignalStrentchString=experiment6Node.getSignalStrentch()+";";
                        if (counter!=experiment6RandomValue){
                            ciBatteryString+=";;\n";
                            ciMemoryString+=";;\n";
                            ciMobilityString+=";;\n";
                            ciSignalStrentchString+=";;\n";
                        }else if(counter==experiment6RandomValue && controller!=null){    
                            ciBatteryString+=controller.getCI(new Byte("1"), experiment6Node.getNodeId()).intValue()+";"+experiment6BatteryMCC.intValue()+"\n";
                            ciSignalStrentchString+=controller.getCI(new Byte("4"), experiment6Node.getNodeId()).floatValue()+";"+experiment6SignalStrentchMCC.floatValue()+"\n";
                            ciMemoryString+=controller.getCI(new Byte("2"), experiment6Node.getNodeId()).intValue()+";"+experiment6MemoryMCC.intValue()+"\n";
                            ciMobilityString+=controller.getCI(new Byte("3"), experiment6Node.getNodeId()).intValue()+";"+experiment6MobilityMCC.intValue()+"\n";
                        }
                        try {
                            experiment6Battery.append(ciBatteryString);
                            experiment6Memory.append(ciMemoryString);
                            experiment6Mobility.append(ciMobilityString);
                            experiment6SignalStrentch.append(ciSignalStrentchString);
                        } catch (IOException ex) {
                            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }   
                    //sorteia a próxima rodada a ser testado o experimento 6
                    if (counter==experiment6RandomValue){
                        do{
                            experiment6RandomValue=getCurrentRound()+randomValue.nextInt(10)+1;
                        }while(experiment6RandomValue>requisitions);    
                    }*/
                } catch (InterruptedException ex) {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    public int getNodesNumber(){
        return n;
    }
    
    private class NonControllersNodesThread extends Thread{
        @Override
        public void run(){
            while(!simulationsDoneFlag){
                try {
                    sleep(1600);
                    nodes.forEach((o) -> {
                        o.sendCharactheristicsToController();
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    
    public int getCurrentRound(){
        return counter;
    }

    public void addNode(Observable node){
        nodes.add(node);
    }
    
}
