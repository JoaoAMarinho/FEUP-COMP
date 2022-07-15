package pt.up.fe.comp;

import org.junit.Test;
import pt.up.fe.specs.util.SpecsIo;

public class OptimizationTest {

    @Test
    public void test() {
        var ollirResult = TestUtils.optimize(SpecsIo.getResource("pt/up/fe/comp/HelloWorld.jmm"));
        TestUtils.noErrors(ollirResult);
        //System.out.println("\n---------\n");
    }
}
