package pt.up.fe.comp.jasmin;

import org.specs.comp.ollir.*;
import org.specs.comp.ollir.Type;
import pt.up.fe.comp.jmm.jasmin.JasminBackend;
import pt.up.fe.comp.jmm.jasmin.JasminResult;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

import java.io.File;
import java.util.*;

import static org.specs.comp.ollir.CallType.invokestatic;

public class Jasmin implements JasminBackend {
    private ClassUnit ollirClass;
    private boolean hasConstructor;
    private boolean hasReturnInstruction;
    private boolean insideAnAssignment;
    private String superClass = "java/lang/Object";
    private HashMap<String, String> imports;
    private HashMap<String, Descriptor> currentVarTable;
    private int labelCounter = 0;
    private StackManager stackManager;

    public Jasmin() {
    }

    @Override
    public JasminResult toJasmin(OllirResult ollirResult) {
        this.hasConstructor = false;
        this.ollirClass = ollirResult.getOllirClass();
        File file = new File("./test/fixtures/public/jasmin/"
                + this.ollirClass.getClassName() + ".j");
        StringBuilder jasminCode = new StringBuilder();

        this.parseImports();

        jasminCode.append(this.parseHeader());

        jasminCode.append(this.parseFields());

        jasminCode.append(this.parseMethods());

        if (!this.hasConstructor)
            jasminCode.append(this.getConstructorCode());

        SpecsIo.write(file, jasminCode.toString());

        return new JasminResult(ollirResult, jasminCode.toString(), Collections.emptyList());
    }

    private void parseImports() {
        this.imports = new HashMap<>();
        for (String importString : this.ollirClass.getImports()) {
            var splittedImport = importString.split("\\.");
            String lastName;
            if (splittedImport.length == 0) {
                lastName = importString;
            } else {
                lastName = splittedImport[splittedImport.length - 1];
            }

            this.imports.put(lastName, String.join("/", splittedImport));
        }
    }

    private String parseHeader() {
        StringBuilder jasminCode = new StringBuilder(".class ");

        AccessModifiers aM = this.ollirClass.getClassAccessModifier();
        if (aM != AccessModifiers.DEFAULT) {
            jasminCode.append(aM.toString().toLowerCase()).append(" ");
        }

        jasminCode.append(this.ollirClass.getClassName())
                .append('\n').append(".super ");

        if (this.ollirClass.getSuperClass() != null)
            this.superClass = this.imports.getOrDefault(this.ollirClass.getSuperClass(), this.ollirClass.getSuperClass());

        jasminCode.append(this.superClass);
        jasminCode.append("\n\n");

        return jasminCode.toString();
    }

    private String parseFields() {
        StringBuilder jasminCode = new StringBuilder();

        for (Field field : this.ollirClass.getFields()) {
            jasminCode.append(".field ");

            AccessModifiers aM = field.getFieldAccessModifier();
            if (aM != AccessModifiers.DEFAULT) {
                jasminCode.append(aM.toString().toLowerCase()).append(" ");
            }

            if (field.isStaticField()) {
                jasminCode.append("static ");
            }

            if (field.isFinalField()) {
                jasminCode.append("final ");
            }

            jasminCode.append(field.getFieldName()).append(" ");

            jasminCode.append(this.getType(field.getFieldType()));

            jasminCode.append('\n');
        }

        return jasminCode.toString();
    }

    private String parseMethods() {
        StringBuilder jasminCode = new StringBuilder();

        for (Method method : this.ollirClass.getMethods()) {
            jasminCode.append(parseMethod(method));
        }
        jasminCode.append('\n');

        return jasminCode.toString();

    }

    private String getConstructorCode() {
        return SpecsIo.getResource("jasminTemplate/constructor.template")
                .replace("${SUPER_NAME}", this.superClass) + '\n';
    }

