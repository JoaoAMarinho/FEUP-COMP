package pt.up.fe.comp.analysis;

import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.analysis.table.SymbolTable;
import pt.up.fe.comp.jmm.analysis.table.Type;

import java.util.*;

public class SymbolTableMap implements SymbolTable {
    private final List<String> imports = new ArrayList<>();
    private String className;
    private String parent;
    private final List<Symbol> fields = new ArrayList<>();
    private final List<String> methods = new ArrayList<>();
    private final Map<String, Type> returnTypes = new HashMap<>();
    private final Map<String, List<Symbol>> parameters = new HashMap<>();
    private final Map<String, List<Symbol>> localVars = new HashMap<>();


    @Override
    public List<String> getImports() {
        return this.imports;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public String getSuper() {
        return this.parent;
    }

    @Override
    public List<Symbol> getFields() {
        return this.fields;
    }

    @Override
    public List<String> getMethods() {
        return this.methods;
    }

    @Override
    public Type getReturnType(String methodSignature) {
        return this.returnTypes.get(methodSignature);
    }

    @Override
    public List<Symbol> getParameters(String methodSignature) {
        return this.parameters.get(methodSignature);
    }

    @Override
    public List<Symbol> getLocalVariables(String methodSignature) {
        return this.localVars.get(methodSignature);
    }

    public void addImport(String importStr) {
        this.imports.add(importStr);
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public void addField(Symbol field) {
        this.fields.add(field);
    }

    public void addMethod(String methodSignature, Type returnType, List<Symbol> params, List<Symbol> localVars) {
        this.methods.add(methodSignature);
        this.returnTypes.put(methodSignature, returnType);
        this.parameters.put(methodSignature, params);
        this.localVars.put(methodSignature, localVars);
    }

    public boolean hasImport(String id) {
        for (String importStr : getImports()) {
            var importList = importStr.split("\\.");
            if (importList[importList.length-1].equals(id)){
                return true;
            }
        }
        return false;
    }

    public boolean hasMethod(String methodSignature) {
        return this.methods.contains(methodSignature);
    }

}
