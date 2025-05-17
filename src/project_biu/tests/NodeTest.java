package project_biu.tests;


import project_biu.configs.Node;

public class NodeTest {

    public void testNoCycle() {
        //graph 1
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");

        node1.addEdge(node2);
        node1.addEdge(node3);
        if(node1.hasCycles())
            System.out.println("testNoCycle fail graph 1");
        //graph 2
        Node node4 = new Node("4");
        Node node5 = new Node("5");
        Node node6 = new Node("6");
        node3.addEdge(node4);
        node3.addEdge(node6);
        node2.addEdge(node5);
        if(node1.hasCycles())
            System.out.println("testNoCycle fail graph 2");

    }

    public void testCycleInSingleComponent() {
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");

        node1.addEdge(node2);
        node2.addEdge(node3);
        node3.addEdge(node1); // Cycle here

        if(!node1.hasCycles())
            System.out.println("testCycleInSingleComponent fail ");
    }

    public void testCycleInDisconnectedGraph() {
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");
        Node node4 = new Node("4");
        Node node5 = new Node("5");

        node1.addEdge(node2);
        node2.addEdge(node3);
        node3.addEdge(node1); // Cycle here

        node4.addEdge(node5);
        if(!node1.hasCycles())
            System.out.println("testCycleInDisconnectedGraph fail node 1");
        if(node4.hasCycles())
            System.out.println("testCycleInDisconnectedGraph fail node 4");

    }

    public void testSelfLoop() {
        Node node1 = new Node("1");

        node1.addEdge(node1); // Self loop

        if(!node1.hasCycles())
            System.out.println("testSelfLoop fail node 1");
    }

    public void testComplexGraphWithMultipleCycles() {
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");
        Node node4 = new Node("4");
        Node node5 = new Node("5");

        node1.addEdge(node2);
        node2.addEdge(node3);
        node3.addEdge(node1); // Cycle here

        node2.addEdge(node4);
        node4.addEdge(node5);
        node5.addEdge(node2); // Another cycle here

        if(!node1.hasCycles())
            System.out.println("testComplexGraphWithMultipleCycles fail node 1");
        if(!node2.hasCycles())
            System.out.println("testComplexGraphWithMultipleCycles fail node 2");
        if(!node5.hasCycles())
            System.out.println("testComplexGraphWithMultipleCycles fail node 5");

    }
    public void TestNode(){
        System.out.println("TestNode started.");
        testNoCycle();
        testCycleInSingleComponent();
        testCycleInDisconnectedGraph();
        testComplexGraphWithMultipleCycles();
        System.out.println("TestNode finished.");

    }
}