    private String parseMethod(Method method) {
        StringBuilder jasminCode = new StringBuilder(".method ");
        this.hasReturnInstruction = false;
        this.stackManager = new StackManager();

        AccessModifiers aM = method.getMethodAccessModifier();
        if (aM != AccessModifiers.DEFAULT) {
            jasminCode.append(aM.toString().toLowerCase()).append(' ');
        }
        if (method.isStaticMethod()) {
            jasminCode.append("static ");
        }
        if (method.isFinalMethod()) {
            jasminCode.append("final ");
        }

        if (method.isConstructMethod()) {
            this.hasConstructor = true;
            jasminCode.append("<init>(");
        } else {
            jasminCode.append(method.getMethodName()).append('(');
        }

        for (Element param : method.getParams()) {
            jasminCode.append(this.getType(param.getType()));
        }
        jasminCode.append(')')
                .append(this.getType(method.getReturnType()))
                .append('\n');

        method.buildVarTable();
        this.currentVarTable = method.getVarTable();

        jasminCode.append("\t.limit stack ${STACK_COUNTER}\n\t.limit locals ")
                .append(getLocals()).append("\n");

        //Parse Method
        for (Instruction inst : method.getInstructions()) {
            jasminCode.append(getLabels(method.getLabels(inst)));
            jasminCode.append(getCode(inst));
        }

        if (!this.hasReturnInstruction) {
            jasminCode.append("\treturn\n");
        }
        jasminCode.append(".end method\n\n");

        return jasminCode.toString().replace("${STACK_COUNTER}", Integer.toString(this.stackManager.getStackLimit()));
    }

    private String getLabels(List<String> labels) {
        StringBuilder jasminCode = new StringBuilder();

        for (String label : labels) {
            jasminCode.append("\t").append(label).append(":\n");
        }

        return jasminCode.toString();
    }

    private String getCode(Instruction instruction) {

        if (instruction instanceof CallInstruction) {
            return getCode((CallInstruction) instruction);
        }

        if (instruction instanceof AssignInstruction) {
            return getCode((AssignInstruction) instruction);
        }

        if (instruction instanceof GotoInstruction) {
            return getCode((GotoInstruction) instruction);
        }

        if (instruction instanceof ReturnInstruction) {
            this.hasReturnInstruction = true;
            return getCode((ReturnInstruction) instruction);
        }

        if (instruction instanceof SingleOpInstruction) {
            return getCode((SingleOpInstruction) instruction);
        }

        if (instruction instanceof PutFieldInstruction) {
            return getCode((PutFieldInstruction) instruction);
        }

        if (instruction instanceof GetFieldInstruction) {
            return getCode((GetFieldInstruction) instruction);
        }

        if (instruction instanceof BinaryOpInstruction) {
            return getCode((BinaryOpInstruction) instruction);
        }

        if (instruction instanceof CondBranchInstruction) {
            return getCode((CondBranchInstruction) instruction);
        }

        if (instruction instanceof UnaryOpInstruction) {
            return getCode((UnaryOpInstruction) instruction);
        }

        throw new NotImplementedException(instruction);
    }

    private String getCode(UnaryOpInstruction instruction) {
        StringBuilder jasminCode = new StringBuilder();
        if (instruction.getOperation().getOpType() == OperationType.NOTB) {
            Operand op = (Operand) instruction.getOperand();
            jasminCode.append(loadElement(instruction.getOperand()))
                    .append("\tifne ").append("Then").append(this.labelCounter).append('\n')
                    .append("\ticonst_1\n").append("\tgoto EndIf").append(this.labelCounter).append('\n')
                    .append("\tThen").append(this.labelCounter).append(":\n")
                    .append("\ticonst_0\n")
                    .append("\tEndIf").append(this.labelCounter).append(":\n");
        }

        return jasminCode.toString();
    }

