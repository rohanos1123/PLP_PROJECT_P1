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

    private void addFrame(Frame.Type type) {
        sm.pushFrame(new Frame<Immediate>(type));
        immediateCounts.push(1);
    }

    private void addFrame(Frame.Type type, CallableInfo ci) {
        addFrame(type);
        writer.addFunction(ci);
    }

    private void popFrame() {
        if (sm.top().scopeType == Frame.Type.FUNCTION) {
            writer.flushFunction();
        }
        immediateCounts.pop();
        sm.popFrame();
    }

    private Immediate getImmediate(GeneratorResult result) {
        return switch(result) {
            case Immediate i -> i;
            default -> throw new RuntimeException("Bad generator result");
        };
    }

    private ArrayList<Immediate> getImmediateList(GeneratorResult result) {
        return switch(result) {
            case ImmediateList i -> i.value();
            default -> throw new RuntimeException("Bad generator result");
        };
    }

    private String getIdentifier(GeneratorResult result) {
        return switch(result) {
            case Identifier i -> i.value();
            default -> throw new RuntimeException("Bad generator result");
        };
    }

    private ArrayList<String> getIdentifierList(GeneratorResult result) {
        return switch(result) {
            case IdentifierList i -> i.value();
            default -> throw new RuntimeException("Bad generator result");
        };
    }

    private boolean loadingReference() {
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
                        type = (loadingReference()) ? type : "i32";
                        break;

                    case BOOL:
                        formatString += "%d ";
                        type = (loadingReference()) ? type : "i1";
                        break;
                    
                    case STRING:
                        formatString += "%s ";
                        type = (loadingReference()) ? type : "ptr";
                        break;
                
                    default:
                        break;
                }
                inputString += type + " %" + arg.id + " ";
            }
            String end = (addNewline) ? "\\0A\\00" : "\\00";
            int endLength = end.length() / 3;
            formatString = formatString.substring(0, formatString.length() - 1) + end; // strip trailing ' '
            if (!formatStrings.containsKey(formatString)) {
                writer.writeln("@.str." + formatStrings.size() + " = private unnamed_addr constant [" +
                               (3*fmtArgs.size() - 1 + endLength) + " x i8] c\"" + formatString + "\", align 1", true);
                formatStrings.put(formatString, "@.str." + formatStrings.size());
            }
            inputString = inputString.substring(0, inputString.length() - 1); // strip trailing ' '
            return new String[] {formatString, inputString};
        };

        sm.addCallable(new CallableInfo("Write", new ArrayList<>(), true),
        (args) -> {
            String[] out = addFormatString.apply(args, false);
            String formatString = out[0];
            String inputString = out[1];
            var count = immediateCounts.pop();
            writer.writeln("%" + count++ + " = call i32 (ptr, ...) @printf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
            immediateCounts.push(count);
            return new Immediate();
        }
        );
        sm.addCallable(new CallableInfo("WriteLn", new ArrayList<>(), true),
        (args) -> {
            String[] out = addFormatString.apply(args, true);
            String formatString = out[0];
            String inputString = out[1];
            var count = immediateCounts.pop();
            writer.writeln("%" + count++ + " = call i32 (ptr, ...) @printf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
            immediateCounts.push(count);
            return new Immediate();
        }
        );
        sm.addCallable(new CallableInfo("ReadLn", new ArrayList<>(), true),
        (args) -> {
            String[] out = addFormatString.apply(args, false);
            String formatString = out[0];
            String inputString = out[1];
            var count = immediateCounts.pop();
            writer.writeln("%" + count++ + " = call i32 (ptr, ...) @scanf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
            immediateCounts.push(count);
            return new Immediate();
        }
        );
    }

    private CallableInfo createCallableInfo(String callableName, DelphiParser.FormalParameterListContext paramCtx) {
        var callableId = new CallableInfo(callableName);
        if (paramCtx != null) {
            var params = getImmediateList(visit(paramCtx));
            for (var param : params) {
                callableId.parameterNames.add(param.identifier);
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

    @Override
    public GeneratorResult visitProgram(DelphiParser.ProgramContext ctx){
        var main = new CallableInfo("!!main");
        main.returnType = TYPE.INT;
        addFrame(Frame.Type.FUNCTION, main);
        generateBuiltinCallables();
        visit(ctx.topLevelBlock());
        var count = immediateCounts.pop();
        writer.addLiteralAccess(0, count++);
        immediateCounts.push(count);
        writer.addReturnStatement(new Immediate(TYPE.INT, count - 1));
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
            writer.addCallableCall(procedureId, args, 0);
            return new Immediate();
        });

        addFrame(Frame.Type.FUNCTION, procedureId);
        visit(ctx.block());
        writer.addReturnStatement(new Immediate());
        popFrame();
        return new Immediate();
    }

    @Override
    public GeneratorResult visitVariableDeclaration(DelphiParser.VariableDeclarationContext ctx){
        var identifiers = getIdentifierList(visit(ctx.identifierList()));
        TYPE type = getImmediate(visit(ctx.type_())).type;
        var count = immediateCounts.pop();
        for(var identifier : identifiers){
            var variable = new Immediate(type, count++);
            sm.addVariable(identifier, variable);
            writer.addVariableDeclaration(variable);
        }
        immediateCounts.push(count);
        return new Immediate();
    }

    @Override
    public GeneratorResult visitProcedureStatement(DelphiParser.ProcedureStatementContext ctx){
        var procedureName = getIdentifier(visit(ctx.identifier()));
        if (procedureName.equals("ReadLn")) { // load by reference for readline
            immediateReferenceStack.push(true);
        }
        else {
            immediateReferenceStack.push(false);
        }
        var args = (ctx.parameterList() != null) ? getImmediateList(visit(ctx.parameterList())) : new ArrayList<Immediate>();
        var paramTypes = new ArrayList<TYPE>(args.stream().map(arg -> arg.type).toList());
        var callableId = new CallableInfo(procedureName, paramTypes);
        if (sm.getFunction(callableId).isEmpty()) {
            // try again for variadic functions
            callableId.variadic = true;
            if (sm.getFunction(callableId).isEmpty()) {
                throw new DelphiError(procedureName + " not found.", ctx);
            }
        }
        var result = sm.getFunction(callableId).get().apply(args);
        immediateReferenceStack.pop();
        return result;
        // return executeCallable(procedureName, ctx.parameterList());
    }

    @Override
    public GeneratorResult visitVariable(DelphiParser.VariableContext ctx) {
        var variableName = getIdentifier(visit(ctx.identifier(0)));
        var variable = sm.getVariable(variableName);
        if (loadingReference()) {
            return variable.get();
        }
        var count = immediateCounts.pop();
        writer.addVariableAccess(variable.get(), count++);
        immediateCounts.push(count);
        return new Immediate(variable.get().type, count - 1);
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
        return new Immediate(TYPE.INT, -1);
    }
}
