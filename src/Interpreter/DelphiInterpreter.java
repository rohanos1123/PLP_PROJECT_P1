package Interpreter;

import org.antlr.v4.runtime.ParserRuleContext;
import Grammar.DelphiBaseVisitor;
import Grammar.DelphiParser;
import Interpreter.TypeInfo.InheritanceType;

import java.util.*;
import java.util.function.Function;

public class DelphiInterpreter extends DelphiBaseVisitor<Value> {

    Stack<HashMap<String, Value>> memory = new Stack<HashMap<String, Value>>();
    HashMap<CallableInfo, Function<ArrayList<Value>, Value>> callables = new HashMap<>();
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
    
    public class DelphiRuntimeError extends RuntimeException {
        public DelphiRuntimeError(Object msg, ParserRuleContext ctx) {
            super(createLogMsg(msg, ctx));
        }
    }
    private void setupBuiltinCallables() {
        callables.put(new CallableInfo("WriteLn", new ArrayList<>(), true),
        (args) -> {
            for(var arg : args){
                System.out.print(arg.asString());
            }
            System.out.println();
            return new Value(0);
        });
        callables.put(new CallableInfo("ReadLn", new ArrayList<>(), true),
        (args) -> {
            Scanner sc = new Scanner(System.in);
            Value obj = args.get(0);
            int val = sc.nextInt();
            obj.copyValue(new Value(val, TYPE.INT));
            sc.close();
            return new Value(0);
        });
    }

    @Override
    public Value visitProgram(DelphiParser.ProgramContext ctx){
        setupBuiltinCallables();
        // push the initial Stack Frame
        this.memory.push(new HashMap<>());
        visit(ctx.topLevelBlock());
        return new Value(0);
    }

    @Override
    public Value visitExpandedTypeDefinition(DelphiParser.ExpandedTypeDefinitionContext ctx){
        String className = (String)visit(ctx.identifier()).value;
        this.currentType = className;
        return visitChildren(ctx);
    }

