package pt.up.fe.comp.analysis;

import pt.up.fe.comp.ast.AstNode;
import pt.up.fe.comp.jmm.analysis.table.Symbol;
import pt.up.fe.comp.jmm.ast.JmmNode;
import pt.up.fe.comp.jmm.ast.PreorderJmmVisitor;
import pt.up.fe.comp.jmm.report.Report;
import pt.up.fe.comp.jmm.report.ReportType;
import pt.up.fe.comp.jmm.report.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolTableVisitor extends PreorderJmmVisitor<SymbolTableMap, Integer> implements Reporter {
    private final List<Report> reports;

    public SymbolTableVisitor() {
        this.reports = new ArrayList<>();

        addVisit(AstNode.IMPORT_DECLARATION, this::importVisit);
        addVisit(AstNode.CLASS_DECLARATION, this::classVisit);
        addVisit(AstNode.METHOD_DECLARATION, this::methodVisit);

    }

    private Integer importVisit(JmmNode node, SymbolTableMap symbolTable) {

        var importString = node.getChildren().stream()
                .map(id -> id.get("name"))
                .collect(Collectors.joining("."));

        symbolTable.addImport(importString);
        return 1;
    }

    private Integer classVisit(JmmNode node, SymbolTableMap symbolTable) {

        symbolTable.setClassName(node.get("name"));
        node.getOptional("super").ifPresent(superClass -> validParent(node, superClass, symbolTable));

        for (var child : node.getChildren()){
            if (!child.getKind().equals("VarDeclaration")) break;
            symbolTable.addField(new Symbol(AnalysisUtils.createType(child.getJmmChild(0)), child.get("name")));
        }
        return 1;
    }

    private Integer methodVisit(JmmNode node, SymbolTableMap symbolTable) {
        var methodSignature = node.get("name");

        if (!validMethod(node, symbolTable, methodSignature)) {
            return -1;
        }

        var returnType = AnalysisUtils.createType(node.getJmmChild(0));

        var params = node.getChildren().subList(1, node.getNumChildren()).stream()
                .filter(arg -> arg.getKind().equals("Param"))
                .collect(Collectors.toList());

        var paramSymbols = params.stream()
                .map(param -> new Symbol(AnalysisUtils.createType(param.getJmmChild(0)), param.get("name")))
                .collect(Collectors.toList());

        var localVars = node.getChildren().subList(1, node.getNumChildren()).stream()
                .filter(localVar -> localVar.getKind().equals("VarDeclaration"))
                .collect(Collectors.toList());

        var localVarSymbols =  localVars.stream()
                .map(localVar -> new Symbol(AnalysisUtils.createType(localVar.getJmmChild(0)), localVar.get("name")))
                .collect(Collectors.toList());

        symbolTable.addMethod(methodSignature, returnType, paramSymbols, localVarSymbols);
        return 1;
    }

    private boolean validMethod(JmmNode node, SymbolTableMap symbolTable, String methodSignature) {
        if (symbolTable.hasMethod(methodSignature)) {
            addReport(node, "Found duplicated method with signature '" + methodSignature +"'!");
            return false;
        }

        return true;
    }

    private void validParent(JmmNode node, String parent, SymbolTableMap symbolTable) {

        if (symbolTable.hasImport(parent)) {
            symbolTable.setParent(parent);
            return;
        }

        addReport(node, "Super class '"+ parent +"' not found in import statements!");
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
