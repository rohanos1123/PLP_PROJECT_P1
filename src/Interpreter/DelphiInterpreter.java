package Interpreter;

import org.antlr.v4.runtime.ParserRuleContext;
import grammar.DelphiBaseVisitor;
import grammar.DelphiParser;
import Interpreter.TypeInfo.InheritanceType;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DelphiInterpreter extends DelphiBaseVisitor<Value> {


    ScopeManager sm = new ScopeManager();
    HashMap<String, TypeInfo> typeInfo = new HashMap<>();

    // Keeps track of information between visitors
    boolean isPrivate = false;
    String currentType = "";

    private static String createLogMsg(Object msg, ParserRuleContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return "Line: " + line + ", Column: " + column + ": " + msg.toString();
    }

	@SuppressWarnings("unused")
	private static void log(Object msg, ParserRuleContext ctx) {
        System.err.println(createLogMsg(msg, ctx));
    }
    
    public static class delphiRuntimeError extends RuntimeException {
        public delphiRuntimeError(Object msg, ParserRuleContext ctx) {
            super(createLogMsg(msg, ctx));
        }
    }

    private static class ControlChange extends delphiRuntimeError {
        enum TYPE {
            BREAK,
            CONTINUE
        };

        public TYPE type;

        public ControlChange(TYPE type, Object msg, ParserRuleContext ctx) {
            this.type = type;
            super(msg, ctx);
        }
    }

    /* helpers */

    private void setupBuiltinCallables() {
        sm.addCallable(new CallableInfo("WriteLn", new ArrayList<>(), true),
        (args) -> {
            for(var arg : args){
                System.out.print(arg.asString());
            }
            System.out.println();
            return new Value(0);
        });
        sm.addCallable(new CallableInfo("ReadLn", new ArrayList<>(), true),
        (args) -> {
            Scanner sc = new Scanner(System.in);
            Value obj = args.get(0);
            int val = sc.nextInt();
            obj.copyValue(new Value(val, TYPE.INT));
            sc.close();
            return new Value(0);
        });
    }

    private CallableInfo createCallableInfo(String callableName, DelphiParser.FormalParameterListContext paramCtx) {
        var callableId = new CallableInfo(callableName);
        if (paramCtx != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(paramCtx).value;
            for (var param : params) {
                callableId.parameterNames.add((String)param.value);
                callableId.parameterTypes.add(param.type);
            }
        }
        return callableId;
    }

    private Value registerMethodPrototype(DelphiParser.IdentifierContext nameCtx, DelphiParser.FormalParameterListContext paramCtx) {
        String methodName = (String)visit(nameCtx).value;
        var ti = this.typeInfo.get(this.currentType);
        var methodId = createCallableInfo(methodName, paramCtx);
        ti.registerMethod(methodId, (args) -> {
            throw new delphiRuntimeError("Method " + this.currentType + "." + methodName + " not defined.", nameCtx);
        });
        return new Value(0);
    }

    private Value registerCallable(DelphiParser.FormalParameterListContext paramCtx) {
        String callableName = this.currentType;
        var functionId = createCallableInfo(callableName, paramCtx);
        sm.addCallable(functionId, (args) -> {
            throw new delphiRuntimeError("No definition for: " + callableName, paramCtx);
        });
        return new Value(0);
    }

    private Value executeCallable(String functionName, DelphiParser.ParameterListContext paramCtx) {
        @SuppressWarnings("unchecked")
        var args = (paramCtx != null) ? (ArrayList<Value>) visit(paramCtx).value : new ArrayList<Value>();
        var paramTypes = new ArrayList<TYPE>(args.stream().map(arg -> arg.type).toList());
        var callableId = new CallableInfo(functionName, paramTypes);


        if (sm.getFunction(callableId).isEmpty()) {
            // try again for variadic functions
            callableId.variadic = true;
            if (sm.getFunction(callableId).isEmpty()) {
                throw new delphiRuntimeError(functionName + " not found.", paramCtx);
            }
        }
        return sm.getFunction(callableId).get().apply(args);
    }

    /* visitor implementation */

    @Override
    public Value visitProgram(DelphiParser.ProgramContext ctx){
        setupBuiltinCallables();
        visit(ctx.topLevelBlock());
        return new Value(0);
    }

    @Override
    public Value visitExpandedTypeDefinition(DelphiParser.ExpandedTypeDefinitionContext ctx){
        String typeName = (String)visit(ctx.identifier()).value;
        this.currentType = typeName;
        return visitChildren(ctx);
    }

    @Override
    public Value visitTypeDefinition(DelphiParser.TypeDefinitionContext ctx){
        String typeName = (String)visit(ctx.identifier()).value;
        this.currentType = typeName;
        return visitChildren(ctx);
    }

    @Override
    public Value visitInterfaceType(DelphiParser.InterfaceTypeContext ctx){
        TypeInfo ti = new TypeInfo(TypeInfo.Type.INTERFACE);
        this.typeInfo.put(this.currentType, ti);

        if (ctx.identifier() != null) {
			var anscestor = (String) visit(ctx.identifier()).value;
            if (!this.typeInfo.containsKey(anscestor)) {
                throw new delphiRuntimeError("No such interface: " + anscestor, ctx);
            }
            var parentInfo = this.typeInfo.get(anscestor);
            if (parentInfo.type != TypeInfo.Type.INTERFACE) {
                throw new delphiRuntimeError("Cannot extend non interface type: " + anscestor, ctx);
            }
            ti.parents.add(parentInfo);
        }
        visitChildren(ctx);
        
        return new Value(0);
    }

    @Override
    public Value visitClassType(DelphiParser.ClassTypeContext ctx){
        TypeInfo ti = new TypeInfo(TypeInfo.Type.CLASS);
        this.typeInfo.put(this.currentType, ti);
        if (ctx.ABSTRACT() != null) {
            ti.inheritanceType = InheritanceType.ABSTRACT;
        }
        else if (ctx.SEALED() != null) {
            ti.inheritanceType = InheritanceType.SEALED;
        }

        if (ctx.interfaces() != null) {
            @SuppressWarnings("unchecked")
			var interfaces = (ArrayList<String>) visit(ctx.interfaces()).value;
            var anscestor = interfaces.get(0);
            if (!this.typeInfo.containsKey(anscestor)) {
                throw new delphiRuntimeError("No such class: " + anscestor, ctx);
            }
            var parentInfo = this.typeInfo.get(anscestor);
            if (parentInfo.inheritanceType == InheritanceType.SEALED) {
                throw new delphiRuntimeError("Cannot extend sealed class: " + anscestor, ctx);
            }
            ti.parents.add(parentInfo);
            for (int i = 1; i < interfaces.size(); i++) {
                var interfaceName = interfaces.get(i);
                if (!this.typeInfo.containsKey(interfaceName)) {
                    throw new delphiRuntimeError("No such interface: " + interfaceName, ctx);
                }
                var currentInterface = this.typeInfo.get(interfaceName);
                if (currentInterface.type != TypeInfo.Type.INTERFACE) {
                    throw new delphiRuntimeError("Cannot implement non interface type: " + interfaceName, ctx);
                }
                ti.parents.add(currentInterface);
            }
        }
        
        if (ctx.classDefinition() != null) {
            this.isPrivate = false;
            sm.pushFrame(new Frame(Frame.Type.OBJECT)); // abuse scoping to extract all definitions
            visit(ctx.classDefinition());
            for(var entry : sm.top().memory.entrySet()){
                ti.registerAttribute(entry.getKey(), entry.getValue());
            }
            sm.popFrame();
            this.isPrivate = false;
        }
        
        return new Value(0);
    }

    @Override
    public Value visitProcedureType(DelphiParser.ProcedureTypeContext ctx){
        return registerCallable(ctx.formalParameterList());
    }

    @Override
    public Value visitFunctionType(DelphiParser.FunctionTypeContext ctx){
        return registerCallable(ctx.formalParameterList());
    }

    @Override
    public Value visitInterfaces(DelphiParser.InterfacesContext ctx) {
        /* 
            have to overload explicitly because visitChildren() in default impl 
            will return null due to terminals at end of rule
        */
        return visit(ctx.identifierList());
    }

    @Override
    public Value visitAccessSpecifier(DelphiParser.AccessSpecifierContext ctx){
        this.isPrivate = ctx.PRIVATE() != null;
        return new Value(0);
    }

    @Override
    public Value visitProcedurePrototype(DelphiParser.ProcedurePrototypeContext ctx){
        return registerMethodPrototype(ctx.identifier(), ctx.formalParameterList());
    }

    @Override
    public Value visitConstructorPrototype(DelphiParser.ConstructorPrototypeContext ctx){
        return registerMethodPrototype(ctx.identifier(), ctx.formalParameterList());
    }

    @Override
    public Value visitDestructorPrototype(DelphiParser.DestructorPrototypeContext ctx){
        return registerMethodPrototype(ctx.identifier(), ctx.formalParameterList());
    }

    @Override
    public Value visitFunctionPrototype(DelphiParser.FunctionPrototypeContext ctx) {
        return registerMethodPrototype(ctx.identifier(), ctx.formalParameterList());
    }

    @Override
    public Value visitFormalParameterList(DelphiParser.FormalParameterListContext ctx) {
        var paramGroups = ctx.formalParameterSection();
        ArrayList<Value> params = new ArrayList<>();
        for(int i = 0 ; i < paramGroups.size(); i++){
            Value paramGroup = visit(paramGroups.get(i));
            @SuppressWarnings("unchecked")
			var groupVals = (ArrayList<String>) paramGroup.value;
            for (var param : groupVals) {
                params.add(new Value(param, paramGroup.type));
            }
        }
        return new Value(params);
    }

	@Override public Value visitParameterGroup(DelphiParser.ParameterGroupContext ctx) {
        var params = visit(ctx.identifierList()).value;
        TYPE type = visit(ctx.typeIdentifier()).type;
        return new Value(params, type);
    }

    @Override
    public Value visitProcedureDeclaration(DelphiParser.ProcedureDeclarationContext ctx){
        String procedureName = (String)visit(ctx.identifier()).value;
        var procedureId = createCallableInfo(procedureName, ctx.formalParameterList());
        /* might need to eventually check for redefinition dunno if its allowed */

        sm.addCallable(procedureId, (args) -> {
            sm.pushFrame(new Frame(Frame.Type.FUNCTION), procedureId);
            for (int i = 0; i < args.size(); i++) {
                sm.addVariable(procedureId.parameterNames.get(i), new Value(args.get(i)));
            }
            visit(ctx.block());
            sm.popFrame();
            return new Value(0);
        });

        return new Value(0);
    }

    @Override
    public Value visitFunctionDeclaration(DelphiParser.FunctionDeclarationContext ctx){
        String functionName = (String)visit(ctx.identifier()).value;
        var functionId = createCallableInfo(functionName, ctx.formalParameterList());
        /* might need to eventually check for redefinition dunno if its allowed */

        sm.addCallable(functionId, (args) -> {
            sm.pushFrame(new Frame(Frame.Type.FUNCTION), functionId);
            for (int i = 0; i < args.size(); i++) {
                sm.addVariable(functionId.parameterNames.get(i), new Value(args.get(i)));
            }
            var result = new Value(0);
            sm.addVariable("Result", result);
            visit(ctx.block());
            sm.popFrame();
            return result;
        });

        return new Value(0);
    }

    @Override
    public Value visitMethodProcedureDeclaration(DelphiParser.MethodProcedureDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String methodName = (String)visit(ctx.identifier(1)).value;
        TypeInfo tdata = this.typeInfo.get(className);
        var methodId = createCallableInfo(methodName, ctx.formalParameterList());

        if(!tdata.hasMethod(methodId)){
            throw new delphiRuntimeError("Method " + methodName + " not declared in class " + className, ctx);
        }

        tdata.registerMethod(methodId, (args) -> {
            var invokerObject = args.remove(0);
            var ti = this.typeInfo.get(((DelphiObject)invokerObject.value).type);
            sm.pushFrame(new Frame(Frame.Type.OBJECT, ti));
            sm.addVariable("Self", invokerObject);
            for (int i = 0; i < args.size(); i++) {
                sm.addVariable(methodId.parameterNames.get(i), new Value(args.get(i)));
            }
            visit(ctx.block());
            sm.popFrame();
            return new Value(0);
        });

        return new Value(0);
    }

    @Override
    public Value visitMethodFunctionDeclaration(DelphiParser.MethodFunctionDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String methodName = (String)visit(ctx.identifier(1)).value;
        TypeInfo tdata = this.typeInfo.get(className);
        var methodId = createCallableInfo(methodName, ctx.formalParameterList());

        if(!tdata.hasMethod(methodId)){
            throw new delphiRuntimeError("Method " + methodName + " not declared in class " + className, ctx);
        }

        tdata.registerMethod(methodId, (args) -> {
            var invokerObject = args.remove(0);
            var ti = this.typeInfo.get(((DelphiObject)invokerObject.value).type);
            sm.pushFrame(new Frame(Frame.Type.OBJECT, ti));
            sm.addVariable("Self", invokerObject);
            for (int i = 0; i < args.size(); i++) {
                sm.addVariable(methodId.parameterNames.get(i), new Value(args.get(i)));
            }
            var result = new Value(0);
            sm.addVariable("Result", result);
            visit(ctx.block());
            sm.popFrame();
            return result;
        });

        return new Value(0);
    }

    @Override
    public Value visitConstructorDeclaration(DelphiParser.ConstructorDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String constructorName = (String)visit(ctx.identifier(1)).value;
        TypeInfo tdata = this.typeInfo.get(className);
        var constructorId = createCallableInfo(constructorName, ctx.formalParameterList());

        if(!tdata.hasMethod(constructorId)){
            throw new delphiRuntimeError("Constructor " + constructorName + " not declared in class " + className, ctx);
        }

        tdata.registerMethod(constructorId, (args) -> {
            var invokerClass = (String)args.remove(0).value;
            var ti = this.typeInfo.get(invokerClass);
            sm.pushFrame(new Frame(Frame.Type.OBJECT, ti));
            sm.addVariable("Self", new Value(new DelphiObject(invokerClass, ti.getAttributes()), TYPE.REFERENCE));
            for (int i = 0; i < args.size(); i++) {
                sm.addVariable(constructorId.parameterNames.get(i), new Value(args.get(i)));
            }
            visit(ctx.block());
            var object = sm.getVariable("Self").get();
            sm.popFrame();
            return object;
        });

        return new Value(0);
    }

    @Override
    public Value visitDestructorDeclaration(DelphiParser.DestructorDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String destructorName = (String)visit(ctx.identifier(1)).value;
        TypeInfo tdata = this.typeInfo.get(className);
        var destructorId = createCallableInfo(destructorName, ctx.formalParameterList());

        if(!tdata.hasMethod(destructorId)){
            throw new delphiRuntimeError("Destructor " + destructorName + " not declared in class " + className, ctx);
        }

        tdata.registerMethod(destructorId, (args) -> {
            var invokerObject = args.remove(0);
            var ti = this.typeInfo.get(((DelphiObject)invokerObject.value).type);
            sm.pushFrame(new Frame(Frame.Type.OBJECT, ti));
            sm.addVariable("Self", invokerObject);
            for (int i = 0; i < args.size(); i++) {
                sm.addVariable(destructorId.parameterNames.get(i), new Value(args.get(i)));
            }
            visit(ctx.block());
            var object = sm.getVariable("Self").get();
            object.type = TYPE.VOID;
            sm.popFrame();
            return object;
        });

        return new Value(0);
    }

    @Override
    public Value visitVariableDeclaration(DelphiParser.VariableDeclarationContext ctx){
        // Get the type
        @SuppressWarnings("unchecked")
        var identifiers = (ArrayList<String>)visit(ctx.identifierList()).value;
        for(var identifier : identifiers){
            sm.addVariable(identifier, new Value(0, TYPE.INT, identifier, this.isPrivate));
        }
        return new Value(0);
    }

    @Override
    public Value visitAssignmentStatement(DelphiParser.AssignmentStatementContext ctx){
        Value writeTo = visit(ctx.variable());
        Value v = visit(ctx.expression());
        writeTo.copyValue(v);
        return writeTo;
    }

    @Override
    public Value visitProcedureStatement(DelphiParser.ProcedureStatementContext ctx){
        var procedureName = (String)visit(ctx.identifier()).value;
        return executeCallable(procedureName, ctx.parameterList());
    }

    @Override
    public Value visitWhileStatement(DelphiParser.WhileStatementContext ctx){
        sm.pushFrame(new Frame(Frame.Type.LOCAL));
        while ((boolean)visit(ctx.expression()).value) {
            try {
                visit(ctx.statement());
            }
            catch (ControlChange ctl) {
                switch (ctl.type) {
                    case CONTINUE:
                        continue;
                
                    case BREAK:
                        return new Value(0);
                }
            }
        }
        sm.popFrame();
        return new Value(0);
    }

    @Override
    public Value visitForStatement(DelphiParser.ForStatementContext ctx){
        var counter = visit(ctx.variable());
        var forList = ctx.forList();
        counter.value = visit(forList.initialValue()).value;
        var finalValue = (Integer)visit(forList.finalValue()).value;
        Function<Integer, Integer> op = (val) -> val + 1;
        BiFunction<Integer, Integer, Boolean> check = (lhs, rhs) -> lhs <= rhs;
        if (forList.DOWNTO() != null) {
            op = (val) -> val - 1;
            check = (lhs, rhs) -> lhs >= rhs;
        }

        sm.pushFrame(new Frame(Frame.Type.LOCAL));
        while (check.apply((Integer)counter.value, finalValue)) {
            try {
                visit(ctx.statement());
                counter.value = op.apply((Integer)counter.value);
            }
            catch (ControlChange ctl) {
                switch (ctl.type) {
                    case CONTINUE:
                        continue;
                
                    case BREAK:
                        return new Value(0);
                }
            }
        }
        sm.popFrame();
        return new Value(0);
    }

    @Override
    public Value visitControlStatement(DelphiParser.ControlStatementContext ctx){
        ControlChange.TYPE type = (ctx.BREAK() != null) ? ControlChange.TYPE.BREAK : ControlChange.TYPE.CONTINUE;
        throw new ControlChange(type, "Invalid location for control statement.", ctx);
    }

    @Override
    public Value visitFunctionDesignator(DelphiParser.FunctionDesignatorContext ctx){
        var functionName = (String)visit(ctx.identifier()).value;
        return executeCallable(functionName, ctx.parameterList());
    }

    @Override public Value visitParameterList(DelphiParser.ParameterListContext ctx) {
        var resolvedParams = new ArrayList<Value>();
        for (var param : ctx.actualParameter()) {
            resolvedParams.add(visit(param));
        }
        return new Value(resolvedParams);
    }

    @Override
    public Value visitMethodCall(DelphiParser.MethodCallContext ctx){
        String variableName = (String)visit(ctx.identifier(0)).value;
        String methodName = (String)visit(ctx.identifier(1)).value;
        @SuppressWarnings("unchecked")
		var args = (ctx.parameterList() != null) ? (ArrayList<Value>) visit(ctx.parameterList()).value : new ArrayList<Value>();
        var paramTypes = new ArrayList<TYPE>(args.stream().map(arg -> arg.type).toList());
        var methodId = new CallableInfo(methodName, paramTypes);
        var variable = sm.getVariable(variableName);
        if (this.typeInfo.containsKey(variableName)) { // class method or constructor
            var method = this.typeInfo.get(variableName).getMethod(methodId);
            args.add(0, new Value(variableName));
            return method.apply(args);
        }
        else if (variable.isPresent()) {
            var resolvedVar = variable.get();
            var object = (DelphiObject)resolvedVar.value;
            var method = this.typeInfo.get(object.type).getMethod(methodId);
            args.add(0, resolvedVar);
            return method.apply(args);
        }
        else {
            throw new delphiRuntimeError("Invalid method invocation: " + variableName + "." + methodName, ctx);
        }
    }

    @Override
    public Value visitSignedFactor(DelphiParser.SignedFactorContext ctx){
        Value pureValue = visit(ctx.factor());
        if(ctx.MINUS() != null) {
            pureValue.value = -(pureValue.asInteger());
        }
        return pureValue;
    }

    @Override
    public Value visitUnsignedInteger(DelphiParser.UnsignedIntegerContext ctx){
        return new Value(Integer.parseInt(ctx.NUM_INT().toString()), TYPE.INT);
    }

    @Override
    public Value visitBool_(DelphiParser.Bool_Context ctx){
        return new Value(ctx.TRUE() != null, TYPE.BOOL);
    }

	@Override public Value visitVariable(DelphiParser.VariableContext ctx) {
        String variableName = (String)visit(ctx.identifier(0)).value;
        String memberName = (ctx.identifier().size() > 1) ? (String)visit(ctx.identifier(1)).value : "";  
        var variable = sm.getVariable(variableName);
        var function = sm.getFunction(new CallableInfo(variableName));
        if (variable.isPresent()) {
            var resolvedVar = variable.get();
            if (resolvedVar.type == TYPE.VOID && !(ctx.getParent() instanceof DelphiParser.AssignmentStatementContext)) {
                throw new delphiRuntimeError(variableName + " is uninitialized.", ctx);
            }
            if (!memberName.isEmpty()) {
                var object = (DelphiObject)resolvedVar.value;
                var ti = this.typeInfo.get(object.type);
                var methodId = new CallableInfo(memberName);
                if (object.hasAttribute(memberName)) {
                    var attribute = object.getAttribute(memberName);
                    if (attribute.isPrivate()) {
                        throw new delphiRuntimeError("Attempting to access a private member: " + memberName, ctx);
                    }
                    return attribute;
                }
                else if (ti.hasMethod(methodId)) {
                    return ti.getMethod(methodId).apply(new ArrayList<Value>(Arrays.asList(resolvedVar)));
                }
                else {
                    throw new delphiRuntimeError("No such attribute named: " + memberName, ctx);
                }
            }
            return resolvedVar;
        }
        else if (this.typeInfo.containsKey(variableName)) { // class method or constructor
            var methodId = new CallableInfo(memberName);
            var ti = this.typeInfo.get(variableName);
            if (!ti.hasMethod(methodId)) {
                throw new delphiRuntimeError("No such method named: " + memberName, ctx);
            }
            return ti.getMethod(methodId).apply(new ArrayList<Value>(Arrays.asList(new Value(variableName))));
        }
        else if (function.isPresent()) {
            return function.get().apply(new ArrayList<>());
        }
        else {
            throw new delphiRuntimeError("Identifier " + variableName + " not found in context", ctx);
        }
    }

	@Override public Value visitIdentifierList(DelphiParser.IdentifierListContext ctx) {
        ArrayList<String> identifiers = new ArrayList<>();
        for (var identifier : ctx.identifier()) {
            identifiers.add((String) visit(identifier).value);
        }
        return new Value(identifiers, TYPE.ARRAY);
    }

    @Override
    public Value visitIdentifier(DelphiParser.IdentifierContext ctx){
        return new Value(ctx.IDENT().toString(), TYPE.STRING);
    }

	@Override public Value visitTypeIdentifier(DelphiParser.TypeIdentifierContext ctx) {
        return new Value(0, TYPE.INT);
    }

}

