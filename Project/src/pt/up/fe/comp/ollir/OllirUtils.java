package pt.up.fe.comp.ollir;

import pt.up.fe.comp.analysis.SymbolTableMap;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.List;

public class OllirUtils {

    public static String getCode(Symbol symbol){
        return symbol.getName() + "." + getCode(symbol.getType());
    }

    public static String getCode(Type type){
        StringBuilder code = new StringBuilder();

        if (type.isArray()) {
            code.append("array.");
        }

        code.append(getOllirType(type.getName()));

        return code.toString();
    }

    public static String getOllirType(String jmmType) {

        return switch (jmmType) {
            case "void" -> "V";
            case "int" -> "i32";
            case "boolean" -> "bool";
            default -> jmmType;
        };
    }

    public static boolean getIdentifierInfo(JmmNode identifier, SymbolTableMap symbolTable, String methodSignature, List<String> info) {
        var name = identifier.get("name");
        StringBuilder value = new StringBuilder();

        // localVars
        var localVars = symbolTable.getLocalVariables(methodSignature);
        for (Symbol localVar : localVars) {
            if (localVar.getName().equals(name)) {
                info.add(name);
                info.add(OllirUtils.getCode(localVar.getType()));
                return false;
            }
        }

        // params
        var params = symbolTable.getParameters(methodSignature);
        int paramIndex = 1;
        for (Symbol param : params) {
            if (param.getName().equals(name)) {
                value.append("$").append(paramIndex).append(".").append(param.getName());
                info.add(value.toString());
                info.add(OllirUtils.getCode(param.getType()));
                return false;
            }
            paramIndex++;
        }

        // class fields
        if (!methodSignature.equals("main")) {
            var fields = symbolTable.getFields();
            for (Symbol field : fields) {
                if (field.getName().equals(name)) {
                    info.add(name);
                    info.add(OllirUtils.getCode(field.getType()));
                    return true;
                }
            }
        }

        return false;
    }
}