    private String getCode(CondBranchInstruction instruction) {
        StringBuilder jasminCode = new StringBuilder();

        Instruction condition = instruction.getCondition();
        if (condition instanceof OpInstruction) {
            OpInstruction opInst = (OpInstruction) condition;
            Element leftElem = opInst.getOperands().get(0);
            Element rightElem = opInst.getOperands().size() > 1 ? opInst.getOperands().get(1) : null;

            switch (opInst.getOperation().getOpType()) {
                case EQ -> {
                    jasminCode.append(prepareComparison(leftElem, rightElem))
                            .append("\tifeq ").append(instruction.getLabel()).append('\n');
                    this.stackManager.decrementStackCounter();
                }
                case ANDB -> {
                    jasminCode.append(loadElement(leftElem))
                            .append("\tifeq FalseAND").append(this.labelCounter).append('\n')
                            .append(loadElement(rightElem))
                            .append("\tifeq FalseAND").append(this.labelCounter).append('\n')
                            .append("\tgoto ").append(instruction.getLabel()).append('\n')
                            .append("\tFalseAND").append(this.labelCounter++).append(":\n");
                }
                case ORB -> {
                    jasminCode.append(loadElement(leftElem))
                            .append("\tifne ").append(instruction.getLabel()).append('\n')
                            .append(loadElement(rightElem))
                            .append("\tifne ").append(instruction.getLabel()).append('\n');
                }
                case NOTB -> {
                    jasminCode.append(loadElement(leftElem))
                            .append("\tifeq ").append(instruction.getLabel()).append('\n');
                }
                case NEQ -> {
                    jasminCode.append(prepareComparison(leftElem, rightElem))
                            .append("\tifne ").append(instruction.getLabel()).append('\n');
                    this.stackManager.decrementStackCounter();
                }
                case LTH, LTE, GTH, GTE -> {
                    jasminCode.append(prepareComparison(leftElem, rightElem))
                            .append('\t').append(getComparison(opInst.getOperation())).append(' ')
                            .append(instruction.getLabel()).append('\n');
                    this.stackManager.decrementStackCounter();
                }
                default -> throw new NotImplementedException(opInst.getOperation().getOpType());
            }
        } else if (instruction instanceof SingleOpCondInstruction) {
            SingleOpCondInstruction singleOp = (SingleOpCondInstruction) instruction;
            jasminCode.append(loadElement(singleOp.getOperands().get(0)))
                    .append("\tifne ").append(instruction.getLabel()).append("\n");
        } else {
            throw new NotImplementedException(instruction);
        }

        this.stackManager.decrementStackCounter();

        return jasminCode.toString();
    }

    private String getIncrementation(BinaryOpInstruction instruction, Operand toIncrement) {
        String toIncrementName = toIncrement.getName();
        Element leftElem = instruction.getLeftOperand();
        Element rightElem = instruction.getRightOperand();
        Integer literal = null;

        if (!leftElem.isLiteral() && rightElem.isLiteral())
            if (((Operand) leftElem).getName().equals(toIncrementName))
                literal = Integer.parseInt(((LiteralElement) instruction.getRightOperand()).getLiteral());

        if (leftElem.isLiteral() && !rightElem.isLiteral())
            if (((Operand) rightElem).getName().equals(toIncrementName))
                literal = Integer.parseInt(((LiteralElement) instruction.getLeftOperand()).getLiteral());

        //TODO: RANGE??????
        if (literal != null && -128 < literal && literal < 513) {
            return "\tiinc " +
                    this.currentVarTable.get(toIncrementName).getVirtualReg()
                    + ' ' + literal + '\n';
        }

        return "";

    }

