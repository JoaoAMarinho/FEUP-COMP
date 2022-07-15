package pt.up.fe.comp;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import pt.up.fe.comp.analysis.JmmAnalyser;
import pt.up.fe.comp.jasmin.Jasmin;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.comp.jmm.parser.JmmParserResult;
import pt.up.fe.comp.ollir.JmmOptimizer;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.SpecsSystem;

public class Launcher {

    public static void main(String[] args) {
        SpecsSystem.programStandardInit();

        SpecsLogs.info("Executing with args: " + Arrays.toString(args));

        // read the input code
        if (args.length < 1) {
            throw new RuntimeException("Expected more than one argument, a path to an existing input file.");
        }

        // Create config
        Map<String, String> config = new HashMap<>();

        config.put("inputFile", "");
        config.put("optimize", "false");
        config.put("registerAllocation", "-1");
        config.put("debug", "false");

        for (String flag : args) {
            var options = flag.split("=");
            switch (options[0]) {
                case "-r" -> config.put("registerAllocation", options[1]);
                case "-o" -> config.put("optimize", "true");
                case "-d" -> config.put("debug", "true");
                case "-i" -> config.put("inputFile", options[1]);
                default -> throw new IllegalArgumentException();
            }
        }

        File inputFile = new File(config.get("inputFile"));
        if (!inputFile.isFile()) {
            throw new RuntimeException("Expected a path to an existing input file, got '" + args[0] + "'.");
        }
        String input = SpecsIo.read(inputFile);

        // Instantiate JmmParser
        SimpleParser parser = new SimpleParser();

        // Parse stage
        JmmParserResult parserResult = parser.parse(input, config);

        // Check if there are parsing errors
        TestUtils.noErrors(parserResult.getReports());

        // Instantiate JmmAnalysis
        JmmAnalyser analyser = new JmmAnalyser();

        // Analysis stage
        JmmSemanticsResult analysisResult = analyser.semanticAnalysis(parserResult);

        // Check if there are parsing errors
        TestUtils.noErrors(analysisResult.getReports());

        // Instantiate JmmOptimization
        JmmOptimizer optimizer = new JmmOptimizer();

        // Optimization stage
        JmmSemanticsResult optimizedAnalysisResult = optimizer.optimize(analysisResult);

        // Check if there are conversion errors
        OllirResult ollirResult = optimizer.toOllir(optimizedAnalysisResult);
        TestUtils.noErrors(ollirResult.getReports());

        OllirResult optimizedOllirResult = optimizer.optimize(ollirResult);


        // Instantiate Jasmin Backend
        Jasmin jasminBackend = new Jasmin();

        // Check if there are conversion errors
        TestUtils.noErrors(jasminBackend.toJasmin(optimizedOllirResult));
    }

}
