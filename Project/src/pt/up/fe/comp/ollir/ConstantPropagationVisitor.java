package pt.up.fe.comp.ollir;

import pt.up.fe.comp.analysis.SemanticAnalysisVisitor;
import pt.up.fe.comp.ast.AstNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConstantPropagationVisitor extends OptimizerVisitor {

    public ConstantPropagationVisitor(SymbolTable symbolTable) {
        super(symbolTable);

        addVisit(AstNode.METHOD_DECLARATION, this::methodVisit);
        setDefaultVisit(this::defaultVisit);
    }

    private Integer methodVisit(JmmNode method, Integer dummy) {
        String methodSignature = method.get("name");

        var statements = method.getChildren().subList(1, method.getNumChildren()).stream()
                .filter(statement -> SemanticAnalysisVisitor.isStatement(statement))
                .collect(Collectors.toList());

        for (int i = 0; i < statements.size(); i++) {
            var statement = statements.get(i);

            if ("AssignStatement".equals(statement.getKind())) {
                propagateConstant(statements.subList(i, statements.size()), methodSignature);
            }
        }

        return null;
    }

    private void propagateConstant(List<JmmNode> statements, String method) {
        var assignStatement = statements.get(0);

        List<String> idInfo = new ArrayList<>();

        idInfo.add(assignStatement.getJmmChild(0).get("name"));
        idInfo.add(Objects.requireNonNull(getIdentifierType(idInfo.get(0), method)).get(0));
        idInfo.add(getTerminalValue(assignStatement.getJmmChild(1)));

        if (idInfo.get(2) == null || statements.size() < 2) return;

        replaceIdentifierValue(statements.subList(1, statements.size()), idInfo);
    }

    public void replaceIdentifierValue(List<JmmNode> statements, List<String> idInfo) {
        for (var statement : statements) {
            if(!visitReplace(statement, idInfo)) return;
        }
    }

    private boolean visitReplace(JmmNode node, List<String> idInfo) {
        switch (node.getKind()) {
            case "AssignStatement" -> {
                visitReplace(node.getJmmChild(1), idInfo);
                if (isReassign(node, idInfo)) return false;
            }
            case "ArrayAssignStatement" -> {
                visitReplace(node.getJmmChild(1), idInfo);
                visitReplace(node.getJmmChild(2), idInfo);
            }
            case "Identifier" -> {
                if (!node.get("name").equals(idInfo.get(0))) break;

                var parentNode = node.getJmmParent();
                var index = parentNode.removeJmmChild(node);
                if (index == -1) break;

                var newNode = new JmmNodeImpl("Terminal");
                newNode.put("type", idInfo.get(1));
                newNode.put("image", idInfo.get(2));
                newNode.put("col", node.get("col"));
                newNode.put("line", node.get("line"));

                parentNode.add(newNode, index);
                OptimizerVisitor.optimizationChanges++;
            }
            default -> {
                for (int i = 0; i < node.getNumChildren(); i++) {
                    if (!visitReplace(node.getJmmChild(i), idInfo)) return false;
                }
            }
        }
        return true;
    }

    private boolean isReassign(JmmNode assign, List<String> idInfo) {
        return assign.getJmmChild(0).get("name").equals(idInfo.get(0));
    }
}