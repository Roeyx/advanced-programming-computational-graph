package project_biu.tests;

import project_biu.configs.GenericConfig;
import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

import java.util.Random;

public class GenericConfigTest {

    public static void testMul(){
        int c=Thread.activeCount();
        System.out.println(c);
        //create generic config
        GenericConfig gc=new GenericConfig();
        gc.setConfFile("C:\\Users\\roeyn\\Roey_Adir__Advanced_Programming_Project\\AdvancedProgrammingProject\\config_files\\abequation.conf"); // change to the exact loaction where you put the file.
       try {
           gc.create();

       }
       catch (Exception e) {}
       if(Thread.activeCount()!=c+3){
            System.out.println("the configuration did not create the right number of threads (-10)");
        }
        double result[]={0.0};
        TopicManagerSingleton.get().getTopic("E").subscribe(new Agent() {

            @Override
            public String getName() {
                return "";
            }

            @Override
            public void reset() {
            }

            @Override
            public void callback(String topic, Message msg) {
                result[0]=msg.asDouble;
                System.out.println("AGENT E CALLBACL"+result[0]);

            }

            @Override
            public void close() {
            }

        });

        Double x=2.0;
        Double y=1.0;
        TopicManagerSingleton.get().getTopic("A").publish(new Message(x));
        TopicManagerSingleton.get().getTopic("B").publish(new Message(y));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}

        if(result[0]!=(x+y)*(x+y+1)){
            System.out.println("your agents did not produce the desierd result (-10)");
        }


        gc.close();

        try {
        Thread.sleep(100);}
        catch (InterruptedException e) {}

        if(Thread.activeCount()!=c){
        System.out.println("your code did not close all threads (-10)");
        }

        System.out.println("done");

    }
    public static void testFileValidity(){
        GenericConfig gc=new GenericConfig();
        //exact path of conf file
        gc.setConfFile("C:\\Users\\roeyn\\Roey_Adir__Advanced_Programming_Project\\AdvancedProgrammingProject\\config_files\\complex.conf"); // change to the exact loaction where you put the file.
        try{gc.create();}
        catch (Exception e) {}
        System.out.println(gc.getNumberOfAgents());
    }
    public static void testLoopsAgents(){

        int c=Thread.activeCount();
        System.out.println(c);
        //create generic config
        GenericConfig gc=new GenericConfig();
        gc.setConfFile("C:\\Users\\roeyn\\Roey_Adir__Advanced_Programming_Project\\AdvancedProgrammingProject\\config_files\\simple.conf"); // change to the exact loaction where you put the file.
        try{gc.create();}
        catch (Exception e) {}
        if(Thread.activeCount()!=c+3){
            System.out.println("the configuration did not create the right number of threads (-10)");
        }

        double result[]={0.0};
        TopicManagerSingleton.get().getTopic("D").subscribe(new Agent() {

            @Override
            public String getName() {
                return "";
            }

            @Override
            public void reset() {
            }

            @Override
            public void callback(String topic, Message msg) {
                result[0]=msg.asDouble;
                System.out.println("AGENT D CALLBACL"+result[0]);

            }

            @Override
            public void close() {
            }

        });

        Double x=0.0;
        TopicManagerSingleton.get().getTopic("A").publish(new Message(x));
        TopicManagerSingleton.get().getTopic("B").publish(new Message(x));
        while(result[0]<3.0)
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        gc.close();
        if(Thread.activeCount()!=c){
            System.out.println("your code did not close all threads (-10)");
        }

        System.out.println("done");

    }
    public static void GCMainTrain(){//PASSED
        int c=Thread.activeCount();
        GenericConfig gc=new GenericConfig();
        gc.setConfFile("C:\\Users\\roeyn\\Roey_Adir__Advanced_Programming_Project\\AdvancedProgrammingProject\\config_files\\simple.conf"); // change to the exact loaction where you put the file.
        try{gc.create();}
        catch (Exception e) {}

        if(Thread.activeCount()!=c+2){
            System.out.println("the configuration did not create the right number of threads (-10)");
        }

        double result[]={0.0};

        TopicManagerSingleton.get().getTopic("D").subscribe(new Agent() {

            @Override
            public String getName() {
                return "";
            }

            @Override
            public void reset() {
            }

            @Override
            public void callback(String topic, Message msg) {
                result[0]=msg.asDouble;
            }

            @Override
            public void close() {
            }

        });

        Random r=new Random();
        for(int i=0;i<9;i++){
            int x,y;
            x=r.nextInt(1000);
            y=r.nextInt(1000);
            TopicManagerSingleton.get().getTopic("A").publish(new Message(x));
            TopicManagerSingleton.get().getTopic("B").publish(new Message(y));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

            if(result[0]!=x+y+1){
                System.out.println("your agents did not produce the desierd result (-10)");
            }
        }

        gc.close();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}

        if(Thread.activeCount()!=c){
            System.out.println("your code did not close all threads (-10)");
        }

        System.out.println("done");

    }
}