    @Override
    public Value visitInterfaceType(DelphiParser.InterfaceTypeContext ctx){
        TypeInfo ti = new TypeInfo(TypeInfo.Type.INTERFACE);
        this.typeInfo.put(this.currentType, ti);

        if (ctx.identifier() != null) {
			var anscestor = (String) visit(ctx.identifier()).value;
            if (!this.typeInfo.containsKey(anscestor)) {
                throw new DelphiRuntimeError("No such interface: " + anscestor, ctx);
            }
            var parentInfo = this.typeInfo.get(anscestor);
            if (parentInfo.type != TypeInfo.Type.INTERFACE) {
                throw new DelphiRuntimeError("Cannot extend non interface type: " + anscestor, ctx);
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
                throw new DelphiRuntimeError("No such class: " + anscestor, ctx);
            }
            var parentInfo = this.typeInfo.get(anscestor);
            if (parentInfo.inheritanceType == InheritanceType.SEALED) {
                throw new DelphiRuntimeError("Cannot extend sealed class: " + anscestor, ctx);
            }
            ti.parents.add(parentInfo);
            for (int i = 1; i < interfaces.size(); i++) {
                var interfaceName = interfaces.get(i);
                if (!this.typeInfo.containsKey(interfaceName)) {
                    throw new DelphiRuntimeError("No such interface: " + interfaceName, ctx);
                }
                var currentInterface = this.typeInfo.get(interfaceName);
                if (currentInterface.type != TypeInfo.Type.INTERFACE) {
                    throw new DelphiRuntimeError("Cannot implement non interface type: " + interfaceName, ctx);
                }
                ti.parents.add(currentInterface);
            }
        }
        
        if (ctx.classDefinition() != null) {
            this.isPrivate = false;
            this.memory.push(new HashMap<>());
            visit(ctx.classDefinition());
            for(var entry : this.memory.peek().entrySet()){
                ti.registerAttribute(entry.getKey(), entry.getValue());
            }
            this.memory.pop();
            this.isPrivate = false;
        }
        
        return new Value(0);
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
        String procedureName = (String)visit(ctx.identifier()).value;
        var ti = this.typeInfo.get(this.currentType);
        var procedureId = new CallableInfo(procedureName);
        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                procedureId.parameterNames.add((String)param.value);
                procedureId.parameterTypes.add(param.type);
            }
        }
        ti.registerMethod(procedureId);
        return new Value(0);
    }

    @Override
    public Value visitConstructorPrototype(DelphiParser.ConstructorPrototypeContext ctx){
        String constructorName = (String)visit(ctx.identifier()).value;
        var ti = this.typeInfo.get(this.currentType);
        var constructorId = new CallableInfo(constructorName);
        constructorId.returnType = TYPE.REFERENCE;
        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                constructorId.parameterNames.add((String)param.value);
                constructorId.parameterTypes.add(param.type);
            }
        }
        ti.registerMethod(constructorId);
        return new Value(0);
    }

    @Override
    public Value visitDestructorPrototype(DelphiParser.DestructorPrototypeContext ctx){
        String destructorName = (String)visit(ctx.identifier()).value;
        var ti = this.typeInfo.get(this.currentType);
        var destructorId = new CallableInfo(destructorName);
        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                destructorId.parameterNames.add((String)param.value);
                destructorId.parameterTypes.add(param.type);
            }
        }
        ti.registerMethod(destructorId);
        return new Value(0);
    }

    @Override
    public Value visitFunctionPrototype(DelphiParser.FunctionPrototypeContext ctx) {
        String functionName = (String)visit(ctx.identifier()).value;
        var ti = this.typeInfo.get(this.currentType);
        var procedureId = new CallableInfo(functionName);
        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                procedureId.parameterNames.add((String)param.value);
                procedureId.parameterTypes.add(param.type);
            }
        }
        procedureId.returnType = visit(ctx.resultType()).type;
        ti.registerMethod(procedureId);
        return new Value(0);
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
    public Value visitMethodDeclaration(DelphiParser.MethodDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String methodName = (String)visit(ctx.identifier(1)).value;
        TypeInfo tdata = this.typeInfo.get(className);

        var methodId = new CallableInfo(methodName);

        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                methodId.parameterNames.add((String)param.value);
                methodId.parameterTypes.add(param.type);
            }
        }

        if(!tdata.hasMethod(methodId)){
            throw new DelphiRuntimeError("Method " + methodName + " not declared in class " + className, ctx);
        }

        tdata.registerMethod(methodId, (args) -> {
            HashMap<String, Value> frame = new HashMap<>();
            for (int i = 1; i < args.size(); i++) {
                // add index offset to account for object parameter
                frame.put(methodId.parameterNames.get(i-1), args.get(i));
            }
            var objectAttributes = ((DelphiObject) args.get(0).value).attributeMap;
            objectAttributes.forEach((attrName, attrVal) -> frame.put(attrName, attrVal));
            memory.add(frame);
            visit(ctx.block());
            objectAttributes.forEach((attrName, attrVal) -> objectAttributes.put(attrName, frame.get(attrName)));
            memory.pop();
            return new Value(0);
        });

        return new Value(0);
    }

    @Override
    public Value visitConstructorDeclaration(DelphiParser.ConstructorDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String constructorName = (String)visit(ctx.identifier(1)).value;
        TypeInfo tdata = this.typeInfo.get(className);

        var constructorId = new CallableInfo(constructorName);

        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                constructorId.parameterNames.add((String)param.value);
                constructorId.parameterTypes.add(param.type);
            }
        }
        if(!tdata.hasMethod(constructorId)){
            throw new DelphiRuntimeError("Constructor " + constructorName + " not declared in class " + className, ctx);
        }

        tdata.registerMethod(constructorId, (args) -> {
            HashMap<String, Value> frame = new HashMap<>();
            var invokerClass = (String)args.get(0).value;
            var objectAttributes = tdata.getAttributes();
            for (int i = 1; i < args.size(); i++) {
                // add index offset to account for class parameter
                frame.put(constructorId.parameterNames.get(i-1), args.get(i));
            }
            objectAttributes.forEach((attrName, attrVal) -> frame.put(attrName, attrVal));
            memory.add(frame);
            visit(ctx.block());
            objectAttributes.forEach((attrName, attrVal) -> objectAttributes.put(attrName, frame.get(attrName)));
            var object = new DelphiObject(invokerClass, objectAttributes);
            memory.pop();
            return new Value(object, TYPE.REFERENCE);
        });

        return new Value(0);
    }

    @Override
    public Value visitDestructorDeclaration(DelphiParser.DestructorDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String destructorName = (String)visit(ctx.identifier(1)).value;
        TypeInfo tdata = this.typeInfo.get(className);

        var destructorId = new CallableInfo(destructorName);

        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                destructorId.parameterNames.add((String)param.value);
                destructorId.parameterTypes.add(param.type);
            }
        }

        if(!tdata.hasMethod(destructorId)){
            throw new DelphiRuntimeError("Destructor " + destructorName + " not declared in class " + className, ctx);
        }

        tdata.registerMethod(destructorId, (args) -> {
            HashMap<String, Value> frame = new HashMap<>();
            var object = args.get(0);
            var resolvedObject = (DelphiObject) object.value;
            var objectAttributes = resolvedObject.attributeMap;
            for (int i = 1; i < args.size(); i++) {
                // add index offset to account for object parameter
                frame.put(destructorId.parameterNames.get(i-1), args.get(i));
            }
            objectAttributes.forEach((attrName, attrVal) -> frame.put(attrName, attrVal));
            memory.add(frame);
            visit(ctx.block());
            memory.pop();
            memory.peek().get(object.identifier).type = TYPE.VOID;
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
            this.memory.peek().put(identifier, new Value(0, TYPE.INT, identifier, this.isPrivate));
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
        @SuppressWarnings("unchecked")
        var args = (ctx.parameterList() != null) ? (ArrayList<Value>) visit(ctx.parameterList()).value : new ArrayList<Value>();
        var paramTypes = new ArrayList<TYPE>(args.stream().map(arg -> arg.type).toList());
        var procedureId = new CallableInfo(procedureName, paramTypes);
        if (!callables.containsKey(procedureId)) {
            // try again for variadic functions
            procedureId.variadic = true;
            if (!callables.containsKey(procedureId)) {
                throw new DelphiRuntimeError(procedureName + " not found.", ctx);
            }
        }
        return callables.get(procedureId).apply(args);
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
        var topVariables = this.memory.peek();
        @SuppressWarnings("unchecked")
		var args = (ctx.parameterList() != null) ? (ArrayList<Value>) visit(ctx.parameterList()).value : new ArrayList<Value>();
        var paramTypes = new ArrayList<TYPE>(args.stream().map(arg -> arg.type).toList());
        var methodId = new CallableInfo(methodName, paramTypes);
        if (this.typeInfo.containsKey(variableName)) { // class method or constructor
            var method = this.typeInfo.get(variableName).getMethod(methodId);
            args.add(0, new Value(variableName));
            return method.apply(args);
        }
        else if (topVariables.containsKey(variableName)) {
            var variable = topVariables.get(variableName);
            var object = (DelphiObject)variable.value;
            var method = this.typeInfo.get(object.type).getMethod(methodId);
            args.add(0, variable);
            return method.apply(args);
        }
        else {
            throw new DelphiRuntimeError("Invalid method invocation: " + variableName + "." + methodName, ctx);
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

	@Override public Value visitVariable(DelphiParser.VariableContext ctx) {
        String variableName = (String)visit(ctx.identifier(0)).value;
        String memberName = (ctx.identifier().size() > 1) ? (String)visit(ctx.identifier(1)).value : "";  
        var topVariables = this.memory.peek();
        if (topVariables.containsKey(variableName)) {
            var variable = topVariables.get(variableName);
            if (variable.type == TYPE.VOID && !(ctx.getParent() instanceof DelphiParser.AssignmentStatementContext)) {
                throw new DelphiRuntimeError(variableName + " is uninitialized.", ctx);
            }
            if (!memberName.isEmpty()) {
                var object = (DelphiObject)variable.value;
                var ti = this.typeInfo.get(object.type);
                var methodId = new CallableInfo(memberName);
                if (ti.hasAttribute(memberName)) {
                    var attribute = ti.getAttribute(memberName);
                    if (attribute.isPrivate()) {
                        throw new DelphiRuntimeError("Attempting to access a private member: " + memberName, ctx);
                    }
                    return attribute;
                }
                else if (ti.hasMethod(methodId)) {
                    return ti.getMethod(methodId).apply(new ArrayList<Value>(Arrays.asList(variable)));
                }
                else {
                    throw new DelphiRuntimeError("No such attribute named: " + memberName, ctx);
                }
            }
            return variable;
        }
        else if (this.typeInfo.containsKey(variableName)) { // class method or constructor
            var methodId = new CallableInfo(memberName);
            var ti = this.typeInfo.get(variableName);
            if (!ti.hasMethod(methodId)) {
                throw new DelphiRuntimeError("No such method named: " + memberName, ctx);
            }
            return ti.getMethod(methodId).apply(new ArrayList<Value>(Arrays.asList(new Value(variableName))));
        }
        else {
            throw new DelphiRuntimeError("Identifier " + variableName + " not found in context", ctx);
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

