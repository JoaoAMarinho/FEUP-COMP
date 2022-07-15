package pt.up.fe.comp.ollir;

import pt.up.fe.comp.analysis.SymbolTableMap;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionVisitor {
    private final SymbolTableMap symbolTable;
    private String methodSignature;
    private static int temporaryIndex;

    public ExpressionVisitor(SymbolTableMap symbolTable) {
        this.symbolTable = symbolTable;
        this.methodSignature = null;
        temporaryIndex = 1;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public static void resetTemporaryIndex() {
        temporaryIndex = 1;
    }

    public String getMethodSignature() {
        return this.methodSignature;
    }

    public List<String> visit(JmmNode expression) {
        return switch (expression.getKind()) {
            case "BinOp" -> visitBinOp(expression);
            case "UnaryOp" -> visitUnaryOp(expression);
            case "Terminal" -> visitTerminal(expression);
            case "Identifier" -> visitIdentifier(expression);
            case "MemberCall" -> visitMemberCall(expression);
            default -> new ArrayList<>();
        };
    }

    private List<String> visitMemberCall(JmmNode memberCall) {

        StringBuilder codeBefore = new StringBuilder();
        StringBuilder value = new StringBuilder();
        StringBuilder params = new StringBuilder();

        for (JmmNode param: memberCall.getJmmChild(1).getChildren()) {
            params.append(", ");
            List<String> resultParam = visit(param);
            codeBefore.append(resultParam.get(0));

            String temp = getTemporary();
            String dotType = "." + getValueType(resultParam.get(1));
            codeBefore.append(temp).append(dotType).append(" :=").append(dotType).append(" ")
                    .append(resultParam.get(1)).append(";\n");
            params.append(temp+dotType);
        }

        if (memberCall.getJmmChild(0).getKind().equals("Identifier")){
            String name = memberCall.getJmmChild(0).get("name");
            if (symbolTable.hasImport(name)) {
                value.append("invokestatic(").append(name).append(", ").append("\"")
                        .append(memberCall.getJmmChild(1).get("name")).append("\"")
                        .append(params).append(").V");
                return Arrays.asList(codeBefore.toString(), value.toString());
            }
        }

        List<String> result = visit(memberCall.getJmmChild(0));
        codeBefore.append(result.get(0));

        if (result.get(1).matches("\\$(.*)")) {
            String temp = getTemporary();
            String dotType = "." + getValueType(result.get(1));
            codeBefore.append(temp).append(dotType).append(" :=").append(dotType).append(" ")
                    .append(result.get(1)).append(";\n");
            result.set(1, temp + dotType);
        }

        var resultValue = result.get(1).equals("this."+symbolTable.getClassName()) ? "this" : result.get(1);

        value.append("invokevirtual(").append(resultValue).append(", ").append("\"")
                .append(memberCall.getJmmChild(1).get("name")).append("\"")
                .append(params).append(").");

        String returnType = "V";
        if (resultValue.equals("this") || getValueType(result.get(1)).equals(symbolTable.getClassName())){
            String type = symbolTable.getReturnType(memberCall.getJmmChild(1).get("name")).getName();
            returnType = OllirUtils.getOllirType(type);
        }
        value.append(returnType);
        return Arrays.asList(codeBefore.toString(), value.toString());
    }

    private List<String> visitIdentifier(JmmNode identifier) {
        StringBuilder value = new StringBuilder();
        List<String> idInfo = new ArrayList<>();
        var isField = OllirUtils.getIdentifierInfo(identifier, this.symbolTable, this.methodSignature, idInfo);
        var dotType = "." + idInfo.get(1);

        if (isField) {
            value.append("getfield(this, ").append(idInfo.get(0)).append(dotType).append(")").append(dotType);
        } else {
            value.append(idInfo.get(0)).append(dotType);
        }

        return Arrays.asList("", value.toString());
    }

    private List<String> visitTerminal(JmmNode terminal) {
        if (terminal.get("image").equals("this")) return Arrays.asList("", "this."+symbolTable.getClassName());
        return switch(terminal.get("type")) {
            case "boolean" -> Arrays.asList("", Boolean.parseBoolean(terminal.get("image")) ? "1.bool"  : "0.bool");
            case "int" -> Arrays.asList("", terminal.get("image") + ".i32");
            default -> new ArrayList<>();
        };
    }

    private List<String> visitUnaryOp(JmmNode unaryOp) {
        switch (unaryOp.get("op")) {
            case "LENGTH" -> {
                List<String> result = visit(unaryOp.getJmmChild(0));
                String temp = getTemporary();
                String dotType = "." + getValueType(result.get(1));

                var codeBefore = result.get(0) + temp + dotType + " :=" + dotType + " " + result.get(1) + ";\n";
                return Arrays.asList(codeBefore, "arraylength(" + temp + dotType + ").i32");
            }
            case "NOT" -> {
                List<String> result = visit(unaryOp.getJmmChild(0));
                return Arrays.asList(result.get(0), "!.bool " + result.get(1));
            }
            case "ARRAY_INIT" -> {
                List<String> result = visit(unaryOp.getJmmChild(0));

                String temp = getTemporary();
                String dotType = "." + getValueType(result.get(1));
                var codeBefore = result.get(0) + temp + dotType + " :=" + dotType + " " + result.get(1) + ";\n";

                return Arrays.asList(codeBefore, "new(array, " + temp + dotType + ").array.i32");
            }
            case "OBJ_INIT" -> {
                String name = unaryOp.getJmmChild(0).get("name");
                String temp = getTemporary();
                String dotType = "." + name;
                return Arrays.asList(temp+dotType+ " :="+dotType+" new("+ name + ")" + dotType +";\n", temp + dotType);
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }

    private List<String> visitBinOp(JmmNode binOp) {
        List<String> leftResult = visit(binOp.getJmmChild(0));
        List<String> rightResult = visit(binOp.getJmmChild(1));

        var token = binOp.getOptional("token").orElse("");

        if (token.equals("")) { //Array access
            StringBuilder codeBefore = new StringBuilder();

            String tempArray = getTemporary();
            String tempIndex = getTemporary();

            String tempFinal = getTemporary();

            var arrayType = "." + getValueType(leftResult.get(1));
            var dotType = "." + getArrayType(leftResult.get(1));

            codeBefore.append(leftResult.get(0)).append(rightResult.get(0));
            codeBefore.append(tempIndex).append(".i32 :=.i32 ").append(rightResult.get(1)).append(";\n");
            codeBefore.append(tempArray).append(arrayType).append(" :=").append(arrayType).append(" ").append(leftResult.get(1)).append(";\n");

            codeBefore.append(tempFinal).append(dotType).append(" :=").append(dotType).append(" ").append(tempArray)
                    .append("[").append(tempIndex).append(".i32]").append(dotType).append(";\n");

            return Arrays.asList(codeBefore.toString(), tempFinal + dotType);
        }

        var dotType = switch (binOp.get("op")) {
            case "AND", "LT" -> ".bool";
            case "ADD", "SUB", "MUL", "DIV" -> ".i32";
            default -> throw new IllegalStateException("Unexpected value: " + binOp.get("op"));
        };

        List<String> left = getCodeBefore(leftResult, token);
        String codeBeforeLeft = left.get(0);

        List<String> right = getCodeBefore(rightResult, token);
        String codeBeforeRight = right.get(0);

        String value = left.get(1) + " " + token + dotType + " " + right.get(1);

        String temp = getTemporary();
        var codeBefore = codeBeforeLeft + codeBeforeRight + temp + dotType + " :=" + dotType + " " + value + ";\n";

        return Arrays.asList(codeBefore, temp + dotType);
    }

    public String getTemporary() {
        String temp =  "t" + this.temporaryIndex;
        this.temporaryIndex++;
        return temp;
    }

    private String getValueType(String value) {
        if(value.matches("invokestatic(.*)") || value.matches("invokevirtual(.*)") || value.matches("getfield(.*)")) {
            var list = value.split("\\.");
            var isArray = list[list.length-2].equals("array");
            return isArray ? "array." + list[list.length-1] : list[list.length-1];
        }
        var list = Arrays.asList(value.split(" ")[0].split("\\."));
        if (list.get(list.size()-2).equals("array")) {
            return "array." + list.get(list.size()-1);
        }

        return list.get(list.size()-1);
    }

    private String getArrayType(String value) {
        if(value.matches("invokestatic(.*)") || value.matches("invokevirtual(.*)") || value.matches("getfield(.*)")) {
            var list = value.split("\\.");
            return list[list.length-1];
        }
        var list = value.split(" ")[0].split("\\.");
        return list[list.length-1];
    }

    private String getOpType(String token) {
        return switch (token) {
            case "<", "&&" -> "bool";
            case "+","-","/","*" -> "i32";
            default -> "";
        };
    }

    private List<String> getCodeBefore(List<String> info, String token) {
        String value = info.get(1);
        var type = info.get(0).equals("") ? getValueType(value) : getOpType(token);

        String temp = getTemporary() + "." + type;
        String codeBefore = info.get(0) + temp +
                " :=." + type + " " +
                value + ";\n";

        return Arrays.asList(codeBefore, temp);
    }
}
