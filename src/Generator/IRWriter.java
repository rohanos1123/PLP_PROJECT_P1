package Generator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import Util.CallableInfo;
import Util.ScopeManager;
import Util.TYPE;
import Util.Frame;

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

    private StreamWriter outputFile;
    private ScopeManager<Immediate> sm;
    private Deque<StreamWriter> functionStreams = new ArrayDeque<>();

    public IRWriter(String outputFile, ScopeManager<Immediate> sm) throws IOException {
        this.outputFile = new StreamWriter(new FileWriter(outputFile));
        this.sm = sm;
    }

    private String convertType(TYPE t) {
        switch (t) {
            case INT: return "i32";
            case BOOL: return "i1";
            case STRING: return "i8*";
            case VOID: return "void";
            default: return "";
        }
    }

    private String getDefaultValue(TYPE t) {
        switch (t) {
            case INT: return "0";
            case BOOL: return "0";
            case STRING: return "zeroinitializer";
            default: return "";
        }
    }

    private String createFunctionSignature(CallableInfo ci, boolean includeParams) {
        var returnType = convertType(ci.returnType);
        var callableName = (ci.name == "!!main") ? "main" : "Delphi_" + ci.name;
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
        if (sm.top().scopeType == Frame.Type.CLASS) return; // handle classes separately through dedicated function
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

    public void writeln(String s, boolean global) {
        writeln(s, global, "    ");
    }

    public void writeln(String s) {
        writeln(s, false);
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

    public void addCallableDeclaration(CallableInfo ci) {
        String declaration = "declare " + createFunctionSignature(ci, false);
        writeln(declaration, true);
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

    public Immediate addVariableDeclaration(TYPE type, int immediateIndex) {
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
        writeln(declaration, sm.global());
        return variable;
    }

    public Immediate addVariableAccess(Immediate variable, int immediateIndex) {
        var immediate = new Immediate(variable.type, "%" + immediateIndex); // guaranteed to be a local
        String access = immediate.id + " = load " + convertType(variable.type) + ", ptr " + variable.id;
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
}
