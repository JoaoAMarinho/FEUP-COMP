package pt.up.fe.comp.ollir.optimization.graph;

import org.specs.comp.ollir.Descriptor;
import org.specs.comp.ollir.Method;
import org.specs.comp.ollir.VarScope;

import java.util.*;

public class Graph {
    private final HashMap<String, Descriptor> varTable;
    private final List<HashMap<Integer, List<String>>> livenessAnalysis;

    private final boolean staticMethod;
    private int minimumRegisters;

    private final HashMap<Integer, Node> nodes;

    public Graph(List<HashMap<Integer, List<String>>> livenessAnalysis, Method method) {
        this.livenessAnalysis = livenessAnalysis;
        this.varTable = method.getVarTable();

        this.staticMethod = method.isStaticMethod();
        this.minimumRegisters = staticMethod ? 0 : 1;

        this.nodes = new HashMap<>();
        initializeNodes();
        initializeEdges();
    }

    private void initializeNodes() {
        for (var name : varTable.keySet()) {
            Descriptor descriptor = varTable.get(name);
            if (descriptor.getScope() == VarScope.PARAMETER || descriptor.getScope() == VarScope.FIELD) {
                minimumRegisters++;
                continue;
            }

            var reg = descriptor.getVirtualReg();
            var node = new Node(name, reg);

            node.setLiveRange(getLiveRange(name));
            nodes.put(reg ,node);
        }
    }

    private void initializeEdges() {
        List<Integer> indexes = new ArrayList<>(nodes.keySet());

        for (int i = 0; i < nodes.size(); i++) {
            Node firstNode = nodes.get(indexes.get(i));
            for (int j = i + 1; j < nodes.size(); j++) {
                Node secondNode = nodes.get(indexes.get(j));
                if (!firstNode.intersectNodeLiveRange(secondNode.getLiveRange())) continue;

                firstNode.addEdge(new Edge(firstNode, secondNode));
                secondNode.addEdge(new Edge(secondNode, firstNode));
            }
        }
    }

    private List<List<Integer>> getLiveRange(String name) {
        List<List<Integer>> result = new ArrayList<>();

        var in = livenessAnalysis.get(0);
        var out = livenessAnalysis.get(1);

        int from = -1;

        for (var instruction : in.keySet()) {
            if (from == -1) {
                if (out.get(instruction).contains(name)) from = instruction;
                continue;
            }

            if (!out.get(instruction).contains(name)) {
                result.add(Arrays.asList(from, instruction));
                from = -1;
            }
        }

        return result;
    }

    public void applyColoring(int k, boolean minimizeReg) {
        if (k <= minimumRegisters) {
            if (minimizeReg) {
                applyColoring(k+1, true);
                return;
            }
            System.err.println("Not enough registers to allocate.");
            return;
        }

        HashMap<Integer, List<String>> colors = new HashMap<>();
        for (int i = minimumRegisters; i < k; i++)
            colors.put(i, new ArrayList<>());

        Stack<Node> stack = new Stack<>();

        boolean removed;

        do {
            var it = nodes.entrySet().iterator();
            removed = false;

            while (it.hasNext()) {
                var node = it.next().getValue();
                if (node.getNumberActiveEdges() >= k) continue;

                stack.push(node);
                it.remove();
                node.setIsColored(false);
                removed = true;
            }
        } while (removed);

        if (!nodes.isEmpty()) {
            System.err.println("Erro");
            return;
        }

        HashMap<String, Descriptor> newVarTable = new HashMap<>();
        while (!stack.isEmpty()) {
            var node = stack.pop();
            node.setIsColored(true);
            nodes.put(node.getRegister(), node);

            boolean colored = false;
            for (var reg : colors.keySet()) {
                boolean canColor = true;
                for (var linkedNode: node.getActiveEdges()) {
                    if (colors.get(reg).contains(linkedNode)) {
                        canColor = false;
                        break;
                    }
                }

                if (!canColor) continue;

                colors.get(reg).add(node.getId());
                var descriptor = varTable.get(node.getId());
                newVarTable.put(node.getId(), new Descriptor(descriptor.getScope(), reg, descriptor.getVarType()));
                colored = true;
                break;
            }

            if (!colored) {
                if (minimizeReg) {
                    applyColoring(k+1, true);
                    return;
                }
                System.err.println("Not enough registers to allocate.");
                return;
            }

        }

        int reg = staticMethod ? 0 : 1;
        for (var name : varTable.keySet()) {
            Descriptor descriptor = varTable.get(name);
            if (descriptor.getScope() == VarScope.PARAMETER || descriptor.getScope() == VarScope.FIELD) {
                newVarTable.put(name, new Descriptor(descriptor.getScope(), reg, descriptor.getVarType()));
                reg++;
            }
        }

        ArrayList<Integer> used_reg = new ArrayList<>();
        for (var name : varTable.keySet()) {
            Descriptor descriptor = newVarTable.get(name);
            varTable.put(name, descriptor);

            if(!used_reg.contains(descriptor.getVirtualReg()))
                used_reg.add(descriptor.getVirtualReg());
        }

        if (!used_reg.contains(0))
            used_reg.add(0);

        System.out.println("Allocated " + used_reg.size() + " registers");
    }
}
