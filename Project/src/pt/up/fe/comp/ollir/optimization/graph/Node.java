package pt.up.fe.comp.ollir.optimization.graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String id;
    private final int register;

    private List<List<Integer>> liveRange;
    private List<Edge> edges;
    private String color;
    private boolean isColored;

    public Node(String id, int register) {
        this.id = id;
        this.register = register;
        this.edges = new ArrayList<>();
        this.isColored = true;
    }

    public String getId() {
        return id;
    }

    public int getRegister() {
        return register;
    }

    public void setIsColored(boolean newValue) {
        this.isColored = newValue;
    }

    public boolean isColored() {
        return isColored;
    }

    public void setLiveRange(List<List<Integer>> liveRange) {
        this.liveRange = liveRange;
    }

    public List<List<Integer>> getLiveRange() {
        return liveRange;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<String> getActiveEdges() {
        ArrayList<String> result = new ArrayList<>();
        for (var edge: edges) {
            if (edge.getNode2().isColored())
                result.add(edge.getNode2().getId());
        }
        return result;
    }

    public int getNumberActiveEdges() {
        return getActiveEdges().size();
    }

    public boolean intersectNodeLiveRange(List<List<Integer>> nodeLiveRange) {
        for (var range : liveRange) {
            for (var intersectRange : nodeLiveRange) {
                if (range.get(0) >= intersectRange.get(1)
                        || range.get(1) <= intersectRange.get(0)) continue;
                return true;
            }
        }
        return false;
    }
}
