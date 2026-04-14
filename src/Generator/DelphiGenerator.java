package Generator;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.function.BiFunction;

import Util.CallableInfo;
import Util.DelphiError;
import Util.Frame;
import Util.ScopeManager;
import Util.TYPE;
import Util.TypeInfo;
import grammar.DelphiBaseVisitor;
import grammar.DelphiParser;

public class DelphiGenerator extends DelphiBaseVisitor<GeneratorResult> {
    private ScopeManager<Immediate> sm = new ScopeManager<>();
    HashMap<String, TypeInfo<Immediate>> typeInfo = new HashMap<>();
    Deque<Integer> immediateCounts = new ArrayDeque<>();
    private HashMap<String, String> formatStrings = new HashMap<>();
    private IRWriter writer;

    // Keeps track of information between visitors
    Deque<String> typeStack = new ArrayDeque<>();
    Deque<Boolean> immediateReferenceStack = new ArrayDeque<>();

    public DelphiGenerator(String outputFile) throws IOException {
        writer = new IRWriter(outputFile, sm);
    }

    private void addFrame(Frame.Type type, int count) {
        sm.pushFrame(new Frame<Immediate>(type));
        immediateCounts.push(count);
    }

    private void addFrame(Frame.Type type, CallableInfo ci) {
        addFrame(type, ci.parameterNames.size() + 1);
        writer.addFunction(ci);
    }

    private void popFrame() {
        if (sm.top().scopeType == Frame.Type.FUNCTION) {
            writer.flushFunction();
        }
        immediateCounts.pop();
        sm.popFrame();
    }

    private void addVariable(String identifier, TYPE type, int id) {
        var variable = writer.addVariableDeclaration(type, id);
        sm.addVariable(identifier, variable);
    }

    private Immediate getImmediate(GeneratorResult result) {
        return switch(result) {
            case Immediate i -> i;
            default -> throw new RuntimeException("Wrong type for GeneratorResult; probably missed a visited branch");
        };
    }

    private ArrayList<Immediate> getImmediateList(GeneratorResult result) {
        return switch(result) {
            case ImmediateList i -> i.value();
            default -> throw new RuntimeException("Wrong type for GeneratorResult; probably missed a visited branch");
        };
    }

    private String getIdentifier(GeneratorResult result) {
        return switch(result) {
            case Identifier i -> i.value();
            default -> throw new RuntimeException("Wrong type for GeneratorResult; probably missed a visited branch");
        };
    }

    private ArrayList<String> getIdentifierList(GeneratorResult result) {
        return switch(result) {
            case IdentifierList i -> i.value();
            default -> throw new RuntimeException("Wrong type for GeneratorResult; probably missed a visited branch");
        };
    }

    private boolean readAsReference() {
        return !immediateReferenceStack.isEmpty() && immediateReferenceStack.peek();
    }

