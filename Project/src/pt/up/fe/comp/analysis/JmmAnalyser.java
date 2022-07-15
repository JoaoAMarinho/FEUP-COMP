package pt.up.fe.comp.analysis;

import java.util.ArrayList;
import java.util.List;

import pt.up.fe.comp.jmm.analysis.JmmAnalysis;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.parser.JmmParserResult;
import pt.up.fe.comp.jmm.report.Report;

public class JmmAnalyser implements JmmAnalysis {

    @Override
    public JmmSemanticsResult semanticAnalysis(JmmParserResult parserResult) {

        List<Report> reports = new ArrayList<>();
        SymbolTableMap symbolTable = new SymbolTableMap();

        var symbolTableVisitor = new SymbolTableVisitor();
        symbolTableVisitor.visit(parserResult.getRootNode(), symbolTable);
        reports.addAll(symbolTableVisitor.getReports());

        // Semantic Analysis
        var semanticAnalysisVisitor = new SemanticAnalysisVisitor(symbolTable);
        semanticAnalysisVisitor.visit(parserResult.getRootNode());
        reports.addAll(semanticAnalysisVisitor.getReports());

        return new JmmSemanticsResult(parserResult, symbolTable, reports);
    }
}
