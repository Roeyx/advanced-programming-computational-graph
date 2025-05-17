/**
 * BinOpAgent.java
 *
 * This class defines an agent that subscribes to two input topics,
 * applies a binary operation (e.g., addition or multiplication) on incoming values,
 * and publishes the result to a designated output topic.
 *
 * Part of the project_biu.configs module.
 *
 * Author: Roey
 * Date: May 2025
 */

package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

import java.util.function.BinaryOperator;

/**
 * BinOpAgent listens to two input topics and publishes the result
 * of a binary operation (like +, -, *, /) to an output topic.
 *
 * Each agent keeps track of the most recent input values from both topics.
 * Once it receives valid numeric inputs from both, it computes the result
 * and sends it as a message on the output topic.
 */
public class BinOpAgent implements Agent {

    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;
    BinaryOperator<Double> operation;

    public BinOpAgent(String name, String firstTopicName, String secondTopicName, String outputTopicName, BinaryOperator<Double> operation){
        this.name = name;
        this.firstTopicName = firstTopicName;
        this.secondTopicName = secondTopicName;
        this.outputTopicName = outputTopicName;
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;
        this.operation = operation;

        // Subscribe this agent to both input topics
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);

        // Register this agent as a publisher on the output topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);
    }

    /**
     * Reacts to incoming messages from subscribed topics.
     * When both inputs are received, it applies the operation and publishes the result.
     *
     * @param topic The topic the message came from
     * @param msg   The message carrying a Double value
     */
    @Override
    public void callback(String topic, Message msg) {
        // Store received value in the appropriate input variable
        if (topic.equals(firstTopicName)) {
            firstInputVal = msg.asDouble;
        }
        if (topic.equals(secondTopicName)) {
            secondInputVal = msg.asDouble;
        }

        // Check if both inputs are now ready to compute the result
        if ((!Double.isNaN(firstInputVal)) && (!Double.isNaN(secondInputVal))) {
            Double outputVal = operation.apply(firstInputVal, secondInputVal);

            // Publish the computed result to the output topic
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));
        }
    }

    @Override
    public void close() {
        // Optional: Clean-up logic for the agent, currently unused
    }

    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        // Optional: Logic to reset internal state, currently unused
    }

    public String getFirstTopicName() {
        return firstTopicName;
    }

    public String getOutputTopicName() {
        return outputTopicName;
    }

    public String getSecondTopicName() {
        return secondTopicName;
    }

    public BinaryOperator<Double> getOperation() {
        return operation;
    }
}
