package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

// A basic arithmetic agent that adds two numbers from separate topics and outputs the result
public class PlusAgent implements Agent {

    String name;                      // Unique agent name
    String firstTopicName;           // Name of the first input topic
    private Double firstInputVal;    // Value received from the first topic
    String secondTopicName;          // Name of the second input topic
    private Double secondInputVal;   // Value received from the second topic
    String outputTopicName;          // Name of the output topic
    static int counter = 1;          // Counter to differentiate agent instances

    // Constructor sets up subscriptions and publishing channels
    public PlusAgent(String[] subs, String[] pubs){
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;

        // Assign a unique name to the agent
        this.name = "PlusAgent " + counter;
        counter++;

        // Subscribe to both input topics
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);

        // Register as a publisher for the output topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        // No specific state to reset in this implementation
    }

    // Handle incoming messages and perform addition when both inputs are available
    @Override
    public void callback(String topic, Message msg) {
        // Update stored value based on topic
        if(topic.equals(firstTopicName)){
            firstInputVal = msg.asDouble;
        }
        if(topic.equals(secondTopicName)){
            secondInputVal = msg.asDouble;
        }

        // Check if both inputs are available before calculating
        if (!Double.isNaN(firstInputVal) && !Double.isNaN(secondInputVal)) {
            Double outputVal = firstInputVal + secondInputVal;

            // Publish result to the output topic
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));
        }
    }

    @Override
    public void close() {
        // No cleanup necessary
    }

    // Getters for internal topic names
    public String getFirstTopicName() {
        return firstTopicName;
    }

    public String getSecondTopicName() {
        return secondTopicName;
    }

    public String getOutputTopicName() {
        return outputTopicName;
    }
}
