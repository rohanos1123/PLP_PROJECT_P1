package Generator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyStore.Entry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;

import Util.CLASS;
import Util.CallableInfo;
import Util.ScopeManager;
import Util.TYPE;
import Util.TypeInfo;
import Util.Frame;
import Util.GenericType;


enum MathOperations{
    ADD,
    SUB, 
    MUL, 
    DIV, 
    MOD
}; 

enum LogicalOperations{
    AND, 
    OR, 
    XOR
}; 

enum CmpOperations{
    LT, 
    GT,
    LTE,
    GTE, 
    EQ,
    NEQ, 
}; 


public class IRWriter {
    private class StreamWriter extends PrintWriter {
        private final Writer underlyingStream;

        public StreamWriter(Writer stream) {
            super(stream);
            this.underlyingStream = stream;
        }

        public String toString() {
            return underlyingStream.toString();
        }
    }

    // Simple pojo that contains loop bounds
    private record ControlBound(int breakBound, int continueBound) {}; 

    private StreamWriter outputFile;
    private ScopeManager<Immediate> sm;
    private Deque<StreamWriter> functionStreams = new ArrayDeque<>();
    private boolean writingClass = false;


    public IRWriter(String outputFile, ScopeManager<Immediate> sm) throws IOException {
        this.outputFile = new StreamWriter(new FileWriter(outputFile));
        this.sm = sm;
    }

    private String convertType(GenericType t) {
        return switch (t) {
            case TYPE tp -> switch (tp) {
                case INT -> "i32";
                case REAL -> "double";
                case CHAR -> "i8";
                case BOOL -> "i1";
                case STRING -> "i8*";
                case VOID -> "void";
                case INTPTR -> "i32*";
                case BOOLPTR -> "i1*";
                case REALPTR -> "double*";
                case CHARPTR -> "i8*";
                case REFERENCE -> "ptr";
                default -> "";
            };
            case CLASS cp -> cp.name();
        };
    }   

    private String getDefaultValue(GenericType t) {
        return switch (t) {
            case TYPE tp -> switch (tp) {
                case INT -> "0";
                case REAL -> "0.0";
                case BOOL -> "0";
                case CHAR -> "0";
                case STRING -> "zeroinitializer";
                default -> "";
            };
            default -> "zeroinitializer";
        };
    }

    private String createFunctionSignature(CallableInfo ci, boolean includeParams) {
        var returnType = convertType(ci.returnType);
        var callableName = (ci.name.startsWith("!!")) ? ci.name.substring(2) : "Delphi_" + ci.name + "_" + ci.parameterTypes.size(); // for now add the param count to differentiate between overloads
        String signature = returnType + " @" + callableName;
        if (!includeParams) return signature;

        signature += "(";
        if (ci.variadic) {
            signature += "ptr, ...";
        }
        for (int i = 0; i < ci.parameterTypes.size(); i++) {
            signature += convertType(ci.parameterTypes.get(i));
            signature += " %" + i;
            signature += ", ";
        }
        if (!ci.parameterTypes.isEmpty()) {
            signature = signature.substring(0, signature.length() - 2); // strip trailing ', '
        }
        signature += ")";
        return signature;
    }

    private void writeln(String s, boolean global, String tab) {
        if (sm.top().scopeType == Frame.Type.CLASS && !writingClass) return; // handle classes separately through dedicated function
        if (global) {
            outputFile.println(s);
            outputFile.flush();
        }
        else {
            var targetStream = functionStreams.peek();
            targetStream.println(tab + s);
            targetStream.flush();
        }
    }

    private String replaceControlStatements(String bodyString, String breakBranch, String continueBranch){
        String breakPass = bodyString.replace("BREAK_PLACEHOLDER", breakBranch);
        return breakPass.replace("CONTINUE_PLACEHOLDER", continueBranch); 
    }

    public void writeln(String s, boolean global) {
        writeln(s, global, "    ");
    }

    public void writeln(String s) {
        writeln(s, false);
    }

    public void addClass(TypeInfo<Immediate> ti) {
        writingClass = true;
        String classDeclaration = "%" + ti.name + " = type { ";
        var attributes = ti.getOrderedAttributes();
        for (var entry : attributes.entrySet()) {
            var attribute = entry.getValue().member();
            classDeclaration += convertType(attribute.type) + ", ";
        }
        if (!attributes.isEmpty()) {
            classDeclaration = classDeclaration.substring(0, classDeclaration.length() - 2); // strip trailing ', '
        }
        classDeclaration += " }";
        writeln(classDeclaration, true);
        writingClass = false;
    }

    public void addFunction(CallableInfo ci) {
        functionStreams.push(new StreamWriter(new StringWriter()));
        String definition = "define " + createFunctionSignature(ci, true) + " {";
        writeln(definition, false, "");
    }

