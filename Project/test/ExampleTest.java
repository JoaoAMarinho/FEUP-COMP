import org.junit.Test;

import pt.up.fe.comp.TestUtils;

public class ExampleTest {

    @Test
    public void testExpression() {
        var r = TestUtils.parse("import io;", "ImportDeclaration");
        System.out.println(r.getReports());
        TestUtils.noErrors(r.getReports());
        // var parserResult = TestUtils.parse("2+3\n10+20\n");
        // parserResult.getReports().get(0).getException().get().printStackTrace();
        // // System.out.println();
        // var analysisResult = TestUtils.analyse(parserResult);
    }
}