    private String getCode(BinaryOpInstruction instruction) {
        StringBuilder jasminCode = new StringBuilder();

        jasminCode.append(loadElement(instruction.getLeftOperand()))
                .append(loadElement(instruction.getRightOperand()));
        Operation op = instruction.getOperation();
        switch (op.getOpType()) {
            // Pops 2 values and pushes the result
            case ADD -> jasminCode.append("\tiadd\n");
            case SUB -> jasminCode.append("\tisub\n");
            case MUL -> jasminCode.append("\timul\n");
            case DIV -> jasminCode.append("\tidiv\n");
            // Pops two values
            case LTH, LTE, GTH, GTE, EQ, NEQ -> {
                jasminCode.append("\tisub\n\t")
                        .append(getComparison(op))
                        .append(generateComparisonLabels());
                this.stackManager.decrementStackCounter();
            } case ANDB -> {
                jasminCode.append("\tiadd\n")
                        .append("\ticonst_2\n")
                        .append("\tisub\n")
                        .append("\tiflt ComparisonThen").append(this.labelCounter).append('\n')
                        .append("\ticonst_1\n")
                        .append("\tgoto ComparisonEndIf").append(this.labelCounter).append("\n")
                        .append("\tComparisonThen").append(this.labelCounter).append(":\n")
                        .append("\ticonst_0\n")
                        .append("\tComparisonEndIf").append(this.labelCounter++).append(":\n");
            }
            default -> throw new NotImplementedException(op.getOpType());
        }

        this.stackManager.decrementStackCounter();

        return jasminCode.toString();
    }

    private String getCode(GetFieldInstruction instruction) {
        StringBuilder jasminCode = new StringBuilder();

        Operand firstElem = (Operand) instruction.getFirstOperand();
        Operand secondElem = (Operand) instruction.getSecondOperand();

        jasminCode.append(loadElement(firstElem))
                .append("\tgetfield ")
                .append(this.imports.getOrDefault(this.ollirClass.getClassName(), this.ollirClass.getClassName()))
                .append("/")
                .append(secondElem.getName()).append(" ")
                .append(getType(secondElem.getType())).append('\n');

        return jasminCode.toString();
    }

    private String getCode(PutFieldInstruction instruction) {
        StringBuilder jasminCode = new StringBuilder();

        Operand firstOp = (Operand) instruction.getFirstOperand();
        Operand secondOp = (Operand) instruction.getSecondOperand();
        Element thirdOp = instruction.getThirdOperand();

        jasminCode.append(loadElement(firstOp))
                .append(loadElement(thirdOp))
                .append("\tputfield ")
                .append(((firstOp.getName().equals("this")) ? this.ollirClass.getClassName() : firstOp.getName()))
                .append("/").append(secondOp.getName()).append(" ")
                .append(getType(secondOp.getType())).append('\n');
        this.stackManager.decrementStackCounter(2);

        return jasminCode.toString();
    }

    private String getCode(AssignInstruction aI) {
        StringBuilder jasminCode = new StringBuilder();

        Operand o = (Operand) aI.getDest();
        Instruction rhs = aI.getRhs();

        // var x = x + const || var x = const + x
        if (rhs.getInstType() == InstructionType.BINARYOPER) {
            BinaryOpInstruction binaryOp = ((BinaryOpInstruction) rhs);
            if (binaryOp.getOperation().getOpType() == OperationType.ADD) {
                String temp = getIncrementation(binaryOp, o);
                if (!temp.isEmpty()) {
                    return temp;
                }
            }
        }

        int reg = this.currentVarTable.get(o.getName()).getVirtualReg();
        Descriptor descriptor = this.currentVarTable.get(o.getName());

        if (descriptor.getVarType().getTypeOfElement() == ElementType.ARRAYREF
                && o.getType().getTypeOfElement() != ElementType.ARRAYREF) {
            ArrayOperand arrayOp = (ArrayOperand) o;
            Element index = arrayOp.getIndexOperands().get(0);

            jasminCode.append(loadDescriptor(descriptor))
                    .append(loadElement(index));
        }

        this.insideAnAssignment = true;
        String rhsCode = getCode(rhs);
        this.insideAnAssignment = false;

        if (o.getType() instanceof ArrayType) {
            ArrayType aT = (ArrayType)o.getType();
            if (o instanceof ArrayOperand) {
                ArrayOperand aO = (ArrayOperand)o;
                jasminCode.append(loadElement(aO))
                        .append(loadElement(aO.getIndexOperands().get(0)))
                        .append(rhsCode)
                        .append(
                            (aT.getArrayType() == ElementType.INT32 || aT.getArrayType() == ElementType.BOOLEAN)
                            ?
                            "\tiastore\n"
                            :
                            "\taastore\n"
                        );
                this.stackManager.decrementStackCounter(3);

                return jasminCode.toString();
            }
        }

        jasminCode.append(rhsCode);

        if (o.getType().getTypeOfElement() == ElementType.INT32 || o.getType().getTypeOfElement() == ElementType.BOOLEAN)
            jasminCode.append("\tistore");
        else {
            jasminCode.append("\tastore");
        }

        this.stackManager.decrementStackCounter();

        jasminCode.append((reg <= 3) ? "_" : " ").append(reg).append("\n");

        return jasminCode.toString();
    }

