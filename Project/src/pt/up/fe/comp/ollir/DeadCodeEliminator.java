package pt.up.fe.comp.ollir;

import pt.up.fe.comp.analysis.SemanticAnalysisVisitor;
import pt.up.fe.comp.ast.AstNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeadCodeEliminator extends OptimizerVisitor{
    public DeadCodeEliminator(SymbolTable symbolTable) {
        super(symbolTable);

        addVisit(AstNode.LOOP_STATEMENT, this::loopVisit);
        addVisit(AstNode.IF_STATEMENT, this::ifStatementVisit);
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
                if(checkUsage(statements.subList(i, statements.size()), methodSignature)){
                    statement.delete();
                    OptimizerVisitor.optimizationChanges++;
                }
                continue;
            }

            visit(statement);
        }

        return null;
    }

    private boolean checkUsage(List<JmmNode> statements, String methodSignature) {
        var assignStatement = statements.get(0);

        var idName = assignStatement.getJmmChild(0).get("name");
        var idInfo = getIdentifierType(idName, methodSignature);

        if(!Boolean.parseBoolean(idInfo.get(1))) return false;

        var typeName = idInfo.get(0);
        if (!typeName.equals("int") && !typeName.equals("boolean")) return false;


        if (statements.size() == 1) return true;
        return isRemovable(statements.subList(1, statements.size()), idName);
    }

    private boolean isRemovable(List<JmmNode> statements, String name) {
        for (JmmNode statement : statements) {
            switch (statement.getKind()) {
                case "AssignStatement" -> {
                    if (idInExpression(statement.getJmmChild(1), name)) return false;

                    var id = statement.getJmmChild(0);
                    if (id.get("name").equals(name)) return true;
                }
                case "EnclosedStatement" -> {
                    if (isRemovable(statement.getChildren(), name)) return true;
                }
                case "IfStatement" -> {
                    if (idInExpression(statement.getJmmChild(0), name)) return false;
                    if (isRemovable(statement.getJmmChild(1).getChildren(), name)) return true;
                    if (isRemovable(statement.getJmmChild(2).getChildren(), name)) return true;
                }
                case "LoopStatement" -> {
                    if (idInExpression(statement.getJmmChild(0), name)) return false;
                    if (isRemovable(statement.getJmmChild(1).getChildren(), name)) return true;
                }
                case "ArrayAssignmentStatement" -> {
                    if (idInExpression(statement.getJmmChild(1), name)) return false;
                }
                case "ExpressionStatement", "ReturnStatement" -> {
                    if (idInExpression(statement.getJmmChild(0), name)) return false;
                }
            }
        }
        return true;
    }

    private boolean idInExpression(JmmNode expression, String id) {
        switch (expression.getKind()) {
            case "BinOp" -> {
                return (idInExpression(expression.getJmmChild(0) ,id) || idInExpression(expression.getJmmChild(1), id));
            }
            case "UnaryOp" -> {
                return idInExpression(expression.getJmmChild(0) ,id);
            }
            case "Terminal" -> {
                return false;
            }
            case "Identifier" -> {
                return expression.get("name").equals(id);
            }
            case "MemberCall" -> {
                for (var expr : expression.getJmmChild(1).getChildren()) {
                    if (idInExpression(expr, id)) return true;
                }
            }
        }
        return false;
    }

    private Integer ifStatementVisit(JmmNode ifStatement, Integer dummy) {
        var value = getTerminalValue(ifStatement.getJmmChild(0));
        if (value == null) return null;

        JmmNode newNode;

        if (Boolean.valueOf(value)) {
            newNode = ifStatement.getJmmChild(1).getJmmChild(0);
        } else {
            newNode = ifStatement.getJmmChild(2).getJmmChild(0);
        }

        var parent = ifStatement.getJmmParent();
        var index = parent.removeJmmChild(ifStatement);
        parent.add(newNode, index);

        OptimizerVisitor.optimizationChanges++;
        return null;
    }

    private Integer loopVisit(JmmNode loopStatement, Integer dummy) {
        var value = getTerminalValue(loopStatement.getJmmChild(0));
        if (value == null) return null;

        if (!Boolean.parseBoolean(value)) {
            loopStatement.delete();
            OptimizerVisitor.optimizationChanges++;
        }

        loopStatement.put("DoWhile", "true");
        return null;
    }
}
