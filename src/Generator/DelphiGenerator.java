package Generator;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;

import Util.CLASS;
import Util.CallableInfo;
import Util.DelphiError;
import Util.Frame;
import Util.GenericType;
import Util.ScopeManager;
import Util.TYPE;
import Util.TypeInfo;
import Util.TypeInfo.InheritanceType;
import grammar.DelphiBaseVisitor;
import grammar.DelphiParser;

public class DelphiGenerator extends DelphiBaseVisitor<GeneratorResult> {
    private enum ACCESS_SPECIFIER {
        PUBLIC,
        PRIVATE
    };

    private ScopeManager<Immediate> sm = new ScopeManager<>();
    HashMap<String, TypeInfo<Immediate>> typeInfo = new HashMap<>();
    Deque<MutableInteger> immediateCounts = new ArrayDeque<>();
    private HashMap<String, String> formatStrings = new HashMap<>();
    private IRWriter writer;

    // Keeps track of information between visitors
    Deque<String> typeStack = new ArrayDeque<>();
    Deque<Boolean> immediateReferenceStack = new ArrayDeque<>();
    Deque<ACCESS_SPECIFIER> accessSpecifierStack = new ArrayDeque<>();

    // stupid ptr hack for constructors
    Immediate lastObjPtr;

    public DelphiGenerator(String outputFile) throws IOException {
        writer = new IRWriter(outputFile, sm);
    }

    private void addFrame(Frame.Type type, int count, Optional<TypeInfo<Immediate>> ti) {
        Frame<Immediate> newFrame = (ti.isPresent()) ? new Frame<>(type, ti.get()) : new Frame<>(type);
        sm.pushFrame(newFrame);
        immediateCounts.push(new MutableInteger(count));
    }

    private void addFrame(Frame.Type type) {
        addFrame(type, 0, Optional.empty());
    }

    private void addFrame(Frame.Type type, CallableInfo ci) {
        addFrame(type, ci.parameterNames.size() + 1, Optional.empty());
        writer.addFunction(ci);
    }

    private CallableInfo addFrame(TypeInfo<Immediate> ti, CallableInfo ci) {
        var qualifiedCi = new CallableInfo(ci);
        qualifiedCi.name = ti.name + "_" + qualifiedCi.name;
        qualifiedCi.parameterNames.addFirst("Self");
        qualifiedCi.parameterTypes.addFirst(new CLASS("%" + ti.name + "*"));
        addFrame(Frame.Type.OBJECT, qualifiedCi.parameterNames.size() + 1, Optional.of(ti));
        writer.addFunction(qualifiedCi);
        return qualifiedCi;
    }

    private void popFrame() {
        var frameType = sm.top().scopeType;
        if (frameType == Frame.Type.FUNCTION || frameType == Frame.Type.OBJECT) {
            writer.flushFunction();
        }
        immediateCounts.pop();
        sm.popFrame();
    }

