package pt.up.fe.comp.ollir;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.ast.AJmmVisitor;
import pt.up.fe.comp.jmm.ast.JmmNode;

import java.util.Arrays;
import java.util.List;

public abstract class OptimizerVisitor extends AJmmVisitor<Integer, Integer> {
    protected final SymbolTable symbolTable;
    public static int optimizationChanges = 0;

    public OptimizerVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public static void resetChanges() {
        optimizationChanges = 0;
    }

    protected Integer defaultVisit(JmmNode node, Integer dummy) {
        for (int i = 0; i < node.getNumChildren(); i++)
            visit(node.getJmmChild(i));

        return null;
    }

    protected String getTerminalValue(JmmNode expression) {
        if (expression.getKind().equals("Terminal")) {
            if (expression.getOptional("type").isPresent()) {
                return expression.get("image");
            }
        }
        return null;
    }

    protected List<String> getIdentifierType(String name, String methodSignature) {

        // localVars
        var localVars = this.symbolTable.getLocalVariables(methodSignature);
        for (Symbol localVar : localVars) {
            if (localVar.getName().equals(name)) {
                return Arrays.asList(localVar.getType().getName(), String.valueOf(!localVar.getType().isArray()));
            }
        }

        // params
        var params = this.symbolTable.getParameters(methodSignature);
        for (Symbol param : params) {
            if (param.getName().equals(name)) {
                return Arrays.asList(param.getType().getName(), String.valueOf(!param.getType().isArray()));
            }
        }

        // class fields
        if (!methodSignature.equals("main")) {
            var fields = this.symbolTable.getFields();
            for (Symbol field : fields) {
                if (field.getName().equals(name)) {
                    return Arrays.asList(field.getType().getName(), "false");
                }
            }
        }

        return null;
    }
}
