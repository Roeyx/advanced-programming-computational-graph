package project_biu.configs;

import project_biu.graph.Message;
import java.util.*;

/**
 * Represents a node in a directed graph structure.
 * Each node can store a message, have a name, and maintain a list of outgoing edges to other nodes.
 * This class supports cycle detection using depth-first search (DFS) and neighbor listing.
 */
public class Node {

    // Unique identifier for this node
    private String name;

    // List of adjacent nodes (outgoing connections)
    private List<Node> edges;

    // Optional message/data associated with this node
    private Message msg;

    /**
     * Constructs a new Node with the given name.
     * Initializes the edge list.
     *
     * @param name the name of the node
     */
    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    /**
     * Adds a directed edge from this node to the given node.
     *
     * @param n the node to connect to
     */
    public void addEdge(Node n) {
        edges.add(n);
    }

    // Basic getter and setter for node name
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Getters and setters for the node's outgoing edges
    public List<Node> getEdges() { return edges; }
    public void setEdges(List<Node> edges) { this.edges = edges; }

    // Getters and setters for the node's message object
    public Message getMsg() { return msg; }
    public void setMsg(Message msg) { this.msg = msg; }

    /**
     * Detects whether the current node is part of a cycle in the graph.
     * Uses depth-first search and a recursion stack to identify cycles.
     *
     * @return true if a cycle is found, false otherwise
     */
    public boolean hasCycles() {
        return detectCycle(this, new HashSet<>(), new HashSet<>());
    }

    /**
     * Recursive helper for cycle detection using DFS.
     *
     * @param node          the node currently being visited
     * @param visitedNodes  set of nodes already visited
     * @param currentStack  nodes currently in the DFS recursion stack
     * @return true if a cycle is detected from this node
     */
    private boolean detectCycle(Node node, Set<Node> visitedNodes, Set<Node> currentStack) {
        if (currentStack.contains(node)) return true; // Back edge found
        if (visitedNodes.contains(node)) return false; // Already processed

        visitedNodes.add(node);
        currentStack.add(node);

        for (Node neighbor : node.edges) {
            if (detectCycle(neighbor, visitedNodes, currentStack)) return true;
        }

        currentStack.remove(node); // Backtrack
        return false;
    }

    /**
     * Prints all direct neighbors of this node to the console.
     * Useful for graph visualization or debugging.
     */
    public void printAllNeighbours() {
        for (Node neighbor : this.edges) {
            System.out.println(this.name + "->" + neighbor.name);
        }
    }
}
