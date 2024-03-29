package pt.up.fe.comp.ast;

import pt.up.fe.comp.BaseNode;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;

public class NodePositionAnnotator extends PreorderJmmVisitor<Object, Integer> {

    public NodePositionAnnotator() {
        setDefaultVisit(this::anotatePosition);
    }

    private Integer anotatePosition(JmmNode node, Object dummy) {
        var baseNode = (BaseNode) node;

        node.put("line", Integer.toString(baseNode.getBeginLine()));
        node.put("col", Integer.toString(baseNode.getBeginColumn()));

        return 1;
    }
}