    private void generateBuiltinCallables() {
        writer.writeln("declare i32 @printf(ptr, ...)", true);
        writer.writeln("declare i32 @scanf(ptr, ...)", true);
        BiFunction<ArrayList<Immediate>, Boolean, String[]> addFormatString = (fmtArgs, addNewline) -> {
            String formatString = "";
            String inputString = "";
            for (Immediate arg : fmtArgs) {
                String type = "ptr";
                switch (arg.type) {
                    case INT:
                        formatString += "%d ";
                        type = (readAsReference()) ? type : "i32";
                        break;

                    case BOOL:
                        formatString += "%d ";
                        type = (readAsReference()) ? type : "i1";
                        break;
                    
                    case STRING:
                        formatString += "%s ";
                        type = (readAsReference()) ? type : "ptr";
                        break;
                
                    default:
                        break;
                }
                inputString += type + " " + arg.id + ", ";
            }
            String end = (addNewline) ? "\\0A\\00" : "\\00";
            int endLength = end.length() / 3;
            formatString = formatString.substring(0, formatString.length() - 1) + end; // strip trailing ' '
            if (!formatStrings.containsKey(formatString)) {
                writer.writeln("@.str." + formatStrings.size() + " = private unnamed_addr constant [" +
                               (3*fmtArgs.size() - 1 + endLength) + " x i8] c\"" + formatString + "\", align 1", true);
                formatStrings.put(formatString, "@.str." + formatStrings.size());
            }
            inputString = inputString.substring(0, inputString.length() - 2); // strip trailing ', '
            return new String[] {formatString, inputString};
        };

        /* manually write printf and scanf calls since they are slightly different from normal internal function calls */
        sm.addCallable(new CallableInfo("Write", new ArrayList<>(), true),
        (args) -> {
            String[] out = addFormatString.apply(args, false);
            String formatString = out[0];
            String inputString = out[1];
            var count = immediateCounts.pop();
            writer.writeln("%" + count + " = call i32 (ptr, ...) @printf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
            immediateCounts.push(count + 1);
            return new Immediate();
        }
        );
        sm.addCallable(new CallableInfo("WriteLn", new ArrayList<>(), true),
        (args) -> {
            String[] out = addFormatString.apply(args, true);
            String formatString = out[0];
            String inputString = out[1];
            var count = immediateCounts.pop();
            writer.writeln("%" + count + " = call i32 (ptr, ...) @printf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
            immediateCounts.push(count + 1);
            return new Immediate();
        }
        );
        sm.addCallable(new CallableInfo("ReadLn", new ArrayList<>(), true),
        (args) -> {
            String[] out = addFormatString.apply(args, false);
            String formatString = out[0];
            String inputString = out[1];
            var count = immediateCounts.pop();
            writer.writeln("%" + count + " = call i32 (ptr, ...) @scanf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
            immediateCounts.push(count + 1);
            return new Immediate();
        }
        );
    }

    private CallableInfo createCallableInfo(String callableName, DelphiParser.FormalParameterListContext paramCtx) {
        var callableId = new CallableInfo(callableName);
        if (paramCtx != null) {
            var params = getImmediateList(visit(paramCtx));
            for (var param : params) {
                callableId.parameterNames.add(param.id);
                callableId.parameterTypes.add(param.type);
            }
        }
        return callableId;
    }

    private Immediate registerCallable(DelphiParser.FormalParameterListContext paramCtx) {
        String callableName = this.typeStack.pop();
        var functionId = createCallableInfo(callableName, paramCtx);
        sm.addCallable(functionId, (args) -> {
            throw new DelphiError("No definition for: " + callableName, paramCtx);
        });
        return new Immediate();
    }

    private Immediate generateCall(String functionName, DelphiParser.ParameterListContext paramCtx) {
        boolean readAsReference = functionName.equals("ReadLn"); // load by reference for readline (scanf)
        immediateReferenceStack.push(readAsReference);
        var args = (paramCtx != null) ? getImmediateList(visit(paramCtx)) : new ArrayList<Immediate>();
        var paramTypes = new ArrayList<TYPE>(args.stream().map(arg -> arg.type).toList());
        var callableId = new CallableInfo(functionName, paramTypes);
        if (sm.getFunction(callableId).isEmpty()) {
            // try again for variadic functions
            callableId.variadic = true;
            if (sm.getFunction(callableId).isEmpty()) {
                throw new DelphiError(functionName + " not found.", paramCtx);
            }
        }
        var result = sm.getFunction(callableId).get().apply(args);
        immediateReferenceStack.pop();
        return result;
    }

    @Override
    public GeneratorResult visitProgram(DelphiParser.ProgramContext ctx){
        var main = new CallableInfo("!!main");
        main.returnType = TYPE.INT;
        addFrame(Frame.Type.FUNCTION, main);
        generateBuiltinCallables();
        visit(ctx.topLevelBlock());
        writer.addReturnStatement(new Immediate(TYPE.INT, "0"));
        popFrame();
        return null;
    }

    @Override
    public GeneratorResult visitExpandedTypeDefinition(DelphiParser.ExpandedTypeDefinitionContext ctx){
        String typeName = getIdentifier(visit(ctx.identifier()));
        this.typeStack.push(typeName);
        return visitChildren(ctx);
    }

    @Override
    public GeneratorResult visitTypeDefinition(DelphiParser.TypeDefinitionContext ctx){
        String typeName = getIdentifier(visit(ctx.identifier()));
        this.typeStack.push(typeName);
        return visitChildren(ctx);
    }

    @Override
    public GeneratorResult visitProcedureType(DelphiParser.ProcedureTypeContext ctx){
        return registerCallable(ctx.formalParameterList());
    }

    @Override
    public GeneratorResult visitFunctionType(DelphiParser.FunctionTypeContext ctx){
        return registerCallable(ctx.formalParameterList());
    }

    @Override
    public GeneratorResult visitProcedureDeclaration(DelphiParser.ProcedureDeclarationContext ctx){
        /* might need to eventually check for redefinition dunno if its allowed */
        String procedureName = getIdentifier(visit(ctx.identifier()));
        var procedureId = createCallableInfo(procedureName, ctx.formalParameterList());
        sm.addCallable(procedureId, (args) -> {
            return writer.addCallableCall(procedureId, args, 0);
        });

        addFrame(Frame.Type.FUNCTION, procedureId);
        var count = immediateCounts.pop();
        for (int i = 0; i < procedureId.parameterNames.size(); i++) {
            var paramName = procedureId.parameterNames.get(i);
            var paramType = procedureId.parameterTypes.get(i);
            addVariable(paramName, paramType, count++);
            writer.addVariableStore(sm.getVariable(paramName).get(), new Immediate(paramType, "%" + i));
        }
        immediateCounts.push(count);
        visit(ctx.block());
        writer.addReturnStatement(new Immediate());
        popFrame();
        return new Immediate();
    }

    @Override
    public GeneratorResult visitFunctionDeclaration(DelphiParser.FunctionDeclarationContext ctx){
        /* might need to eventually check for redefinition dunno if its allowed */
        String functionName = getIdentifier(visit(ctx.identifier()));
        var functionId = createCallableInfo(functionName, ctx.formalParameterList());
        functionId.returnType = getImmediate(visit(ctx.resultType())).type;
        sm.addCallable(functionId, (args) -> {
            var count = immediateCounts.pop();
            var result = writer.addCallableCall(functionId, args, count);
            immediateCounts.push(count + 1);
            return result;
        });

        addFrame(Frame.Type.FUNCTION, functionId);
        var count = immediateCounts.pop();
        for (int i = 0; i < functionId.parameterNames.size(); i++) {
            var paramName = functionId.parameterNames.get(i);
            var paramType = functionId.parameterTypes.get(i);
            addVariable(paramName, paramType, count++);
            writer.addVariableStore(sm.getVariable(paramName).get(), new Immediate(paramType, "%" + i));
        }
        addVariable("Result", functionId.returnType, count++);
        immediateCounts.push(count);
        visit(ctx.block());
        count = immediateCounts.pop();
        var result = writer.addVariableAccess(sm.getVariable("Result").get(), count++);
        immediateCounts.push(count);
        writer.addReturnStatement(result);
        popFrame();
        return new Immediate();
    }

    @Override
    public GeneratorResult visitAssignmentStatement(DelphiParser.AssignmentStatementContext ctx){
        immediateReferenceStack.push(true);
        var storeTarget = getImmediate(visit(ctx.variable()));
        immediateReferenceStack.pop();
        var value = getImmediate(visit(ctx.expression()));
        writer.addVariableStore(storeTarget, value);
        return storeTarget;
    }

    @Override
    public GeneratorResult visitVariableDeclaration(DelphiParser.VariableDeclarationContext ctx){
        var identifiers = getIdentifierList(visit(ctx.identifierList()));
        TYPE type = getImmediate(visit(ctx.type_())).type;
        var count = immediateCounts.pop();
        for(var identifier : identifiers){
            addVariable(identifier, type, count++);
        }
        immediateCounts.push(count);
        return new Immediate();
    }

    @Override
    public GeneratorResult visitProcedureStatement(DelphiParser.ProcedureStatementContext ctx){
        var procedureName = getIdentifier(visit(ctx.identifier()));
        return generateCall(procedureName, ctx.parameterList());
    }

    @Override
    public GeneratorResult visitFunctionDesignator(DelphiParser.FunctionDesignatorContext ctx) {
        var functionName = getIdentifier(visit(ctx.identifier()));
        return generateCall(functionName, ctx.parameterList());
    }

    @Override
    public GeneratorResult visitVariable(DelphiParser.VariableContext ctx) {
        var variableName = getIdentifier(visit(ctx.identifier(0)));
        var memberName = (ctx.identifier().size() > 1) ? getIdentifier(visit(ctx.identifier(1))) : "";
        var variable = sm.getVariable(variableName);
        var function = sm.getFunction(new CallableInfo(variableName));
        if (variable.isPresent() && memberName.isEmpty()) {
            if (readAsReference()) return variable.get();
            var count = immediateCounts.pop();
            var result = writer.addVariableAccess(variable.get(), count);
            immediateCounts.push(count + 1);
            return result;
        }
        else if (function.isPresent()) {
            return function.get().apply(new ArrayList<>());
        }
        else {
            throw new DelphiError("Identifier " + variableName + " not found in context", ctx);
        }
    }

    @Override
    public GeneratorResult visitFormalParameterList(DelphiParser.FormalParameterListContext ctx) {
        var paramGroups = ctx.formalParameterSection();
        ArrayList<Immediate> params = new ArrayList<>();
        for(int i = 0 ; i < paramGroups.size(); i++){
            var paramGroup = getImmediateList(visit(paramGroups.get(i)));
            for (var param : paramGroup) {
                params.add(param);
            }
        }
        return new ImmediateList(params);
    }

	@Override
    public GeneratorResult visitParameterGroup(DelphiParser.ParameterGroupContext ctx) {
        var params = getIdentifierList(visit(ctx.identifierList()));
        TYPE type = getImmediate(visit(ctx.typeIdentifier())).type;
        ArrayList<Immediate> groupVals = new ArrayList<>();
        for (var param : params) {
            groupVals.add(new Immediate(type, param));
        }
        return new ImmediateList(groupVals);
    }

    @Override
    public GeneratorResult visitParameterList(DelphiParser.ParameterListContext ctx) {
        var resolvedParams = new ArrayList<Immediate>();
        for (var param : ctx.actualParameter()) {
            resolvedParams.add(getImmediate(visit(param)));
        }
        return new ImmediateList(resolvedParams);
    }

    @Override
    public GeneratorResult visitIdentifierList(DelphiParser.IdentifierListContext ctx) {
        ArrayList<String> identifiers = new ArrayList<>();
        for (var identifier : ctx.identifier()) {
            identifiers.add(getIdentifier(visit(identifier)));
        }
        return new IdentifierList(identifiers);
    }

    @Override
    public GeneratorResult visitIdentifier(DelphiParser.IdentifierContext ctx){
        return new Identifier(ctx.IDENT().toString());
    }

	@Override
    public GeneratorResult visitTypeIdentifier(DelphiParser.TypeIdentifierContext ctx) {
        return new Immediate(TYPE.INT, "");
    }

    @Override
    public GeneratorResult visitSignedFactor(DelphiParser.SignedFactorContext ctx){
        var factor = getImmediate(visit(ctx.factor()));
        if(ctx.MINUS() != null) {
            // add immediate value binary op sub 0 factor and return that
        }
        return factor;
    }

    @Override
    public GeneratorResult visitUnsignedInteger(DelphiParser.UnsignedIntegerContext ctx){
        return new Immediate(TYPE.INT, ctx.NUM_INT().toString());
    }

    @Override
    public GeneratorResult visitString(DelphiParser.StringContext ctx){
        var literal = ctx.STRING_LITERAL().toString();
        return new Immediate(TYPE.STRING, literal.substring(1, literal.length() - 1));
    }

    @Override
    public GeneratorResult visitBool_(DelphiParser.Bool_Context ctx){
        return new Immediate(TYPE.BOOL, ctx.getText());
    }
}