    private String getCode(SingleOpInstruction instruction) {
        return loadElement(instruction.getSingleOperand());
    }

    private String getCode(GotoInstruction instruction) {
        return "\tgoto " + instruction.getLabel() + "\n";
    }

    private String getCode(ReturnInstruction i) {
        if (!i.hasReturnValue())
            return "\treturn\n";

        ElementType returnType = i.getOperand().getType().getTypeOfElement();

        String jasminCode = loadElement(i.getOperand()) +
                "\t" + ((returnType == ElementType.INT32 || returnType == ElementType.BOOLEAN) ? "i" : "a") +
                "return\n";

        this.stackManager.decrementStackCounter();

        return jasminCode;
    }

    private String parseNewCall(CallInstruction instruction) {
        StringBuilder jasminCode = new StringBuilder();

        if (instruction.getReturnType().getTypeOfElement() == ElementType.OBJECTREF) {
            jasminCode.append("\tnew ")
                    .append(((Operand) instruction.getFirstArg()).getName()).append("\n");

            this.stackManager.incrementStackCounter();

            return jasminCode.toString();
        }
        if (instruction.getReturnType().getTypeOfElement() == ElementType.ARRAYREF) {

            //Number of elements
            for (Element e : instruction.getListOfOperands()) {
                jasminCode.append(loadElement(e));
            }

            jasminCode.append("\tnewarray ");

            if (((ArrayType) instruction.getReturnType()).getArrayType() == ElementType.INT32)
                jasminCode.append("int\n");
            else
                throw new NotImplementedException(instruction);

        } else
            throw new NotImplementedException(instruction);
        this.stackManager.decrementStackCounter(instruction.getListOfOperands().size() - 1);

        return jasminCode.toString();
    }

