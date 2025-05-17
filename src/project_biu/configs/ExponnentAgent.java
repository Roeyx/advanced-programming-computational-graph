package project_biu.configs;

import project_biu.graph.Agent;
import project_biu.graph.Message;
import project_biu.graph.TopicManagerSingleton;

/*
 * ExponnentAgent:
 * A reactive computational agent that listens to two data topics,
 * treats one as the base and the other as the exponent, and
 * publishes the result of base^exponent to an output topic.
 *
 * Fields:
 *   name            - Unique agent label using a static counter
 *   firstTopicName  - Topic expected to provide the base
 *   secondTopicName - Topic expected to provide the exponent
 *   outputTopicName - Target for publishing the computed result
 *   firstInputVal   - Latest value received from the base topic
 *   secondInputVal  - Latest value received from the exponent topic
 *   counter         - Static counter for naming instances uniquely
 */
public class ExponnentAgent implements Agent {

    String name;
    String firstTopicName;
    private Double firstInputVal;
    String secondTopicName;
    private Double secondInputVal;
    String outputTopicName;
    static int counter = 1;

    /**
     * Initializes topic subscriptions and sets up publishing behavior.
     * Also generates a unique agent name using a counter.
     *
     * @param subs  Array of topic names to subscribe to [base, exponent]
     * @param pubs  Array of output topic names [0] = result topic
     */
    public ExponnentAgent(String[] subs, String[] pubs){
        this.firstTopicName = subs[0];
        this.secondTopicName = subs[1];
        this.outputTopicName = pubs[0];

        this.firstInputVal = Double.NaN;
        this.secondInputVal = Double.NaN;

        this.name = "ExponnentAgent " + counter;
        counter++;

        // Register for incoming data
        TopicManagerSingleton.get().getTopic(firstTopicName).subscribe(this);
        TopicManagerSingleton.get().getTopic(secondTopicName).subscribe(this);

        // Register as publisher for the result
        TopicManagerSingleton.get().getTopic(outputTopicName).addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        // Not currently implemented
    }

    /**
     * Processes messages from input topics and performs exponentiation.
     * Publishes result only when both inputs are valid (not NaN).
     *
     * @param topic Topic the message came from
     * @param msg   Message containing a numeric value
     */
    @Override
    public void callback(String topic, Message msg) {
        // Store incoming values based on source topic
        if (topic.equals(firstTopicName)) {
            firstInputVal = msg.asDouble;
        }
        if (topic.equals(secondTopicName)) {
            secondInputVal = msg.asDouble;
        }

        // If both inputs are available, compute and publish result
        if (!Double.isNaN(firstInputVal) && !Double.isNaN(secondInputVal)) {
            double outputVal = Math.pow(firstInputVal, secondInputVal);
            TopicManagerSingleton.get().getTopic(outputTopicName).publish(new Message(outputVal));
        }
    }

    @Override
    public void close() {
        // Optional: clean-up logic goes here
    }

    /**
     * @return Name of the base-value topic
     */
    public String getFirstTopicName() {
        return firstTopicName;
    }

    /**
     * @return Name of the exponent-value topic
     */
    public String getSecondTopicName() {
        return secondTopicName;
    }

    /**
     * @return Output topic where results are published
     */
    public String getOutputTopicName() {
        return outputTopicName;
    }
}
