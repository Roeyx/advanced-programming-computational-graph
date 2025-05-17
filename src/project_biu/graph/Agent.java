package project_biu.graph;

/*
 * Represents the essential behavior required for an agent in a publish-subscribe or event-based architecture.
 * Each agent should be able to handle incoming messages, manage its internal state, and identify itself.
 *
 * Contract:
 * - getName(): Returns the identifier assigned to this agent.
 * - reset(): Clears or reinitializes the agent’s internal state.
 * - callback(String topic, Message msg): Handles a message associated with a specific topic.
 * - close(): Carries out any cleanup tasks before the agent is terminated.
 */
public interface Agent {
    String getName();                         // Returns the agent's name
    void reset();                             // Resets the agent's state
    void callback(String topic, Message msg); // Handles a message from a given topic
    void close();                             // Finalizes and releases agent resources
}
