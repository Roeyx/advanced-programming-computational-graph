package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

/**
 * MulAgent is an agent that listens to two input topics,
 * multiplies the received numeric values, and publishes the result to an output topic.
 *
 * This agent demonstrates a basic binary arithmetic operation in a reactive publish-subscribe model.
 */
public class MulAgent implements Agent {

    // Unique identifier for the agent instance
    String name;

    // Name of the first topic (left operand of multiplication)
    String firstTopicName;

    // Most recent value received from first topic
    private Double firstInputVal;

    // Name of the second topic (right operand of multiplication)
    String secondTopicName;

    // Most recent value received from second topic
    private Double secondInputVal;

    // Output topic where the product is published
    String outputTopicName;

    // Used to generate unique names for each agent instance
    static int counter = 1;

    /**
     * Constructs a MulAgent and sets up its subscriptions and publishing behavior.
     *
     * @param subs Array with two topic names: [0] = first input, [1] = second input
     * @param pubs Array with one topic name: [0] = output topic
     */
    public MulAgent(String[] subs, String[] pubs) {
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];

        // Initialize values to NaN (indicating no data received yet)
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;

        // Assign a unique name using static counter
        this.name = "MulAgent " + counter;
        counter++;

        // Subscribe this agent to both input topics
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);

        // Register this agent as a publisher to the output topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);
    }

    /**
     * Returns the unique name of this agent instance.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Resets the internal state. Not currently used.
     */
    @Override
    public void reset() {}

    /**
     * Handles incoming messages. Stores the latest value and,
     * if both inputs are available, computes and publishes the product.
     *
     * @param topic The topic the message was received from
     * @param msg   The message containing a numeric value
     */
    @Override
    public void callback(String topic, Message msg) {
        // Identify which topic the message came from and store its value
        if (topic.equals(firstTopicName)) {
            firstInputVal = msg.asDouble;
        }
        if (topic.equals(secondTopicName)) {
            secondInputVal = msg.asDouble;
        }

        // Ensure both values are valid before performing the operation
        if (!Double.isNaN(firstInputVal) && !Double.isNaN(secondInputVal)) {
            double outputVal = firstInputVal * secondInputVal;

            // Publish the multiplication result to the output topic
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));
        }
    }

    /**
     * Clean-up logic if needed during shutdown. Currently not used.
     */
    @Override
    public void close() {}

    // Accessor methods for external inspection or testing

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
