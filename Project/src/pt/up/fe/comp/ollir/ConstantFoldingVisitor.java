package pt.up.fe.comp.ollir;

import pt.up.fe.comp.ast.AstNode;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;

public class ConstantFoldingVisitor extends OptimizerVisitor {
    public ConstantFoldingVisitor(SymbolTable symbolTable) {
        super(symbolTable);
        addVisit(AstNode.UNARY_OP, this::unaryOpVisit);
        addVisit(AstNode.BIN_OP, this::binaryOpVisit);
        setDefaultVisit(this::defaultVisit);
    }

    private Integer binaryOpVisit(JmmNode binaryOp, Integer dummy) {
        if (binaryOp.get("op").equals("ACCESS")) return null;

        var leftValue = getTerminalValue(binaryOp.getJmmChild(0));
        var rightValue = getTerminalValue(binaryOp.getJmmChild(1));

        if (leftValue == null || rightValue == null) {
            if (leftValue == null) visit(binaryOp.getJmmChild(0));
            if (rightValue == null) visit(binaryOp.getJmmChild(1));
            return null;
        }

        var parentNode = binaryOp.getJmmParent();
        var index = parentNode.removeJmmChild(binaryOp);
        if (index == -1) return null;
        JmmNode newNode = new JmmNodeImpl("Terminal");
        newNode.put("col", binaryOp.get("col"));
        newNode.put("line", binaryOp.get("line"));

        switch (binaryOp.get("op")) {
            case "AND" -> {
                newNode.put("type", "boolean");
                newNode.put("image", String.valueOf(Boolean.valueOf(leftValue) && Boolean.valueOf(rightValue)));
            }
            case "LT" -> {
                newNode.put("type", "boolean");
                newNode.put("image", String.valueOf(Integer.parseInt(leftValue) < Integer.parseInt(rightValue)));
            }
            case "ADD" -> {
                newNode.put("type", "int");
                newNode.put("image", String.valueOf(Integer.parseInt(leftValue) + Integer.parseInt(rightValue)));
            }
            case "SUB" -> {
                newNode.put("type", "int");
                newNode.put("image", String.valueOf(Integer.parseInt(leftValue) - Integer.parseInt(rightValue)));
            }
            case "MUL" -> {
                newNode.put("type", "int");
                newNode.put("image", String.valueOf(Integer.parseInt(leftValue) * Integer.parseInt(rightValue)));
            }
            case "DIV" -> {
                newNode.put("type", "int");
                newNode.put("image", String.valueOf(Integer.parseInt(leftValue) / Integer.parseInt(rightValue)));
            }
        }
        parentNode.add(newNode, index);
        OptimizerVisitor.optimizationChanges++;

        return null;
    }

    private Integer unaryOpVisit(JmmNode jmmNode, Integer integer) {
        return null;
    }
}
