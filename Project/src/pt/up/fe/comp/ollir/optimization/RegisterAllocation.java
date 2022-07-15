package pt.up.fe.comp.ollir.optimization;

import org.specs.comp.ollir.ClassUnit;
import org.specs.comp.ollir.OllirErrorException;
import pt.up.fe.comp.ollir.optimization.graph.Graph;

public class RegisterAllocation {
    private final ClassUnit ollirClass;
    private final LivenessAnalysis livenessAnalyzer;

    public RegisterAllocation(ClassUnit ollirClass) {
        this.ollirClass = ollirClass;
        this.livenessAnalyzer = new LivenessAnalysis();
    }

    public void allocate(int n) {
        try {
            ollirClass.checkMethodLabels();
            ollirClass.buildCFGs();
            ollirClass.buildVarTables();
        } catch (OllirErrorException e) {
            e.printStackTrace();
            return;
        }

        for (var method : ollirClass.getMethods()) {
            var livenessAnalysis = livenessAnalyzer.analyse(method);

            Graph graph = new Graph(livenessAnalysis, method);
            graph.applyColoring(n, n == 0);
        }

    }
}
