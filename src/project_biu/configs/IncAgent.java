// IncAgent.java
package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

/**
 * IncAgent is a simple reactive component in a publish-subscribe system.
 * It subscribes to a single input topic, increments each received numeric value by 1,
 * and publishes the result to a designated output topic.
 *
 * This agent demonstrates basic message transformation and forwarding.
 */
public class IncAgent implements Agent {
    String name; // Unique name of this agent instance
    String inputTopicName; // Topic name from which the input value is received
    private Double inputVal; // The last value received from input topic
    String outputTopicName; // Topic name where the incremented value is published
    static int counter = 1; // Counter to generate unique agent names

    /**
     * Constructor sets up input/output topics and registers the agent with the messaging system.
     *
     * @param subs A one-element array with the input topic name
     * @param pubs A one-element array with the output topic name
     */
    public IncAgent(String[] subs, String[] pubs) {
        this.inputTopicName = subs[0];
        this.outputTopicName = pubs[0];
        this.inputVal = Double.NaN; // Initialize with NaN to indicate no input received yet

        this.name = "IncAgent " + counter; // Assign unique name to agent
        counter++;

        // Subscribe to input topic to receive messages
        TopicManagerSingleton.get().getTopic(inputTopicName).subscribe(this);

        // Declare intent to publish on output topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);
    }

    /**
     * Returns the name assigned to this agent.
     * Useful for identification within the system.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Placeholder for state-resetting logic.
     * Can be implemented if the agent needs to reset its internal state.
     */
    @Override
    public void reset() {}

    /**
     * Called whenever a message arrives on the subscribed topic.
     * Extracts the value, increments it by 1, and publishes the result.
     *
     * @param topic The name of the topic the message came from
     * @param msg The message received, expected to contain a double value
     */
    @Override
    public void callback(String topic, Message msg) {
        inputVal = msg.asDouble;

        // If a valid number was received, perform the increment operation
        if (!Double.isNaN(inputVal)) {
            Double outputVal = inputVal + 1;

            // Send the new value to the output topic
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));
        }
    }

    /**
     * Placeholder for clean-up logic when the agent is removed or the system is shutting down.
     */
    @Override
    public void close() {}

    // --- Accessors for testing, diagnostics, or external management ---

    public String getInputTopicName() {
        return inputTopicName;
    }

    public String getOutputTopicName() {
        return outputTopicName;
    }
}
