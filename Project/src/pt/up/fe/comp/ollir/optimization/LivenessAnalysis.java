package pt.up.fe.comp.ollir.optimization;

import org.specs.comp.ollir.*;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

import java.util.*;

public class LivenessAnalysis {
    private HashMap<Integer, List<String>> in;
    private HashMap<Integer, List<String>> out;
    private HashMap<Integer, String> def;
    private HashMap<Integer, List<String>> use;

    public LivenessAnalysis() {
        this.in = new HashMap<>();
        this.out = new HashMap<>();
        this.def = new HashMap<>();
        this.use = new HashMap<>();
    }

    public List<HashMap<Integer, List<String>>> analyse(Method method) {
        var instructions = method.getInstructions();

        for (var instruction : instructions) {
            in.put(instruction.getId(), new ArrayList<>());
            out.put(instruction.getId(), new ArrayList<>());
            def.put(instruction.getId(), getDefinedVars(instruction));
            use.put(instruction.getId(), getUsedVars(instruction));
        }

        boolean changes;

        do {
            changes = false;
            for (int i = instructions.size()-1; i >= 0; i--) {
                var instruction = instructions.get(i);
                var id = instruction.getId();

                List<String> in_ = new ArrayList<>(in.get(id));
                List<String> out_ = new ArrayList<>(out.get(id));

                out.put(id, getNewOut(instruction));

                var diff = new ArrayList<>(out.get(id));
                var definedVar = def.get(id);
                if (definedVar != null)
                    diff.remove(definedVar);

                in.put(id, union(use.get(id), diff));

                if (!changes) {
                    if (in.get(id).stream().anyMatch(in_::contains)) {
                        changes = true;
                        continue;
                    }
                    changes = out.get(id).stream().anyMatch(out_::contains);
                }
            }
        } while (changes);

        return Arrays.asList(in, out);
    }

    private List<String> getNewOut(Instruction instruction) {
        List<String> result = new ArrayList<>();
        for (var succ : instruction.getSuccessors()) {
            var value = in.get(succ.getId());
            if (value == null) continue;

            result.addAll(value);
        }
        return result;
    }

    public <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<T>(set);
    }

    private List<String> getUsedVars(Instruction instruction) {
        return switch (instruction.getInstType()) {
            case ASSIGN -> getUsedVars((AssignInstruction) instruction);
            case CALL -> getUsedVars((CallInstruction) instruction);
            case GOTO -> new ArrayList<>();
            case BRANCH -> getUsedVars((CondBranchInstruction) instruction);
            case RETURN -> getUsedVars((ReturnInstruction) instruction);
            case PUTFIELD -> getUsedVars((PutFieldInstruction) instruction);
            case GETFIELD -> getUsedVars((GetFieldInstruction) instruction);
            case UNARYOPER -> getUsedVars((UnaryOpInstruction) instruction);
            case BINARYOPER -> getUsedVars((BinaryOpInstruction) instruction);
            case NOPER -> getUsedVars((SingleOpInstruction) instruction);
        };
    }
    private List<String> getUsedVars(AssignInstruction instruction) {
        return getUsedVars(instruction.getRhs());
    }
    private List<String> getUsedVars(CallInstruction instruction) {
        List<String> result = new ArrayList<>();
        for (var operand : instruction.getListOfOperands()) {
            var operandName = getOperandName(operand);
            if (operandName == null) continue;

            result.add(operandName);
        }
        return result;
    }
    private List<String> getUsedVars(CondBranchInstruction instruction) {
        if (instruction instanceof SingleOpCondInstruction) {
            return getUsedVars(((SingleOpCondInstruction) instruction).getCondition());
        }

        //TODO verify if other instances
        throw new NotImplementedException(instruction.getInstType());
    }

    private List<String> getUsedVars(ReturnInstruction instruction) {
        if (!instruction.hasReturnValue()) return new ArrayList<>();

        var operandName = getOperandName(instruction.getOperand());
        if (operandName == null) return new ArrayList<>();
        return Collections.singletonList(operandName);
    }

    private List<String> getUsedVars(PutFieldInstruction instruction) {

        List<String> result = new ArrayList<>();

        var operandName1 = getOperandName(instruction.getSecondOperand());
        var operandName2 = getOperandName(instruction.getThirdOperand());

        if (operandName1 != null) result.add(operandName1);
        if (operandName2 != null) result.add(operandName2);

        return result;
    }

    private List<String> getUsedVars(GetFieldInstruction instruction) {
        var operandName = getOperandName(instruction.getSecondOperand());
        if (operandName == null) return new ArrayList<>();
        return Collections.singletonList(operandName);
    }

    private List<String> getUsedVars(UnaryOpInstruction instruction) {
        List<String> result = new ArrayList<>();

        var operandName = getOperandName(instruction.getOperand());

        if (operandName != null) result.add(operandName);

        return result;
    }

    private List<String> getUsedVars(BinaryOpInstruction instruction) {
        List<String> result = new ArrayList<>();

        var operandName1 = getOperandName(instruction.getRightOperand());
        var operandName2 = getOperandName(instruction.getLeftOperand());

        if (operandName1 != null) result.add(operandName1);
        if (operandName2 != null) result.add(operandName2);

        return result;
    }

    private List<String> getUsedVars(SingleOpInstruction instruction) {
        var operandName = getOperandName(instruction.getSingleOperand());
        if (operandName == null) return new ArrayList<>();

        return Collections.singletonList(operandName);
    }

    private String getOperandName(Element element) {
        if (element.isLiteral()) return null;

        return ((Operand) element).getName();
    }

    private String getDefinedVars(Instruction instruction) {
        if (instruction.getInstType() != InstructionType.ASSIGN) return null;

        var assignInstruction = (AssignInstruction) instruction;
        return ((Operand)assignInstruction.getDest()).getName();
    }
}
