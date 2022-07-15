package pt.up.fe.comp.ollir;

import org.specs.comp.ollir.ClassUnit;
import pt.up.fe.comp.analysis.SymbolTableMap;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ollir.JmmOptimization;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.ollir.optimization.RegisterAllocation;

import java.util.*;

public class JmmOptimizer implements JmmOptimization {

    @Override
    public JmmSemanticsResult optimize(JmmSemanticsResult semanticsResult) {
        if (notOptimizable(semanticsResult.getConfig())) return semanticsResult;
        var symbolTable = semanticsResult.getSymbolTable();
        var rootNode = semanticsResult.getRootNode();

        List<OptimizerVisitor> visitors = Arrays.asList(
                new ConstantPropagationVisitor(symbolTable),
                new DeadCodeEliminator(symbolTable),
                new ConstantFoldingVisitor(symbolTable));

        do {
            OptimizerVisitor.resetChanges();
            for (var visitor : visitors) {
                visitor.visit(rootNode);
            }
        } while (OptimizerVisitor.optimizationChanges != 0);

        System.out.println(semanticsResult.getRootNode().toTree());
        return semanticsResult;
    }

    @Override
    public OllirResult toOllir(JmmSemanticsResult semanticsResult) {
        var symbolTableMap = (SymbolTableMap) semanticsResult.getSymbolTable();
        var ollirVisitor = new OllirVisitor(symbolTableMap);
        ollirVisitor.visit(semanticsResult.getRootNode());

        String ollirCode = ollirVisitor.getCode();
        System.out.println("\nOllir:\n" + ollirCode);
        return new OllirResult(semanticsResult, ollirCode, Collections.emptyList());
    }

    @Override
    public OllirResult optimize(OllirResult ollirResult) {
        var registerNumber = getNumberOfRegisters(ollirResult.getConfig());

        if(registerNumber == -1) return ollirResult;

        ClassUnit classUnit = ollirResult.getOllirClass();
        RegisterAllocation registerAllocator = new RegisterAllocation(classUnit);

        registerAllocator.allocate(registerNumber);
        return ollirResult;
    }

    private int getNumberOfRegisters(Map<String, String> config) {
        if(!config.containsKey("registerAllocation")) return -1;
        return Integer.parseInt(config.get("registerAllocation"));
    }

    private boolean notOptimizable(Map<String, String> config) {
        if(!config.containsKey("optimize")) return true;
        return !Boolean.parseBoolean(config.get("optimize"));
    }
}
