package pt.up.fe.comp.ollir;

import pt.up.fe.comp.analysis.SymbolTableMap;
import pt.up.fe.comp.ast.AstNode;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OllirVisitor extends AJmmVisitor<Integer, Integer> {
    private final StringBuilder code;
    private final SymbolTableMap symbolTable;
    private final ExpressionVisitor expressionVisitor;
    private int labelCounter;

    public OllirVisitor(SymbolTableMap symbolTable) {
        this.code = new StringBuilder();
        this.symbolTable = symbolTable;
        this.expressionVisitor = new ExpressionVisitor(this.symbolTable);
        this.labelCounter = 1;


        addVisit(AstNode.START, this::startVisit);
        addVisit(AstNode.CLASS_DECLARATION, this::classVisit);
        addVisit(AstNode.METHOD_DECLARATION, this::methodVisit);
        addVisit(AstNode.ENCLOSED_STATEMENT, this::visitChildren);
        addVisit(AstNode.EXPRESSION_STATEMENT, this::expressionStatementVisit);
        addVisit(AstNode.IF_STATEMENT, this::ifStatementVisit);
        addVisit(AstNode.THEN_STATEMENT, this::visitChildren);
        addVisit(AstNode.ELSE_STATEMENT, this::visitChildren);
        addVisit(AstNode.LOOP_STATEMENT, this::loopStatementVisit);
        addVisit(AstNode.DO_STATEMENT, this::visitChildren);
        addVisit(AstNode.ARRAY_ASSIGN_STATEMENT, this::arrayAssignStatementVisit);
        addVisit(AstNode.ASSIGN_STATEMENT, this::assignStatement);
        addVisit(AstNode.RETURN_STATEMENT, this::returnStatement);
        addVisit(AstNode.RETURN_VOID, this::returnVoid);
    }

    public String incrementLabelCounter() {
        var counter = Integer.toString(labelCounter);
        labelCounter++;
        return counter;
    }

    public String getCode() {
        return code.toString();
    }

    private Integer startVisit(JmmNode node, Integer dummy) {

        for (String importStr : symbolTable.getImports()) {
            code.append("import ").append(importStr).append(";\n");
        }

        for (JmmNode child : node.getChildren()) {
            visit(child);
        }

        return 1;
    }

    private Integer classVisit(JmmNode node, Integer dummy) {

        code.append("public ").append(symbolTable.getClassName());

        var superClass = symbolTable.getSuper();
        if (superClass != null) {
            code.append(" extends ").append(superClass);
        }

        code.append(" {\n");

        var fields = symbolTable.getFields();
        fields.stream().forEach(symbol -> code.append(".field ")
                .append(OllirUtils.getCode(symbol))
                .append(";\n"));

        var methods = node.getChildren().stream()
                .filter(child -> child.getKind().equals("MethodDeclaration"))
                .collect(Collectors.toList());

        for (JmmNode method : methods) {
            visit(method);
        }

        code.append("}\n");

        return 1;
    }

    private Integer methodVisit(JmmNode node, Integer dummy) {
        var methodSignature = node.get("name");
        expressionVisitor.setMethodSignature(methodSignature);
        expressionVisitor.resetTemporaryIndex();

        code.append(".method public ");
        node.getOptional("isStatic").ifPresent(value -> code.append("static "));

        var params = symbolTable.getParameters(methodSignature);
        var paramCode = params.stream()
                .map(symbol -> OllirUtils.getCode(symbol))
                .collect(Collectors.joining(", "));

        code.append(methodSignature).append("(").append(paramCode).append(").");
        code.append(OllirUtils.getCode(symbolTable.getReturnType(methodSignature)));

        var lastParamIndex = getLastIndex(node, 1, "Param");
        var children = node.getChildren().subList(lastParamIndex, node.getNumChildren());

        code.append(" {\n");

        for (JmmNode child : children) {
            visit(child);
        }
        code.append("}\n");

        return 1;
    }

    private Integer expressionStatementVisit(JmmNode node, Integer dummy) {
        List<String> result = expressionVisitor.visit(node.getJmmChild(0));

        String codeBefore = result.get(0);
        String value = result.get(1);

        code.append(codeBefore).append(value).append(";\n");
        return 1;
    }

    private Integer ifStatementVisit(JmmNode node, Integer dummy) {
        List<String> result = expressionVisitor.visit(node.getJmmChild(0));

        var labelCounter = incrementLabelCounter();

        code.append(result.get(0));
        code.append("if (");
        code.append(result.get(1));
        code.append(") goto Then"+ labelCounter +";\n");
        visit(node.getJmmChild(2));
        code.append("goto Endif"+ labelCounter +";\n").append("Then"+labelCounter+":\n");
        visit(node.getJmmChild(1));
        code.append("Endif"+ labelCounter +":\n");

        return 1;
    }

    private Integer visitChildren(JmmNode node, Integer dummy) {
        for (var child : node.getChildren()) {
            visit(child);
        }
        return 1;
    }

    private Integer loopStatementVisit(JmmNode node, Integer dummy) {
        var labelCounter = incrementLabelCounter();

        List<String> result = expressionVisitor.visit(node.getJmmChild(0));

        code.append("Loop"+ labelCounter +":\n");
        code.append(result.get(0));

        if (node.getOptional("DoWhile").isPresent()) {
            visit(node.getJmmChild(1));
            code.append("if (");
            code.append(result.get(1));
            code.append(") goto Loop"+ labelCounter +";\n");
            return 1;
        }

        code.append("if (");
        code.append(result.get(1));
        code.append(") goto Body"+ labelCounter +";\n").append("goto EndLoop"+ labelCounter +";\n");

        code.append("Body"+ labelCounter +":\n");
        visit(node.getJmmChild(1));
        code.append("goto Loop"+ labelCounter +";\nEndLoop"+ labelCounter +":\n");

        return 1;
    }

    private Integer arrayAssignStatementVisit(JmmNode node, Integer dummy) {
        List<String> result = expressionVisitor.visit(node.getJmmChild(2));
        List<String> index = expressionVisitor.visit(node.getJmmChild(1));

        var value = result.get(1);
        code.append(result.get(0));
        code.append(index.get(0));

        List<String> idInfo = new ArrayList<>();
        OllirUtils.getIdentifierInfo(node.getJmmChild(0), symbolTable, expressionVisitor.getMethodSignature(), idInfo);

        String temp = expressionVisitor.getTemporary() + ".i32";

        StringBuilder codeBefore = new StringBuilder();
        codeBefore.append(temp).append(" :=.i32")
                .append(" ").append(index.get(1)).append(";\n");

        code.append(codeBefore.toString());

        code.append(idInfo.get(0)).append("[").append(temp).append("]").append(".").append(idInfo.get(1)).append(" :=.").append(idInfo.get(1)).append(" ")
                .append(value).append(";\n");

        return 1;
    }

    private Integer assignStatement(JmmNode node, Integer dummy) {
        List<String> result = expressionVisitor.visit(node.getJmmChild(1));
        var value = result.get(1);

        List<String> idInfo = new ArrayList<>();
        var isField = OllirUtils.getIdentifierInfo(node.getJmmChild(0), symbolTable, expressionVisitor.getMethodSignature(), idInfo);

        var valueInfo = value.split("\\.");
        if (valueInfo[valueInfo.length-1].equals("V")) {
            valueInfo[valueInfo.length-1] = idInfo.get(1);
            value = String.join(".", valueInfo);
        }

        code.append(result.get(0));

        if (isField) {
            code.append("putfield(this, ").append(idInfo.get(0)).append(".").append(idInfo.get(1));
            code.append(", ").append(value).append(")").append(".V;\n");
        } else {
            code.append(idInfo.get(0)).append(".").append(idInfo.get(1)).append(" :=.").append(idInfo.get(1)).append(" ")
                    .append(value).append(";\n");

            if (node.getJmmChild(1).getKind().equals("UnaryOp") && node.getJmmChild(1).get("op").equals("OBJ_INIT")) {
                code.append("invokespecial(").append(idInfo.get(0)).append(".").append(idInfo.get(1))
                        .append(",\"<init>\").V;\n");
            }
        }
        return 1;
    }

    private Integer returnStatement(JmmNode node, Integer dummy) {
        List<String> result = expressionVisitor.visit(node.getJmmChild(0));

        var type = OllirUtils.getCode(symbolTable.getReturnType(expressionVisitor.getMethodSignature()));

        code.append(result.get(0)).append("ret.").append(type).append(" ")
                .append(result.get(1)).append(".").append(type).append(";\n");
        return 1;
    }

    private Integer returnVoid(JmmNode node, Integer integer) {
        code.append("ret.V;\n");
        return 1;
    }

    private int getLastIndex(JmmNode node, int initialIndex, String kind) {
        for (int i = initialIndex; i < node.getNumChildren(); i++) {
            if (!node.getJmmChild(i).getKind().equals(kind)) {
                initialIndex = i;
                break;
            }
        }
        return initialIndex;
    }
}

