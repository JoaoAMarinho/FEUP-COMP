/**
 * Copyright 2022 SPeCS.
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

package pt.up.fe.comp;

import org.junit.Test;

public class GrammarTest {

    private static final String IMPORT = "ImportDeclaration";
    private static final String MAIN_METHOD = "MethodDeclaration";
    private static final String INSTANCE_METHOD = "MethodDeclaration";
    private static final String STATEMENT = "Statement";
    private static final String EXPRESSION = "Expression";

    private static void noErrors(String code, String grammarRule) {
        if (grammarRule.isEmpty()) {
            System.out.println(
                    "Name of grammar rule is empty, please define it in the static field at the beginning of the class '"
                            + GrammarTest.class.getName() + "' if test is to be executed");
            return;
        }

        //var parserResult = TestUtils.parse(code, grammarRule);
        //TestUtils.noErrors(parserResult.getReports());

        //System.out.println("AST:\n\n" + parserResult.getRootNode().toTree());

        var analysisResult = TestUtils.analyse(code);
        TestUtils.noErrors(analysisResult.getReports());
        System.out.println(analysisResult.getRootNode().toTree());
    }

    private static void noErrors(String code) {
        noErrors(code, "Start");
    }

    @Test
    public void testImportSingle() {
        noErrors("import bar;", IMPORT);
    }

    @Test
    public void testImportMulti() {
        noErrors("import bar.foo.a;", IMPORT);
    }

    @Test
    public void testTwoImport() {
        noErrors("import bar.a; import bar.foo; class foo {}");
    }

    @Test
    public void testClass() {
        noErrors("class Foo extends Bar {}");
    }

    @Test
    public void testVarDecls() {
        noErrors("class Foo {int a; int[] b; int c; boolean d; Bar e;}");
    }

    @Test
    public void testVarDeclString() {
        noErrors("String aString;", "VarDeclaration");
    }

    @Test
    public void testMainMethodEmpty() {
        noErrors("public static void main(String[] args) {}", MAIN_METHOD);
    }

    @Test
    public void testInstanceMethodEmpty() {
        noErrors("public int foo(int anInt, int[] anArray, boolean aBool, String aString) {return a;}",
                INSTANCE_METHOD);
    }

    @Test
    public void testStmtScope() {
        noErrors("{a; b; c;}", STATEMENT);
    }

    @Test
    public void testStmtEmptyScope() {
        noErrors("{}", STATEMENT);
    }

    @Test
    public void testStmtIfElse() {
        noErrors("if(a){ifStmt1;ifStmt2;}else{elseStmt1;elseStmt2;}", STATEMENT);
    }

    @Test
    public void testIfElse2() {
        noErrors("if(a)ifStmt1;else{elseStmt1;elseStmt2;}", STATEMENT);
    }

    @Test
    public void testStmtIfElseWithoutBrackets() {
        noErrors("if(a)ifStmt;else elseStmt;", STATEMENT);
    }

    @Test
    public void testStmtWhile() {
        noErrors("while(a){whileStmt1;whileStmt2;}", STATEMENT);
    }

    @Test
    public void testStmtWhileWithoutBrackets() {
        noErrors("while(a)whileStmt1;", STATEMENT);
    }

    @Test
    public void testStmtAssign() {
        noErrors("a=b;", STATEMENT);
    }

    @Test
    public void testStmtArrayAssign() {
        noErrors("anArray[a]=b;", STATEMENT);
    }

    @Test
    public void testExprTrue() {
        noErrors("true", EXPRESSION);
    }

    @Test
    public void testExprFalse() {
        noErrors("false", EXPRESSION);
    }

    @Test
    public void testExprThis() {
        noErrors("this", EXPRESSION);
    }

    @Test
    public void testExprSum() {
        noErrors("-1 + 2 + 3", EXPRESSION);
    }

    @Test
    public void testExprId() {
        noErrors("a", EXPRESSION);
    }

    @Test
    public void testExprIntLiteral() {
        noErrors("10", EXPRESSION);
    }

    @Test
    public void testExprParen() {
        noErrors("(10)", EXPRESSION);
    }

    @Test
    public void testArrayNewExp() {
        noErrors("new int[10+2]", EXPRESSION);
    }

    @Test
    public void testObjNewExp() {
        noErrors("new Obj()", EXPRESSION);
    }

    @Test
    public void testExprMemberCall() {
        noErrors("foo.bar(10, a, true)", EXPRESSION);
    }

    @Test
    public void testExprMemberCallChain() {
        noErrors("callee.level1().level2(false, 10).level3(true)", EXPRESSION);
    }

    @Test
    public void testExprLength() {
        noErrors("a.length", EXPRESSION);
    }

    @Test
    public void testExprLengthChain() {
        noErrors("a.length.length", EXPRESSION);
    }

    @Test
    public void testArrayAccess() {
        noErrors("a[10]", EXPRESSION);
    }

    @Test
    public void testArrayAccessChain() {
        noErrors("a[10][20]", EXPRESSION);
    }

    @Test
    public void testParenArrayChain() {
        noErrors("(a)[10]", EXPRESSION);
    }

    @Test
    public void testCallArrayAccessLengthChain() {
        noErrors("callee.foo()[10].length", EXPRESSION);
    }

    @Test
    public void testExprNot() {
        noErrors("!true", EXPRESSION);
    }

    @Test
    public void testExprNewArray() {
        noErrors("new int[!a]", EXPRESSION);
    }

    @Test
    public void testExprNewClass() {
        noErrors("new Foo()", EXPRESSION);
    }

    @Test
    public void testExprMult() {
        noErrors("2 * 3", EXPRESSION);
    }

    @Test
    public void testExprDiv() {
        noErrors("2 / 3", EXPRESSION);
    }

    @Test
    public void testExprMultChain() {
        noErrors("1 * 2 / 3 * 4", EXPRESSION);
    }

    @Test
    public void testExprAdd() {
        noErrors("2 + 3", EXPRESSION);
    }

    @Test
    public void testExprSub() {
        noErrors("2 - 3", EXPRESSION);
    }

    @Test
    public void testExprAddChain() {
        noErrors("1 + 2 - 3 + 4", EXPRESSION);
    }

    @Test
    public void testExprRelational() {
        noErrors("1 < 2", EXPRESSION);
    }

    @Test
    public void testExprRelationalChain() {
        noErrors("1 < 2 < 3 < 4", EXPRESSION);
    }

    @Test
    public void testExprLogical() {
        noErrors("1 && 2", EXPRESSION);
    }

    @Test
    public void testExprLogicalChain() {
        noErrors("1 && 2 && 3 && 4", EXPRESSION);
    }

    @Test
    public void testExprChain() {
        noErrors("1 && 2 < 3 + 4 - 5 * 6 / 7", EXPRESSION);
    }

    @Test
    public void testV1() {
        noErrors("import matematica.square.pp.qq_coisa; import A;\n"+
                "class mat extends qq_coisa {\n"+
                "int v;\n"+
                "public static void main(String[] feia){\n"+
                "\tint x;\n"+
                "\t}\n\n"+
                "public int a () {\n"+
                "\tv.loop();\n"+
                "\treturn v.loop();    }\n}");
    }

    @Test
    public void testV2() {
        noErrors("class mat {\n" +
                "    public int getSomething () {\n" +
                "        int a;\n" +
                "        String x;\n" +
                "        new A().foo();\n" +
                "        return a;\n" +
                "    }\n" +
                "}");
    }

    @Test
    public void testV3() {
        noErrors("class mat {\n" +
                "    public String getSomething (boolean x, int[] arr) {\n" +
                "        int a;\n" +
                "        String x;\n" +
                "        new A().foo();\n" +
                "        return algo;\n" +
                "    }\n" +
                "}");
    }

    @Test
    public void testV4() {
        noErrors("import ómds;\n"+
                "import bea;\n"+
                "class mat {\n"+
                "\tpublic String getSomething (int[] a) {\n"+
                "\t\tString algo;\n"+
                "\t\tint[] naopode;\n"+
                "\t\tint[] l;\n"+
                "\t\tl + naopode;\n"+
                "\t\treturn algo;\n}\n}"
        );
    }

    @Test
    public void testV5() {
        noErrors("import ox;\n"+
                "class mat extends ox{\n"+
                "\tpublic static void main(String[] args) {\n"+
                "\t\tint a;\n"+
                "\t\tboolean x;\n"+
                "\t\tif (x) {\n"+
                "\t\t\ta = 2;\n"+
                "\t\t} else {\n"+
                "\t\t\ta = 4;\n"+
                "\t}\n"+
                "\tthis.foo();\n"+
                "}\n}"
        );
    }

    @Test
    public void testV6() {
        noErrors("import obj.oi;import ol;\n " +
                "class mat extends ol {\n" +
                "    int b;\n" +
                "    String h;\n" +
                "    intoi quero_arrays;\n" +
                "    \n" +
                "    public static void main(String[] args) {\n" +
                "        int a;\n" +
                "        String x;\n" +
                "        new A().foo(1+1, args);\n" +
                "    }\n" +
                "\n" +
                "    public obj nada() {return new obj();}\n"+
                "    public boolean function (int[] um_arrrayy, boolean c) {\n" +
                "        String x;\n" +
                "        return this.nada().foo();\n" +
                "    }\n" +
                "}");
    }

}
