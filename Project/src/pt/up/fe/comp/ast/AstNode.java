package pt.up.fe.comp.ast;

import pt.up.fe.specs.util.SpecsStrings;

public enum AstNode {

    START,
    IMPORT_DECLARATION,
    CLASS_DECLARATION,
    METHOD_DECLARATION,
    ENCLOSED_STATEMENT,
    IF_STATEMENT,
    THEN_STATEMENT,
    ELSE_STATEMENT,
    LOOP_STATEMENT,
    DO_STATEMENT,
    ARRAY_ASSIGN_STATEMENT,
    ASSIGN_STATEMENT,
    EXPRESSION_STATEMENT,
    RETURN_STATEMENT,
    RETURN_VOID,
    UNARY_OP,
    BIN_OP,
    TERMINAL;

    private final String name;

    AstNode() {
        this.name = SpecsStrings.toCamelCase(name(), "_", true);
    }

    @Override
    public String toString() {
        return name;
    }
}