    private void addVariable(String identifier, GenericType type, int id) {

        var variable = writer.addVariableDeclaration(type, id);
        if (sm.top().scopeType == Frame.Type.CLASS) {
            variable.id += "_" + accessSpecifierStack.peek().toString();
        }
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
                    case TYPE tp: { switch (tp) {
                        case INT:
                        case INTPTR:
                            formatString += "%d ";
                            type = (readAsReference()) ? type : "i32";
                            break;
    
                        case BOOL:
                        case BOOLPTR:
                            formatString += "%d ";
                            type = (readAsReference()) ? type : "i1";
                            break;
                        
                        case STRING:
                            formatString += "%s ";
                            type = (readAsReference()) ? type : "ptr";
                            break;
                    
                        default:
                            break;
                    }}
                    default: break;
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
            var count = immediateCounts.peek();
            writer.writeln("%" + count.inc() + " = call i32 (ptr, ...) @printf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
            return new Immediate();
        }
        );
        sm.addCallable(new CallableInfo("WriteLn", new ArrayList<>(), true),
        (args) -> {
            String[] out = addFormatString.apply(args, true);
            String formatString = out[0];
            String inputString = out[1];
            var count = immediateCounts.peek();
            writer.writeln("%" + count.inc() + " = call i32 (ptr, ...) @printf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
            return new Immediate();
        }
        );
        sm.addCallable(new CallableInfo("ReadLn", new ArrayList<>(), true),
        (args) -> {
            String[] out = addFormatString.apply(args, false);
            String formatString = out[0];
            String inputString = out[1];
            var count = immediateCounts.peek();
            writer.writeln("%" + count.inc() + " = call i32 (ptr, ...) @scanf(ptr " + formatStrings.get(formatString) + ", " + inputString + ")");
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

    private Immediate registerMethodPrototype(DelphiParser.IdentifierContext nameCtx, DelphiParser.FormalParameterListContext paramCtx) {
        String methodName = getIdentifier(visit(nameCtx));
        String typeName = this.typeStack.peek();
        var ti = this.typeInfo.get(typeName);
        var methodId = createCallableInfo(methodName, paramCtx);
        ti.registerMethod(methodId, (args) -> {
            throw new DelphiError("Method " + typeName + "." + methodName + " not defined.", nameCtx);
        });
        return new Immediate();
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
        var paramTypes = new ArrayList<GenericType>(args.stream().map(arg -> arg.type).toList());
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
    public GeneratorResult visitInterfaceType(DelphiParser.InterfaceTypeContext ctx){
        String interfaceName = this.typeStack.peek();
        var ti = new TypeInfo<Immediate>(TypeInfo.Type.INTERFACE);
        this.typeInfo.put(interfaceName, ti);

        if (ctx.identifier() != null) {
			var anscestor = getIdentifier(visit(ctx.identifier()));
            if (!this.typeInfo.containsKey(anscestor)) {
                throw new DelphiError("No such interface: " + anscestor, ctx);
            }
            var parentInfo = this.typeInfo.get(anscestor);
            if (parentInfo.type != TypeInfo.Type.INTERFACE) {
                throw new DelphiError("Cannot extend non interface type: " + anscestor, ctx);
            }
            ti.parents.add(parentInfo);
        }
        visitChildren(ctx);
        this.typeStack.pop();
        
        return new Immediate();
    }

    @Override
    public GeneratorResult visitClassType(DelphiParser.ClassTypeContext ctx){
        String className = this.typeStack.peek();
        var ti = new TypeInfo<Immediate>(TypeInfo.Type.CLASS, className);
        this.typeInfo.put(className, ti);
        if (ctx.ABSTRACT() != null) {
            ti.inheritanceType = InheritanceType.ABSTRACT;
        }
        else if (ctx.SEALED() != null) {
            ti.inheritanceType = InheritanceType.SEALED;
        }

        if (ctx.interfaces() != null) {
			var interfaces = getIdentifierList(visit(ctx.interfaces()));
            var anscestor = interfaces.get(0);
            if (!this.typeInfo.containsKey(anscestor)) {
                throw new DelphiError("No such class: " + anscestor, ctx);
            }
            var parentInfo = this.typeInfo.get(anscestor);
            if (parentInfo.inheritanceType == InheritanceType.SEALED) {
                throw new DelphiError("Cannot extend sealed class: " + anscestor, ctx);
            }
            ti.parents.add(parentInfo);
            for (int i = 1; i < interfaces.size(); i++) {
                var interfaceName = interfaces.get(i);
                if (!this.typeInfo.containsKey(interfaceName)) {
                    throw new DelphiError("No such interface: " + interfaceName, ctx);
                }
                var currentInterface = this.typeInfo.get(interfaceName);
                if (currentInterface.type != TypeInfo.Type.INTERFACE) {
                    throw new DelphiError("Cannot implement non interface type: " + interfaceName, ctx);
                }
                ti.parents.add(currentInterface);
            }
        }
        
        if (ctx.classDefinition() != null) {
            this.accessSpecifierStack.push(ACCESS_SPECIFIER.PUBLIC);
            addFrame(Frame.Type.CLASS); // abuse scoping to extract all definitions
            visit(ctx.classDefinition());
            for(var entry : sm.top().scope.definedLocal.entrySet()){
                var member = entry.getValue();
                boolean isPrivate = member.id.endsWith(ACCESS_SPECIFIER.PRIVATE.toString());
                int idx = Integer.parseInt(member.id.split("_")[0].substring(1)); // remove access specifier component and strip leading '%'
                idx += ti.getParentAttributeCount();
                member.id = "c" +  idx;
                member.type = GenericType.getValueType(member.type);
                ti.registerAttribute(entry.getKey(), member, isPrivate);
            }
            popFrame();
            this.accessSpecifierStack.pop();
        }
        this.typeStack.pop();
        writer.addClass(ti);
        
        return new Immediate();
    }

    @Override
    public GeneratorResult visitInterfaces(DelphiParser.InterfacesContext ctx) {
        /* 
            have to overload explicitly because visitChildren() in default impl 
            will return null due to terminals at end of rule
        */
        return visit(ctx.identifierList());
    }

    @Override
    public GeneratorResult visitAccessSpecifier(DelphiParser.AccessSpecifierContext ctx) {
        this.accessSpecifierStack.pop(); // remove current access type
        this.accessSpecifierStack.push((ctx.PRIVATE() != null) ? ACCESS_SPECIFIER.PRIVATE : ACCESS_SPECIFIER.PUBLIC);
        return new Immediate();
    }

    @Override
    public GeneratorResult visitProcedurePrototype(DelphiParser.ProcedurePrototypeContext ctx){
        return registerMethodPrototype(ctx.identifier(), ctx.formalParameterList());
    }

    @Override
    public GeneratorResult visitConstructorPrototype(DelphiParser.ConstructorPrototypeContext ctx){
        return registerMethodPrototype(ctx.identifier(), ctx.formalParameterList());
    }

    @Override
    public GeneratorResult visitDestructorPrototype(DelphiParser.DestructorPrototypeContext ctx){
        return registerMethodPrototype(ctx.identifier(), ctx.formalParameterList());
    }

    @Override
    public GeneratorResult visitFunctionPrototype(DelphiParser.FunctionPrototypeContext ctx) {
        return registerMethodPrototype(ctx.identifier(), ctx.formalParameterList());
    }

    @Override
    public GeneratorResult visitProcedureType(DelphiParser.ProcedureTypeContext ctx){
        return registerCallable(ctx.formalParameterList());
    }

    @Override
    public GeneratorResult visitFunctionType(DelphiParser.FunctionTypeContext ctx){
        return registerCallable(ctx.formalParameterList());
    }

    private void createCallableEntrypoint(CallableInfo ci) {
        var count = immediateCounts.peek();
        for (int i = 0; i < ci.parameterNames.size(); i++) {
            var paramName = ci.parameterNames.get(i);
            var paramType = ci.parameterTypes.get(i);
            var param = new Immediate(paramType, "%" + i);
            if ((GenericType.isPtr(paramType))) {
                var alias = writer.addBitCast(param, paramType, count.inc());
                sm.addVariable(paramName, alias);
            }
            else {
                addVariable(paramName, paramType, count.inc());
                writer.addVariableStore(sm.getVariable(paramName).get(), param);
            }
        }
        if (ci.returnType != TYPE.VOID) {
            addVariable("Result", ci.returnType, count.inc());
        }
    }

    private void createCallableReturn(CallableInfo ci) {
        if (ci.returnType == TYPE.VOID) {
            writer.addReturnStatement(new Immediate());
        }
        else {
            var count = immediateCounts.peek();
            var result = writer.addVariableAccess(sm.getVariable("Result").get(), count.inc());
            writer.addReturnStatement(result);
        }
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
        createCallableEntrypoint(procedureId);
        visit(ctx.block());
        createCallableReturn(procedureId);
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
            var count = immediateCounts.peek();
            return writer.addCallableCall(functionId, args, count.inc());
        });

        addFrame(Frame.Type.FUNCTION, functionId);
        createCallableEntrypoint(functionId);
        visit(ctx.block());
        createCallableReturn(functionId);
        popFrame();
        return new Immediate();
    }

    @Override
    public GeneratorResult visitMethodProcedureDeclaration(DelphiParser.MethodProcedureDeclarationContext ctx){
        String className = getIdentifier(visit(ctx.identifier(0)));
        String methodName = getIdentifier(visit(ctx.identifier(1)));
        var tdata = this.typeInfo.get(className);
        var methodId = createCallableInfo(methodName, ctx.formalParameterList());

        if(!tdata.hasMethod(methodId)){
            throw new DelphiError("Method " + methodName + " not declared in class " + className, ctx);
        }

        var qualifiedMethod = addFrame(tdata, methodId);
        tdata.registerMethod(methodId, (args) -> {
            return writer.addCallableCall(qualifiedMethod, args, 0);
        });

        createCallableEntrypoint(qualifiedMethod);
        visit(ctx.block());
        createCallableReturn(qualifiedMethod);
        popFrame();

        return new Immediate();
    }

    @Override
    public GeneratorResult visitMethodFunctionDeclaration(DelphiParser.MethodFunctionDeclarationContext ctx){
        String className = getIdentifier(visit(ctx.identifier(0)));
        String methodName = getIdentifier(visit(ctx.identifier(1)));
        var tdata = this.typeInfo.get(className);
        var methodId = createCallableInfo(methodName, ctx.formalParameterList());
        methodId.returnType = getImmediate(visit(ctx.resultType())).type;

        if(!tdata.hasMethod(methodId)){
            throw new DelphiError("Method " + methodName + " not declared in class " + className, ctx);
        }

        var qualifiedMethod = addFrame(tdata, methodId);
        tdata.registerMethod(methodId, (args) -> {
            var count = immediateCounts.peek();
            return writer.addCallableCall(qualifiedMethod, args, count.inc());
        });

        createCallableEntrypoint(qualifiedMethod);
        visit(ctx.block());
        createCallableReturn(qualifiedMethod);
        popFrame();

        return new Immediate();
    }

    @Override
    public GeneratorResult visitConstructorDeclaration(DelphiParser.ConstructorDeclarationContext ctx){
        String className = getIdentifier(visit(ctx.identifier(0)));
        String constructorName = getIdentifier(visit(ctx.identifier(1)));
        var tdata = this.typeInfo.get(className);
        var constructorId = createCallableInfo(constructorName, ctx.formalParameterList());
        // constructorId.returnType = new CLASS("%" + className + "*");

        if(!tdata.hasMethod(constructorId)){
            throw new DelphiError("Constructor " + constructorName + " not declared in class " + className, ctx);
        }

        var qualifiedConstructor = addFrame(tdata, constructorId);
        tdata.registerMethod(constructorId, (args) -> {
            // var count = immediateCounts.peek();
            // var newObjectPtr = writer.addVariableDeclaration(new CLASS("%" + args.get(0).id), count.inc());
            // args.set(0, newObjectPtr);
            return writer.addCallableCall(qualifiedConstructor, args, 0);
        });

        createCallableEntrypoint(qualifiedConstructor);
        visit(ctx.block());
        // var object = sm.getVariable("Self").get();
        // writer.addVariableStore(sm.getVariable("Result").get(), object);
        createCallableReturn(qualifiedConstructor);
        popFrame();

        return new Immediate();
    }

    @Override
    public GeneratorResult visitDestructorDeclaration(DelphiParser.DestructorDeclarationContext ctx){
        String className = getIdentifier(visit(ctx.identifier(0)));
        String destructorName = getIdentifier(visit(ctx.identifier(1)));
        var tdata = this.typeInfo.get(className);
        var destructorId = createCallableInfo(destructorName, ctx.formalParameterList());

        if(!tdata.hasMethod(destructorId)){
            throw new DelphiError("Destructor " + destructorName + " not declared in class " + className, ctx);
        }

        var qualifiedDestructor = addFrame(tdata, destructorId);
        tdata.registerMethod(destructorId, (args) -> {
            return writer.addCallableCall(qualifiedDestructor, args, 0);
        });

        createCallableEntrypoint(qualifiedDestructor);
        visit(ctx.block());
        var object = sm.getVariable("Self").get();
        writer.addVariableStore(object, new Immediate(TYPE.REFERENCE, "null"));
        createCallableReturn(qualifiedDestructor);
        popFrame();

        return new Immediate();
    }

    @Override
    public GeneratorResult visitAssignmentStatement(DelphiParser.AssignmentStatementContext ctx){
        immediateReferenceStack.push(true);
        var storeTarget = getImmediate(visit(ctx.variable()));
        immediateReferenceStack.pop();
        this.lastObjPtr = storeTarget; // hack to get around ptr memcpy not working
        var value = getImmediate(visit(ctx.expression()));
        if (value.type != TYPE.VOID) { // for now to account for constructor assignment
            writer.addVariableStore(storeTarget, value);
        }
        return storeTarget;
    }

    @Override
    public GeneratorResult visitVariableDeclaration(DelphiParser.VariableDeclarationContext ctx){
        var identifiers = getIdentifierList(visit(ctx.identifierList()));
        GenericType type = getImmediate(visit(ctx.type_())).type;
        var count = immediateCounts.peek();
        for(var identifier : identifiers){
            addVariable(identifier, type, count.inc());
        }
        return new Immediate();
    }

  
    @Override
    public GeneratorResult visitTerm(DelphiParser.TermContext ctx){
        var lhs = getImmediate(visit(ctx.signedFactor())); 
        var op = ctx.multiplicativeoperator(); 
        if(op == null) return lhs; 
        var rhs = getImmediate(visit(ctx.term())); 
        MutableInteger count = immediateCounts.peek();
        if(op.STAR() != null){
            return writer.addBinaryExpression(lhs, rhs, MathOperations.MUL, count.inc()); 
        }
        else if(op.SLASH() != null){
            return writer.addBinaryExpression(lhs, rhs, MathOperations.DIV, count.inc());
        }
    
        return lhs; 

    }
    
    @Override
    public GeneratorResult visitSimpleExpression(DelphiParser.SimpleExpressionContext ctx){
        var lhs = getImmediate(visit(ctx.term()));
        var op = ctx.additiveoperator(); 
        if(op == null) return lhs; 
        var rhs = getImmediate(visit(ctx.simpleExpression())); 
        var count = immediateCounts.peek(); 
        if(op.PLUS() != null){
            return writer.addBinaryExpression(lhs, rhs, MathOperations.ADD, count.inc()); 
        }
        else if(op.MINUS() != null){
            return writer.addBinaryExpression(lhs, rhs, MathOperations.SUB, count.inc()); 
        }
        return lhs; 
    }


    @Override
    public GeneratorResult visitExpression(DelphiParser.ExpressionContext ctx){
        var lhs = getImmediate(visit(ctx.simpleExpression())); 
        var op = ctx.relationaloperator(); 
        if (op == null) return lhs; 
        var rhs = getImmediate(visit(ctx.expression())); 
        var count = immediateCounts.peek(); 

        if(op.EQUAL() != null){
            return writer.addComparisonExpressions(lhs, rhs, CmpOperations.EQ, count.inc());
        }
        else if(op.NOT_EQUAL() != null){
            return writer.addComparisonExpressions(lhs, rhs, CmpOperations.NEQ, count.inc());
        }
        else if(op.LT() != null){
            return writer.addComparisonExpressions(lhs, rhs, CmpOperations.LT, count.inc()); 
        }
        else if(op.LE() != null){
            return writer.addComparisonExpressions(lhs, rhs, CmpOperations.LTE, count.inc()); 
        }
        else if(op.GE() != null){
            return writer.addComparisonExpressions(lhs, rhs, CmpOperations.GTE, count.inc()); 
        }
        else if (op.GT() != null){
            return writer.addComparisonExpressions(lhs, rhs, CmpOperations.GT, count.inc()); 
        }

        return lhs; 
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

    private String getObjectClass(Immediate object) {
        return switch(object.type) {
            case CLASS cp -> cp.name().substring(1, cp.name().length() - 1);
            default -> "";
        };
    }

    @Override
    public GeneratorResult visitMethodCall(DelphiParser.MethodCallContext ctx){
        String variableName = getIdentifier(visit(ctx.identifier(0)));
        String methodName = getIdentifier(visit(ctx.identifier(1)));
		var args = (ctx.parameterList() != null) ? getImmediateList(visit(ctx.parameterList())) : new ArrayList<Immediate>();
        var paramTypes = new ArrayList<GenericType>(args.stream().map(arg -> arg.type).toList());
        var methodId = new CallableInfo(methodName, paramTypes);
        var variable = sm.getVariable(variableName);
        if (this.typeInfo.containsKey(variableName)) { // class method or constructor
            var ti = typeInfo.get(variableName);
            if (!ti.hasMethod(methodId)) {
                throw new DelphiError("No such method named: " + methodName, ctx);
            }
            var method = this.typeInfo.get(variableName).getMethod(methodId);
            args.add(0, this.lastObjPtr); // for now cause the ptr memcpy doesnt work
            return method.apply(args);
        }
        else if (variable.isPresent()) {
            var object = variable.get();
            String type = getObjectClass(object);
            var method = this.typeInfo.get(type).getMethod(methodId);
            args.add(0, object);
            return method.apply(args);
        }
        else {
            throw new DelphiError("Invalid method invocation: " + variableName + "." + methodName, ctx);
        }
    }

    @Override
    public GeneratorResult visitVariable(DelphiParser.VariableContext ctx) {
        BiFunction<Immediate, Immediate, Immediate> accessObjectMember = (object, classMember) -> {
            var count = immediateCounts.peek();
            var member = writer.addMemberAccess(object, classMember, count.inc());
            if (readAsReference()) return member;
            var result = writer.addVariableAccess(member, count.inc());
            return result;
        };
        var variableName = getIdentifier(visit(ctx.identifier(0)));
        var memberName = (ctx.identifier().size() > 1) ? getIdentifier(visit(ctx.identifier(1))) : "";
        var variable = sm.getVariable(variableName);
        var function = sm.getFunction(new CallableInfo(variableName));
        if (variable.isPresent() && memberName.isEmpty()) { // raw variable
            var resolvedVariable = variable.get();
            if (resolvedVariable.id.startsWith("c")) { // class member (omitted Self)
                return accessObjectMember.apply(sm.getVariable("Self").get(), resolvedVariable);
            }
            if (readAsReference()) return resolvedVariable;
            var count = immediateCounts.peek();
            var result = writer.addVariableAccess(resolvedVariable, count.inc());
            return result;
        }
        else if (variable.isPresent() && !memberName.isEmpty()) { // object member or method
            var object = variable.get();
            String type = getObjectClass(object);
            var ti = typeInfo.get(type); 
            var methodId = new CallableInfo(memberName);
            if (ti.hasAttribute(memberName, true)) {
                return accessObjectMember.apply(object, ti.getAttribute(memberName));
            }
            else if (ti.hasMethod(methodId)) {
                return ti.getMethod(methodId).apply(new ArrayList<>(Arrays.asList(object)));
            }
            else {
                throw new DelphiError("Attempting to access a nonexistent or private member: " + memberName, ctx);
            }
        }
        else if (typeInfo.containsKey(variableName) && !memberName.isEmpty()) { // class member or method
            var methodId = new CallableInfo(memberName);
            var ti = typeInfo.get(variableName);
            if (!ti.hasMethod(methodId)) {
                throw new DelphiError("No such method named: " + memberName, ctx);
            }
            return ti.getMethod(methodId).apply(new ArrayList<>(Arrays.asList(this.lastObjPtr))); // for now cause the ptr memcpy doesnt work
        }
        else if (function.isPresent()) { // function application syntax sugar
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
        GenericType type = getImmediate(visit(ctx.typeIdentifier())).type;
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
        if (ctx.identifier() != null) {
            return new Immediate(new CLASS("%" + getIdentifier(visit(ctx.identifier()))), "");
        }
        return new Immediate(TYPE.INT, "");
    }

    @Override
    public GeneratorResult visitSignedFactor(DelphiParser.SignedFactorContext ctx){
        var factor = getImmediate(visit(ctx.factor()));
        if(ctx.MINUS() != null) {
            var count = immediateCounts.peek();
            return writer.addBinaryExpression(new Immediate(TYPE.INT, "0"), factor, MathOperations.SUB, count.inc());
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
