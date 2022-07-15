
/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.up.fe.comp.jasmin.Jasmin;
import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsStrings;

import java.util.HashMap;

public class BackendTest {

    @Test
    public void testHelloWorld() {
        var result = TestUtils.backend(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
        TestUtils.noErrors(result.getReports());
        var output = result.run();
        assertEquals("Hello, World!", output.trim());
    }

    @Test
    public void testHelloWorld1() {
        String jasminCode = SpecsIo.getResource("fixtures/public/jasmin/HelloWorld.j");
        var output = TestUtils.runJasmin(jasminCode);
        assertEquals("Hello World!\nHello World Again!\n", SpecsStrings.normalizeFileContents(output));
    }

    @Test
    public void mytest() {
        JasminBackend jb = new Jasmin();
        jb.toJasmin(new OllirResult(SpecsIo.getResource("./fixtures/public/ollir/myclass0.ollir"), new HashMap<String, String>()));
    }

    @Test
    public void mytest2() {
        var ollirResult = new OllirResult(SpecsIo.getResource("./fixtures/public/HelloWorld2.jmm"), new HashMap<String, String>());
        JasminBackend jb = new Jasmin();
        var jasminResult = jb.toJasmin(ollirResult);
        jasminResult.compile();
        jasminResult.run();
    }

    @Test
    public void mytest3() {
        var ollirResult = new OllirResult(SpecsIo.getResource("./fixtures/public/ollir/myclass5.ollir"), new HashMap<String, String>());
        JasminBackend jb = new Jasmin();
        var jasminResult = jb.toJasmin(ollirResult);
        jasminResult.compile();
        jasminResult.run();
    }
}
