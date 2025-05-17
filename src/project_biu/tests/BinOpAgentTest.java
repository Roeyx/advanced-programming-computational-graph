package project_biu.tests;

import project_biu.configs.Config;
import project_biu.configs.MathExampleConfig;
import project_biu.graph.Agent;
import project_biu.configs.BinOpAgent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

import java.util.Random;

public class BinOpAgentTest {
    public static class GetAgent implements Agent {

        public Message msg;
        public GetAgent(String topic){
            TopicManagerSingleton.get().getTopic(topic).subscribe(this);
        }

        @Override
        public String getName() { return "Get Agent";}

        @Override
        public void reset() {}

        @Override
        public void callback(String topic, Message msg) {
            this.msg=msg;
        }

        @Override
        public void close() {}

    }
    public static void testBinGraphCases() {
        TopicManagerSingleton.TopicManager tm=TopicManagerSingleton.get();
        BinOpAgent mulAgent = new BinOpAgent("mul", "R1", "R2", "R3", (x,y)->x*y);
        GetAgent ga=new GetAgent("R3");
        Double r1=6.0;
        Double r2=2.0;
        tm.getTopic(("R1")).publish(new Message(r1));
        System.out.println("R3 after R1:"+ga.msg.asDouble);
        tm.getTopic(("R2")).publish(new Message(r1));
        System.out.println("R3 after R2:"+ga.msg.asDouble);
        BinOpAgent plusAgent =new BinOpAgent("plus", "A", "B", "R1", (x, y)->x+y);//6+2=8
        BinOpAgent minusAgent =new BinOpAgent("minus", "A", "B", "R2", (x, y)->x-y);//6-2=4
        GetAgent gar1=new GetAgent("R1");
        GetAgent gar2=new GetAgent("R2");
        BinOpAgent divAgent =new BinOpAgent("Div", "R1", "R2", "R4", (x, y)->x/y);//8/4=2
        GetAgent gar4=new GetAgent("R4");
        tm.getTopic(("A")).publish(new Message(r1));
        System.out.println("R1 after A:"+gar1.msg.asDouble);
        System.out.println("R2 after A:"+gar2.msg.asDouble);
        System.out.println("R4 after A:"+gar4.msg.asDouble);
        tm.getTopic(("B")).publish(new Message(r2));
        System.out.println("R1 after B:"+gar1.msg.asDouble);
        System.out.println("R2 after B:"+gar2.msg.asDouble);
        System.out.println("R4 after B:"+gar4.msg.asDouble);
        BinOpAgent agent = new BinOpAgent(
                "DivisionAgent",
                "Topic1",
                "Topic2",
                "OutputTopic",
                (num1, num2) -> num1 / num2
        );
        GetAgent outputagent=new GetAgent("OutputTopic");

        tm.getTopic(("Topic1")).publish(new Message(20.0));
        tm.getTopic(("Topic2")).publish(new Message(0.0));

        System.out.println("OutputTopic after Topic2:"+outputagent.msg.asDouble);

    }
        public static void testBinGraph(){
        TopicManagerSingleton.TopicManager tm=TopicManagerSingleton.get();
        tm.clear();
        Config c=new MathExampleConfig();
            try {
                c.create();
            }
            catch (Exception e) {}

        GetAgent ga=new GetAgent("R3");

        Random r=new Random();
        int x=1+r.nextInt(100);
        int y=1+r.nextInt(100);
        tm.getTopic("A").publish(new Message(x));
        tm.getTopic("B").publish(new Message(y));
        double rslt=(x+y)*(x-y);

        if (Math.abs(rslt - ga.msg.asDouble)>0.05)
            System.out.println("your BinOpAgents did not produce the desired result (-20)");


    }

}