    private String parseInvokeSpecialCall(CallInstruction instruction) {

        StringBuilder jasminCode = new StringBuilder(loadElement(instruction.getFirstArg()));

        for (Element e : instruction.getListOfOperands()) {
            jasminCode.append(loadElement(e));
        }

        jasminCode.append("\tinvokespecial ")
                .append(
                        (instruction.getFirstArg().getType().getTypeOfElement() == ElementType.THIS) ?
                                this.imports.getOrDefault(this.ollirClass.getSuperClass(), "java/lang/Object")
                                :
                                this.imports.getOrDefault(
                                    ((ClassType)instruction.getFirstArg().getType()).getName(),
                                    ((ClassType)instruction.getFirstArg().getType()).getName())
                )
                .append("/<init>(");

        for (Element e : instruction.getListOfOperands())
            jasminCode.append(getType(e.getType()));

        jasminCode.append(")").append(getType(instruction.getReturnType())).append("\n");

        return jasminCode.toString();
    }
    private String parseInvokeVirtualCall(CallInstruction instruction) {
        StringBuilder jasminCode = new StringBuilder(loadElement(instruction.getFirstArg()));

        for (Element e : instruction.getListOfOperands())
            jasminCode.append(loadElement(e));

        jasminCode.append("\tinvokevirtual ")
                .append(instruction.getFirstArg().getType().getTypeOfElement() == ElementType.THIS ?
                        this.ollirClass.getClassName()
                        :
                        this.imports.getOrDefault(
                                ((ClassType) instruction.getFirstArg().getType()).getName(),
                                ((ClassType) instruction.getFirstArg().getType()).getName()
                        )
                )
                .append("/").append(((LiteralElement) instruction.getSecondArg()).getLiteral().replace("\"", ""))
                .append("(");

        for (Element e : instruction.getListOfOperands())
            jasminCode.append(getType(e.getType()));

        jasminCode.append(")").append(getType(instruction.getReturnType())).append("\n");

        return jasminCode.toString();
    }
    private String parseInvokeStaticCall(CallInstruction instruction) {
        StringBuilder jasminCode = new StringBuilder();

        for (Element e : instruction.getListOfOperands())
            jasminCode.append(loadElement(e));

        String temp = ((Operand) instruction.getFirstArg()).getName();

        jasminCode.append("\tinvokestatic ")
                .append((temp.equals("this")) ? this.ollirClass.getClassName() : temp)
                .append("/").append(((LiteralElement) instruction.getSecondArg()).getLiteral().replace("\"", ""))
                .append("(");

        for (Element e : instruction.getListOfOperands())
            jasminCode.append(getType(e.getType()));

        jasminCode.append(")").append(getType(instruction.getReturnType())).append("\n");

        return jasminCode.toString();
    }

    private String getCode(CallInstruction instruction) {
        return switch (instruction.getInvocationType()) {
            case NEW -> parseNewCall(instruction);
            case invokespecial, invokevirtual, invokestatic -> getInvokeCode(instruction);
            case ldc -> loadElement(instruction.getFirstArg());
            case arraylength -> loadElement(instruction.getFirstArg()) + "\tarraylength\n";
            default -> throw new NotImplementedException(instruction);
        };
    }

    private String getInvokeCode(CallInstruction instruction) {
        boolean hasReturnValue = instruction.getReturnType().getTypeOfElement() != ElementType.VOID;

        String jasminCode = switch (instruction.getInvocationType()) {
            case invokespecial -> parseInvokeSpecialCall(instruction);
            case invokevirtual -> parseInvokeVirtualCall(instruction);
            case invokestatic -> parseInvokeStaticCall(instruction);
            default -> throw new NotImplementedException(instruction.getInvocationType());
        };

        this.invokeInfluenceOnStack(instruction);

        if (hasReturnValue && !this.insideAnAssignment) {
            jasminCode += "\tpop\n";
        }

        return jasminCode;
    }

    private String loadDescriptor(Descriptor descriptor) {
        this.stackManager.incrementStackCounter();
        ElementType elementType = descriptor.getVarType().getTypeOfElement();
        if (elementType == ElementType.THIS)
            return "\taload_0\n";

        int reg = descriptor.getVirtualReg();
        return "\t" + ((elementType == ElementType.INT32 || elementType == ElementType.BOOLEAN) ? "i" : "a") +
                "load" + ((reg <= 3) ? "_" : " ") + reg + "\n";
    }

    private String loadLiteral(LiteralElement element) {
        this.stackManager.incrementStackCounter();
        String jasminCode = "\t";
        int literal;

        try {
            literal = Integer.parseInt(element.getLiteral());
        } catch (NumberFormatException e) {
            return jasminCode + "ldc " + element.getLiteral() + '\n';
        }

        ElementType elementType = element.getType().getTypeOfElement();
        if (elementType == ElementType.INT32 || elementType == ElementType.BOOLEAN) {
            if (literal <= 5 && literal >= -1)
                jasminCode += "iconst_";
            else if (-32768 > literal || literal > 32767 ) // -2^15 || 2^15 - 1
                jasminCode += "ldc ";
            else if (-128 > literal || literal > 127) // -2^7 || 2^7-1
                jasminCode += "sipush "; // push a short onto the stack as an integer value
            else
                jasminCode += "bipush "; // push a byte onto the stack as an integer value
        } else {
            jasminCode += "ldc ";
        }

        return jasminCode + (literal == -1 ? "m1\n" : (literal + "\n"));
    }

