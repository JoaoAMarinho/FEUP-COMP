package pt.up.fe.comp.analysis;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SemanticAnalyser extends PreorderJmmVisitor<Integer, Integer> implements Reporter {
    private final List<Report> reports = new ArrayList<>();
    protected final SymbolTableMap symbolTable;

    protected SemanticAnalyser(SymbolTableMap symbolTable) {
        this.symbolTable = symbolTable;
    }

    protected Type getExpressionType(JmmNode node, String methodSignature) {

        switch (node.getKind()) {
            case "BinOp" -> {
                return getBinaryOpType(node, methodSignature);
            }
            case "UnaryOp" -> {
                return getUnaryOpType(node, methodSignature);
            }
            case "Terminal" -> {
                return getTerminalType(node, methodSignature);
            }
            case "Identifier" -> {
                return getIdentifierType(node, methodSignature);
            }
            case "MemberCall" -> {
                return getMemberCallType(node, methodSignature);
            }
            default -> throw new RuntimeException("Invalid node kind in expression.");
        }
    }

    private Type getBinaryOpType(JmmNode node, String methodSignature) {
        var operation = node.get("op");

        switch (operation) {
            case "AND" -> {
                var typeLeft = getExpressionType(node.getJmmChild(0), methodSignature);
                var typeRight = getExpressionType(node.getJmmChild(1), methodSignature);

                var booleanType = createType("boolean");
                if (typeLeft.matches(booleanType) && typeRight.matches(booleanType)) {
                    return booleanType;
                }

                addReport(node, "Incompatible type in AND expresion!");
                return createType("invalid");
            }
            case "LT", "ADD", "SUB", "MUL", "DIV" -> {
                var typeLeft = getExpressionType(node.getJmmChild(0), methodSignature);
                var typeRight = getExpressionType(node.getJmmChild(1), methodSignature);

                var intType = createType("int");
                if (typeLeft.matches(intType) && typeRight.matches(intType)) {
                    return operation.equals("LT") ? createType("boolean") : intType;
                }
                addReport(node, "Incompatible type in ARITHMETIC expresion!");
                return createType("invalid");
            }
            case "ACCESS" -> {
                var typeLeft = getExpressionType(node.getJmmChild(0), methodSignature);
                var typeRight = getExpressionType(node.getJmmChild(1), methodSignature);

                if (typeLeft.isArray() && typeRight.matches(createType("int"))) {
                    return createType(typeLeft.getName());
                }

                addReport(node, "Incompatible type in ARRAY ACCESS expression!");
                return createType("invalid");
            }
            default -> throw new RuntimeException("Invalid node kind in expression.");
        }
    }

    private Type getTerminalType(JmmNode node, String methodSignature) {
        if (node.get("image").equals("this")){
            if (methodSignature.equals("main")) {
                addReport(node, "Cannot access 'this' inside 'main' function!");
                return createType("invalid");
            }
            return createType(symbolTable.getClassName());
        }
        return createType(node.get("type"));
    }

    private Type getUnaryOpType(JmmNode node, String methodSignature) {
        var operation = node.get("op");

        switch (operation) {
            case "NOT" -> {
                var type = getExpressionType(node.getJmmChild(0), methodSignature);

                if (type.matches(createType("boolean"))) {
                    return type;
                }
                addReport(node, "Incompatible type " + type.toString() + " in NOT expresion!");
                return createType("invalid");
            }
            case "LENGTH" -> {
                var type = getExpressionType(node.getJmmChild(0), methodSignature);

                if (type.isArray()) {
                    return createType("int");
                }
                addReport(node, "Incompatible type " + type.toString() + " in LENGTH expresion!");
                return createType("invalid");
            }
            case "ARRAY_INIT" -> {
                var type = getExpressionType(node.getJmmChild(0), methodSignature);

                if (type.matches(createType("int"))) {
                    return createType("array");
                }
                addReport(node, "Incompatible type " + type.toString() + " in ARRAY_INIT expresion!");
                return createType("invalid");
            }
            case "OBJ_INIT" -> {
                var typeName = node.getJmmChild(0).get("name");
                if (typeName.equals(symbolTable.getClassName()) || symbolTable.hasImport(typeName)) {
                    return createType(typeName);
                }

                addReport(node, "Incompatible type " + typeName + " in OBJ_INIT expresion!");
                return createType("invalid");
            }
            default -> throw new RuntimeException("Invalid node kind in expression.");
        }
    }

    protected Type getIdentifierType(JmmNode node, String methodSignature) {
        var name = node.get("name");

        // localVars
        var localVars = this.symbolTable.getLocalVariables(methodSignature);
        for (Symbol localVar : localVars) {
            if (localVar.getName().equals(name)) {
                return localVar.getType();
            }
        }

        // params
        var params = this.symbolTable.getParameters(methodSignature);
        for (Symbol param : params) {
            if (param.getName().equals(name)) {
                return param.getType();
            }
        }

        // class fields
        if (!methodSignature.equals("main")) {
            var fields = this.symbolTable.getFields();
            for (Symbol field : fields) {
                if (field.getName().equals(name)) {
                    return field.getType();
                }
            }
        }

        addReport(node,"Variable "+ name +" not defined, in "+ methodSignature + "!");
        return createType("undefined");
    }

    private Type getMemberCallType(JmmNode node, String methodSignature) {
        var callerType = getExpressionType(node.getJmmChild(0), methodSignature);

        if (callerType.matches(createType(symbolTable.getClassName()))) {
            var callerMethod = node.getJmmChild(1).get("name");

            if (symbolTable.getMethods().contains(callerMethod)) {

                var params = node.getJmmChild(1).getChildren().stream()
                        .map(param -> getExpressionType(param, methodSignature))
                        .collect(Collectors.toList());
                var methodParams = symbolTable.getParameters(callerMethod).stream()
                        .map(param -> param.getType())
                        .collect(Collectors.toList());

                if(params.equals(methodParams)) return symbolTable.getReturnType(callerMethod);
                addReport(node, "Method call parameters do not match corresponding class method '" + methodSignature + "'!");
                return createType("invalid");
            }

            if (symbolTable.getSuper() != null) return createType("valid");

            addReport(node, "Method does not exist in class!");
            return createType("invalid");
        }

        if (callerType.getName().equals("valid")) {
            return callerType;
        }

        if (callerType.equals(createType("undefined"))) {
            reports.remove(reports.size()-1);
            callerType = createType(node.getJmmChild(0).get("name"));
        }

        if (symbolTable.hasImport(callerType.getName())) return createType("valid");
        addReport(node, "Undefined method call!");
        return createType("invalid");
    }

    protected Type createType(String typeName) {
        boolean isArray = typeName.equals("array");
        if (isArray) {
            typeName = "int";
        }

        return new Type(typeName, isArray);
    }


    @Override
    public List<Report> getReports() {
        return this.reports;
    }

    @Override
    public void addReport(JmmNode node, String message) {
        this.reports.add(new Report(ReportType.ERROR, Stage.SEMANTIC,
                Integer.parseInt(node.get("line")),
                Integer.parseInt(node.get("col")),
                message));
    }
}
