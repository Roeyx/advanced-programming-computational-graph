package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

/*
 * DivAgent is a reactive component that listens to two input streams (topics),
 * divides the first value (numerator) by the second (denominator), and
 * sends the result to a designated output topic. Handles division-by-zero cases safely.
 *
 * Core Fields:
 *   - name: Unique identifier for the agent instance
 *   - firstTopicName / secondTopicName: Input sources
 *   - firstInputVal / secondInputVal: Most recent values received
 *   - outputTopicName: Destination for computed results
 *   - counter: Tracks instance count for labeling
 */
public class DivAgent implements Agent {

    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;
    static int counter = 1;

    /**
     * Sets up subscriptions and publications for the division agent.
     * The agent listens to two input topics and publishes to one output topic.
     *
     * @param subs Array with two topic names: [0] = numerator, [1] = denominator
     * @param pubs Array with one topic name: the target for the output result
     */
    public DivAgent(String[] subs, String[] pubs){
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];
        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;

        // Generate a unique name for each instance
        this.name = "DivAgent " + counter;
        counter++;

        // Register this agent as a subscriber to both input topics
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);

        // Declare this agent as a publisher to the output topic
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        // Placeholder for reset logic, currently unused
    }

    /**
     * Handles incoming messages from subscribed topics.
     * When both numerator and denominator are known and valid,
     * performs division and sends the result. Avoids division by zero.
     *
     * @param topic The name of the topic that triggered the callback
     * @param msg   The received message containing a numeric value
     */
    @Override
    public void callback(String topic, Message msg) {
        // Update the corresponding value based on the topic
        if (topic.equals(firstTopicName)) {
            firstInputVal = msg.asDouble;
        }
        if (topic.equals(secondTopicName)) {
            secondInputVal = msg.asDouble;
        }

        // Perform division if both inputs are ready and denominator is not zero
        if (!Double.isNaN(firstInputVal) && !Double.isNaN(secondInputVal) && secondInputVal != 0.0) {
            double outputVal = firstInputVal / secondInputVal;
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));
        }
    }

    @Override
    public void close() {
        // Optional clean-up logic can go here
    }

    /**
     * @return Name of the first input topic (numerator)
     */
    public String getFirstTopicName() {
        return firstTopicName;
    }

    /**
     * @return Name of the second input topic (denominator)
     */
    public String getSecondTopicName() {
        return secondTopicName;
    }

    /**
     * @return Name of the topic where results are published
     */
    public String getOutputTopicName() {
        return outputTopicName;
    }
}