    private String loadElement(Element e) {
        if (e.isLiteral())
            return loadLiteral((LiteralElement) e);

        Descriptor d = this.currentVarTable.get(((Operand) e).getName());
        if (d == null)
            throw new NotImplementedException(((Operand) e).getName());

        try {
            if (e.getType().getTypeOfElement() != ElementType.ARRAYREF
                    && d.getVarType().getTypeOfElement() == ElementType.ARRAYREF) {
                ArrayOperand arrayOp = (ArrayOperand) e;
                Element index = arrayOp.getIndexOperands().get(0);
                // arrayref[index] -> value
                this.stackManager.decrementStackCounter();
                return loadDescriptor(d) + loadElement(index) + "\tiaload\n";
            }
        } catch (NullPointerException | ClassCastException except) {
            System.out.println(((Operand) e).getName());
            System.out.println(d.getVirtualReg() + " " + d.getVarType());
        }

        return loadDescriptor(d);
    }

    private String getType(Type type) {
        ElementType elementType = type.getTypeOfElement();

        return switch (elementType) {
            case INT32 -> "I";
            case BOOLEAN -> "Z";
            case STRING -> "Ljava/lang/String;";
            case VOID -> "V";
            case ARRAYREF -> "[" + this.getType(new Type(((ArrayType) type).getArrayType()));
            case OBJECTREF ->
                    "L" + this.imports.getOrDefault(((ClassType) type).getName(), ((ClassType) type).getName()) + ";";
            default -> throw new NotImplementedException(type);
        };
    }

    private String getComparison(Operation operation) {
        return switch (operation.getOpType()) {
            case GTE -> "ifge";
            case GTH -> "ifgt";
            case LTE -> "ifle";
            case LTH -> "iflt";
            case EQ -> "ifeq";
            case NOTB, NEQ -> "ifne";
            default -> throw new NotImplementedException(operation.getOpType());
        };
    }

    private String generateComparisonLabels() {
        this.stackManager.incrementStackCounter();
        return " ComparisonThen" + this.labelCounter + "\n" +
                "\ticonst_0\n" +
                "\tgoto ComparisonEndIf" + this.labelCounter + '\n' +
                "\tComparisonThen" + this.labelCounter + ":\n" +
                "\ticonst_1\n" +
                "\tComparisonEndIf" + this.labelCounter++ + ":\n";
    }

    private int getLocals() {
        int max = 0;

        for (Descriptor descriptor : this.currentVarTable.values()) {
            int tempVirtualReg = descriptor.getVirtualReg();
            if (tempVirtualReg > max) max = tempVirtualReg;
        }

        return max + 1;
    }

    private String prepareComparison(Element first, Element second) {
        return loadElement(first) + loadElement(second) + "\tisub\n";
    }

    private void invokeInfluenceOnStack(CallInstruction instruction) {
        boolean hasLoadedOperands = instruction.getListOfOperands().size() != 0;
        boolean hasReturnValue = instruction.getReturnType().getTypeOfElement() != ElementType.VOID;
        int decrementValue = 0;

        if (hasLoadedOperands && hasReturnValue) decrementValue = instruction.getListOfOperands().size();
        else if (hasLoadedOperands && !hasReturnValue) decrementValue = instruction.getListOfOperands().size() + 1;
        else if (!hasLoadedOperands && !hasReturnValue) decrementValue = 1;

        if (instruction.getInvocationType() == invokestatic) {
            decrementValue -= 1;
        }

        //TODO: testar com static e a retornar valor
        this.stackManager.decrementStackCounter(decrementValue);

    }

}
