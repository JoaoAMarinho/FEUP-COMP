package pt.up.fe.comp.ollir.optimization.graph;

public class Edge {
    private final Node node1;
    private final Node node2;


    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public Node getNode2() {
        return node2;
    }
}
