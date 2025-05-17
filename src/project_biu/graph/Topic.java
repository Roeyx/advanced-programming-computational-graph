package project_biu.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a communication channel or topic in a publish-subscribe messaging model.
 * Agents can subscribe to a topic to receive messages, and other agents can publish messages to it.
 * This class maintains lists of both publishing and subscribing agents, and handles broadcasting of messages.
 */
public class Topic {

    // The unique name identifying this topic
    public final String name;

    // A list of agents that are allowed to publish messages to this topic
    List<Agent> pubs;

    // A list of agents that are subscribed to receive messages from this topic
    List<Agent> subs;

    // Stores the last message published to the topic as plain text
    private String lastMessage;

    /**
     * Creates a new topic instance with a specific name.
     * Initializes internal structures to track agents that publish or subscribe to this topic.
     *
     * @param name The identifier name of the topic
     */
    Topic(String name) {
        this.name = name;
        this.pubs = new ArrayList<>();
        this.subs = new ArrayList<>();
    }

    /**
     * Registers an agent to receive messages published on this topic.
     * The agent will be notified with each new message via its callback method.
     *
     * @param a The agent that wishes to subscribe to this topic
     */
    public void subscribe(Agent a) {
        this.subs.add(a);
    }

    /**
     * Removes an agent from the list of subscribers.
     * After removal, the agent will no longer receive messages published to this topic.
     *
     * @param a The agent to be unsubscribed from this topic
     */
    public void unsubscribe(Agent a) {
        this.subs.remove(a);
    }

    /**
     * Broadcasts a message to all subscribed agents.
     * Each subscriber's callback method is called with the topic name and the message.
     * Also stores the text of the last message for record-keeping or future reference.
     *
     * @param m The message to be delivered to all subscribers
     */
    public void publish(Message m) {
        lastMessage = m.asText; // Store the message for future reference
        for (Agent a : this.subs) {
            a.callback(name, m); // Notify each subscribed agent
        }
    }

    /**
     * Adds an agent to the list of publishers allowed to send messages to this topic.
     * This helps track which agents are responsible for producing content on this topic.
     *
     * @param a The agent to be registered as a publisher
     */
    public void addPublisher(Agent a) {
        this.pubs.add(a);
    }

    /**
     * Removes an agent from the list of approved publishers for this topic.
     * This operation prevents the agent from being tracked as a message source for this topic.
     *
     * @param a The agent to be removed from the list of publishers
     */
    public void removePublisher(Agent a) {
        this.pubs.remove(a);
    }

    /**
     * Retrieves the most recently published message as plain text.
     * Useful for inspection, debugging, or displaying the last known state of the topic.
     *
     * @return A string representing the last message published on this topic
     */
    public String getLastMessage() {
        return lastMessage;
    }
}
