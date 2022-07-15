package pt.up.fe.comp.analysis;

import pt.up.fe.comp.jmm.analysis.table.Type;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.specs.util.SpecsCheck;

public class AnalysisUtils {
    public static Type createType(JmmNode node) {
        SpecsCheck.checkArgument(node.getKind().equals("Type"),
                () -> "Expected node of type 'Type' got '"+ node.getKind()+"'!");

        var typeName = node.get("name");
        var isArray = node.getOptional("isArray")
                .map(isArrayStr -> Boolean.valueOf(isArrayStr))
                .orElse(false);

        return new Type(typeName, isArray);
    }
}
