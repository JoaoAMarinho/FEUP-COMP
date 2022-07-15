package pt.up.fe.comp.analysis;

import pt.up.fe.comp.ast.AstNode;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.JmmNodeImpl;

import java.util.stream.Collectors;

public class SemanticAnalysisVisitor extends SemanticAnalyser {

    public SemanticAnalysisVisitor(SymbolTableMap symbolTable) {
        super(symbolTable);

        addVisit(AstNode.METHOD_DECLARATION, this::methodVisit);
    }

    private Integer methodVisit(JmmNode node, Integer dummy) {

        var statements = node.getChildren().subList(1, node.getNumChildren()).stream()
                .filter(statement -> isStatement(statement))
                .collect(Collectors.toList());

        for (var statement : statements) {
            analyseStatement(statement, node.get("name"));
        }

        if(node.get("name").equals("main")) {
            var newNode = new JmmNodeImpl("ReturnVoid");
            node.add(newNode);
        }

        return 1;
    }

    private void analyseStatement(JmmNode node, String methodSignature) {
        switch (node.getKind()) {
            case "ExpressionStatement" -> getExpressionType(node.getJmmChild(0), methodSignature);
            case "ReturnStatement" -> {
                var methodType = symbolTable.getReturnType(methodSignature);
                var returnType = getExpressionType(node.getJmmChild(0), methodSignature);

                if (returnType.matches(methodType)) {
                    break;
                }

                addReport(node, "Return type does not match expected return type in '" + methodSignature + "'!");
            }
            case "IfStatement" -> {
                var expressionType = getExpressionType(node.getJmmChild(0), methodSignature);
                if (!expressionType.matches(createType("boolean"))) {
                    addReport(node, "IF expression type does not match expected 'boolean' type in '" + methodSignature + "'!");
                }
                analyseStatement(node.getJmmChild(1), methodSignature);
                analyseStatement(node.getJmmChild(2), methodSignature);
            }
            case "ThenStatement", "ElseStatement", "DoStatement" -> {
                analyseStatement(node.getJmmChild(0), methodSignature);
            }
            case "LoopStatement" -> {
                var expressionType = getExpressionType(node.getJmmChild(0), methodSignature);
                if (!expressionType.matches(createType("boolean"))) {
                    addReport(node.getJmmChild(0), "LOOP expression type does not match expected 'boolean' type in '" + methodSignature + "'!");
                }
                analyseStatement(node.getJmmChild(1), methodSignature);
            }
            case "ArrayAssignStatement" -> {
                var idType = getIdentifierType(node.getJmmChild(0), methodSignature);
                if (!idType.isArray()) {
                    addReport(node.getJmmChild(0), "ARRAY ASSIGN expression type does not match expected 'array' type in '" + methodSignature + "'!");
                    break;
                }

                if (!getExpressionType(node.getJmmChild(1), methodSignature).matches(createType("int"))) {
                    addReport(node.getJmmChild(0), "ARRAY ASSIGN index type does not match expected 'int' type in '" + methodSignature + "'!");
                    break;
                }

                var expectedType = createType(idType.getName());
                var assignmentValueType = getExpressionType(node.getJmmChild(2), methodSignature);
                if (!assignmentValueType.matches(expectedType)) {
                    addReport(node.getJmmChild(2), "ARRAY ASSIGN assign type does not match expected type in '" + methodSignature + "'!");
                    break;
                }
            }
            case "AssignStatement" -> {
                var idType = getIdentifierType(node.getJmmChild(0), methodSignature);
                var expressionType = getExpressionType(node.getJmmChild(1), methodSignature);

                if (symbolTable.hasImport(idType.getName())) {
                    if (expressionType.getName().equals(symbolTable.getClassName()) && symbolTable.getSuper() == null) {
                        addReport(node.getJmmChild(0), "ASSIGNMENT type does not match expected type in '" + methodSignature + "'!");
                    }
                    break;
                }
                if (symbolTable.hasImport(expressionType.getName())) {
                    if (idType.getName().equals(symbolTable.getClassName()) && symbolTable.getSuper() == null) {
                        addReport(node.getJmmChild(0), "ASSIGNMENT type does not match expected type in '" + methodSignature + "'!");
                    }
                    break;
                }

                if (!expressionType.matches(idType)) {
                    if (idType.getName().equals(symbolTable.getSuper())
                            && expressionType.getName().equals(symbolTable.getClassName())) break;

                    addReport(node.getJmmChild(0), "ASSIGNMENT type does not match expected type in '" + methodSignature + "'!");
                }
            }
            case "EnclosedStatement" -> {
                for (var child : node.getChildren()) {
                    analyseStatement(child, methodSignature);
                }
            }
        }
    }

    public static boolean isStatement (JmmNode node) {
        var kind= node.getKind();
        return !(kind.equals("Param") || kind.equals("VarDeclaration"));

    }
}