    public void flushFunction() {
        writeln("}", false, ""); // write scope end
        outputFile.print(functionStreams.pop().toString());
        outputFile.flush();
    }

    public Immediate addCallableCall(CallableInfo ci, ArrayList<Immediate> args, int immediateIndex) {
        var ret = new Immediate(ci.returnType, "%" + immediateIndex);
        String call = (ci.returnType == TYPE.VOID) ? "" : ret.id + " = ";
        call += "call " + createFunctionSignature(ci, false) + "(";
        for (Immediate arg : args) {
            call += convertType(arg.type) + " " + arg.id + ", ";
        }
        if (!args.isEmpty()) {
            call = call.substring(0, call.length() - 2); // strip trailing ', '
        }
        call += ")";
        writeln(call);
        return ret;
    }

    public Immediate addVariableDeclaration(GenericType type, int immediateIndex) {
        var variable = new Immediate(type, "");
        String declaration = "";
        if (sm.global()) { // global scope or main scope
            variable.id = "@" + immediateIndex;
            declaration += variable.id + " = global " + convertType(variable.type) + " " + getDefaultValue(variable.type); 
        }
        else {
            variable.id = "%" + immediateIndex;
            declaration += variable.id + " = alloca " + convertType(variable.type);
        }
        variable.type = GenericType.getPtrType(type); // new allocated variable is a ptr to a value
        writeln(declaration, sm.global());
        return variable;
    }

    public Immediate addBinaryExpression(Immediate lhs, Immediate rhs, MathOperations op, int immediateIndex){
        GenericType resType = lhs.type;    
        var immediate = new Immediate(resType, "%" + immediateIndex); 
        immediate.type = resType; 
        String production = immediate.id + " = "; 

        switch(op){
            case ADD: 
                production += (resType == TYPE.INT) ? "add " : "fadd ";
                break; 
            case SUB:
                production += (resType == TYPE.INT) ? "sub " : "fsub ";
                break; 
            case DIV: 
                production += (resType == TYPE.INT) ? "sdiv " : "fdiv ";
                break; 
            case MUL: 
                production += (resType == TYPE.INT) ? "mul " : "fmul ";
                break; 
            case MOD : 
                production += (resType == TYPE.INT) ? "srem " : "frem ";
                break; 
        }

        production += convertType(resType); 
        production += " " + lhs.id + ", " + rhs.id; 
        writeln(production);
        return immediate; 
    }


    public Immediate addLogicalExpression(Immediate lhs, Immediate rhs, LogicalOperations op, int immediateIndex){
        GenericType resType = lhs.type; 
    
        var immediate = new Immediate(resType, "%" + immediateIndex); 
        String production = immediate.id + " = "; 
        immediate.type = TYPE.BOOL; 

        switch(op){
            case AND: 
                production += "and "; 
                break;
            case OR: 
                production += "or "; 
                break; 
            case XOR: 
                production += "xor ";
                break;  
        }

        production += " i1 " + lhs.id + ", " + rhs.id; 
        writeln(production);
        return immediate; 
    }

    public Immediate addComparisonExpressions(Immediate lhs, Immediate rhs, CmpOperations op, int immediateIndex){
        GenericType resType = lhs.type;
        if(lhs.type != rhs.type){
            throw new RuntimeException("Comparison expression with mismatched types!"); 
        }
        var compRes = new Immediate(resType, "%" + immediateIndex); 
        compRes.type = TYPE.BOOL; 
        String production = compRes.id + " = icmp"; 

        switch(op){
            case LT:
                production += " slt "; 
                break;
            case GT:
                production += " sgt "; 
                break; 
            case LTE:
                production += " sle ";
                break; 
            case GTE: 
                production += " sge ";
                break; 
            case EQ:
                production += " eq ";
                break; 
            case NEQ:
                break; 
        }

        production += convertType(resType); 
        production += " " + lhs.id + ", " + rhs.id;  
        writeln(production);
        return compRes; 
    }

    public void addBlock(){
        functionStreams.push(new StreamWriter(new StringWriter()));
    }

    public void addContinuePlaceholder(int currIndex){
        writeln("CONTINUE_PLACEHOLDER"); 
        addLabel(currIndex);
    }

    public void addBreakPlaceholder(int currIndex){
        writeln("BREAK_PLACEHOLDER");
        addLabel(currIndex);
    }

