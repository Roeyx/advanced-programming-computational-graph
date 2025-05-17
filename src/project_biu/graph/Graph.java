package project_biu.graph;

import project_biu.configs.Node;

import java.util.ArrayList;
import java.util.Collection;

// A graph data structure implemented by extending ArrayList<Node>
public class Graph extends ArrayList<Node> {

    // Constructor initializes the graph as an empty list
    public Graph() {
        super();
    }

    // Checks if any node in the graph is part of a cycle
    public boolean hasCycles() {
        for (Node node : this) {
            if (node.hasCycles()) {
                return true; // Cycle found
            }
        }
        return false; // No cycles detected
    }

    // Builds the graph structure based on topic-agent relationships
    public void createFromTopics() {
        Node topicNode;
        Node agentSubNode;
        Node agentPubNode;

        // Access topics from the TopicManager
        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();
        Collection<Topic> topics = tm.getTopics();

        // Iterate over each topic to build connections
        for (Topic topic : topics) {
            topicNode = getNodeByName("T" + topic.name); // Create/lookup topic node

            // Add edges from topic to each subscriber agent
            for (Agent sub : topic.subs) {
                agentSubNode = getNodeByName("A" + sub.getName());
                topicNode.addEdge(agentSubNode);
            }

            // Add edges from each publishing agent to the topic
            for (Agent pub : topic.pubs) {
                agentPubNode = getNodeByName("A" + pub.getName());
                agentPubNode.addEdge(topicNode);
            }
        }
    }

    // Retrieves a node by name or creates and adds it if not found
    private Node getNodeByName(String name) {
        for (Node node : this) {
            if (node.getName().equals(name)) {
                return node; // Return existing node
            }
        }
        Node newNode = new Node(name); // Create new node if not found
        this.add(newNode);             // Add it to the graph
        return newNode;                // Return the new node
    }
}