    public void addBranchStatementAndFlush(Immediate target, int thenIndex, int elseIndex, int immediateIndex){
      
        String mergeBranch = "br label %" + immediateIndex; 
        String mergeLabel =  immediateIndex + ":"; 

        if(elseIndex != -1){
            String branchProd = "br i1 " + target.id + ", label %" + thenIndex + ", label %" + elseIndex;
            var elseBlock = functionStreams.peek();
            functionStreams.pop(); 

            var thenBlock = functionStreams.peek(); 
            functionStreams.pop(); 

            thenBlock.println(mergeBranch);
            elseBlock.println(mergeBranch);

            writeln(branchProd); 
            writeln(thenBlock.toString());
            writeln(elseBlock.toString());
            writeln(mergeLabel);
        }
        else{
            String branchProd = "br i1 " + target.id + ", label %" + thenIndex + ", label %" + immediateIndex;
            var thenBlock = functionStreams.peek(); 
            functionStreams.pop(); 
            thenBlock.println(mergeBranch);

            writeln(branchProd); 
            writeln(thenBlock.toString());
            writeln(mergeLabel);
        }
    }

   
    public void addLabel(int immediateIndex){
        String thenLabel = immediateIndex + ":"; 
        writeln(thenLabel);
    }


    public void addUnconditionalBranch(int branchTarget){
        String branchInstr = "br label %" + branchTarget; 
        writeln(branchInstr);
    }


    public void addConditionalBranch(Immediate conImmediate, int trueBrIndex, int falseBrIndex){
        String condBranch = "br i1 " + conImmediate.id + ", label %" + trueBrIndex + ", label %" + falseBrIndex; 
        writeln(condBranch); 
    }


    public void addWhileLoop(Immediate condImmediate, int loopBackIndex, int bodyIndex,  int jumpIndex){
        var bodyPart = functionStreams.peek(); 
        functionStreams.pop(); 
        var condPart = functionStreams.peek(); 
        functionStreams.pop(); 

        String breakBranch = "br label %" + jumpIndex;
        String continueBranch = "br label %" + loopBackIndex; 

        
        writeln(condPart.toString());
        String jumpConditional = "br i1 " + condImmediate.id + ", label %" + bodyIndex + ", label %" + jumpIndex; 
        writeln(jumpConditional);
        //writeln(bodyPart.toString());
        writeln(replaceControlStatements(bodyPart.toString(), breakBranch, continueBranch));
        addUnconditionalBranch(loopBackIndex);
        addLabel(jumpIndex); 
    }

    public void addForLoop(Immediate forCmp, int bodyIndex, int incrementIndex, int currIndex){
        var incrementBlock = functionStreams.peek(); 
        functionStreams.pop(); 
        var bodyBlock = functionStreams.peek(); 
        functionStreams.pop(); 
        var comparisonBlock = functionStreams.peek(); 
        functionStreams.pop(); 
        var intialBlock = functionStreams.peek(); 
        functionStreams.pop(); 

        String breakBranch = "br label %" + currIndex; 
        String continueBranch = "br label %" + incrementIndex;


        writeln(intialBlock.toString());
        writeln(comparisonBlock.toString());
        addConditionalBranch(forCmp, currIndex, bodyIndex);
        //writeln(bodyBlock.toString());
        writeln(replaceControlStatements(bodyBlock.toString(), breakBranch, continueBranch));
        writeln(incrementBlock.toString());
        addLabel(currIndex); 
    }



    public Immediate addString(String literal, int size, int id) {
        var reference = new Immediate(TYPE.STRING, "@.str." + id);
        String constant = reference.id + " = private unnamed_addr constant ["
                        + (size + 1) + " x i8] c\"" + literal + "\\00\", align 1";
        writeln(constant, true);
        return reference;
    }
    
    public Immediate addVariableAccess(Immediate variable, int immediateIndex) {
        var immediate = new Immediate(GenericType.getValueType(variable.type), "%" + immediateIndex); // guaranteed to be a local
        String access = immediate.id + " = load " + convertType(immediate.type) + ", ptr " + variable.id;
        writeln(access);
        return immediate;
    }

    public Immediate addMemberAccess(Immediate object, Immediate classMember, int immediateIndex) {
        var immediate = new Immediate(GenericType.getPtrType(classMember.type), "%" + immediateIndex); // guaranteed to be local
        String access = immediate.id + " = getelementptr inbounds "
                      + convertType(GenericType.getValueType(object.type)) + ", ptr " + object.id
                      + ", i32 " + classMember.id.substring(1); // strip 'c' to get idx
        writeln(access);
        return immediate;
    }

    public void addVariableStore(Immediate variable, Immediate value) {
        String store = "store " + convertType(value.type) + " " + value.id
                     + ", ptr " + variable.id;
        writeln(store);
    }

    public void addReturnStatement(Immediate result) {
        String ret = "ret " + convertType(result.type);
        if (result.type != TYPE.VOID) {
            ret += " " + result.id;
        }
        writeln(ret);
    }

    public Immediate addBitCast(Immediate toCast, GenericType newType, int immediateIndex) {
        var result = new Immediate(newType, "%" + immediateIndex);
        String bitcast = result.id + " = bitcast " + convertType(toCast.type) + " " + toCast.id
                       + " to " + convertType(newType);
        writeln(bitcast);
        return result;
    }


}
