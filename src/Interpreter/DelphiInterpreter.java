package Interpreter;

import org.antlr.v4.runtime.ParserRuleContext;
import Grammar.DelphiBaseVisitor;
import Grammar.DelphiParser;

import java.util.*;
import java.util.function.Function;

public class DelphiInterpreter extends DelphiBaseVisitor<Value> {

    Stack<HashMap<String, Value>> memory = new Stack<HashMap<String, Value>>();
    HashMap<CallableInfo, Function<ArrayList<Value>, Value>> callables = new HashMap<>();
    HashMap<String, ClassInfo> classInfo = new HashMap<>();

    // Keeps track of information between visitors
    boolean isPrivate = false;
    String currentClass = "";

    private static String createLogMsg(Object msg, ParserRuleContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        return "Line: " + line + ", Column: " + column + ": " + msg.toString();
    }

	@SuppressWarnings("unused")
	private static void log(Object msg, ParserRuleContext ctx) {
        System.err.println(createLogMsg(msg, ctx));
    }

    private static RuntimeException error(Object msg, ParserRuleContext ctx) {
        throw new RuntimeException(createLogMsg(msg, ctx));
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
        this.currentClass = className;
        return visitChildren(ctx);
    }

    @Override
    public Value visitClassType(DelphiParser.ClassTypeContext ctx){
        ClassInfo ci = new ClassInfo();
        this.classInfo.put(this.currentClass, ci);
        
        this.isPrivate = false;
        this.memory.push(new HashMap<>());
        visitChildren(ctx);
        for(var entry : this.memory.peek().entrySet()){
            ci.attributeMap.put(entry.getKey(), entry.getValue());
        }
        this.memory.pop();
        this.isPrivate = false;
        
        return new Value(0);
    }

    @Override
    public Value visitAccessSpecifier(DelphiParser.AccessSpecifierContext ctx){
        this.isPrivate = ctx.PRIVATE() != null;
        return visitChildren(ctx);
    }

    @Override
    public Value visitProcedurePrototype(DelphiParser.ProcedurePrototypeContext ctx){
        String procedureName = (String)visit(ctx.identifier()).value;
        var methods = this.classInfo.get(currentClass).methodMap;
        var procedureId = new CallableInfo(procedureName);
        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                procedureId.parameterNames.add((String)param.value);
                procedureId.parameterTypes.add(param.type);
            }
        }
        if (ctx.CONSTRUCTOR() != null) {
            procedureId.returnType = TYPE.REFERENCE;            
        }
        methods.put(procedureId, null);
        return new Value(0);
    }

    @Override
    public Value visitFunctionPrototype(DelphiParser.FunctionPrototypeContext ctx) {
        String functionName = (String)visit(ctx.identifier()).value;
        var methods = this.classInfo.get(currentClass).methodMap;
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
        methods.put(procedureId, null);
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
    public Value visitConstructorDeclaration(DelphiParser.ConstructorDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String constructorName = (String)visit(ctx.identifier(1)).value;
        ClassInfo cdata = this.classInfo.get(className);

        var constructorId = new CallableInfo(constructorName);

        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                constructorId.parameterNames.add((String)param.value);
                constructorId.parameterTypes.add(param.type);
            }
        }

        if(!cdata.methodMap.containsKey(constructorId)){
            throw error("Constructor " + constructorName + " not declared in class " + className, ctx);
        }

        cdata.methodMap.put(constructorId, (args) -> {
            HashMap<String, Value> frame = new HashMap<>();
            var objectAttributes = cdata.attributeMap;
            for (int i = 0; i < args.size(); i++) {
                frame.put(constructorId.parameterNames.get(i), args.get(i));
            }
            objectAttributes.forEach((attrName, attrVal) -> frame.put(attrName, attrVal));
            memory.add(frame);
            visit(ctx.block());
            objectAttributes.forEach((attrName, attrVal) -> objectAttributes.put(attrName, frame.get(attrName)));
            var object = new DelphiObject(className, objectAttributes);
            memory.pop();
            return new Value(object, TYPE.REFERENCE);
        });

        return new Value(0);
    }

    @Override
    public Value visitDestructorDeclaration(DelphiParser.DestructorDeclarationContext ctx){
        String className = (String)visit(ctx.identifier(0)).value;
        String destructorName = (String)visit(ctx.identifier(1)).value;
        ClassInfo cdata = this.classInfo.get(className);

        var destructorId = new CallableInfo(destructorName);

        if (ctx.formalParameterList() != null) {
            @SuppressWarnings("unchecked")
            var params = (ArrayList<Value>) visit(ctx.formalParameterList()).value;
            for (var param : params) {
                destructorId.parameterNames.add((String)param.value);
                destructorId.parameterTypes.add(param.type);
            }
        }

        if(!cdata.methodMap.containsKey(destructorId)){
            throw error("Destructor " + destructorName + " not declared in class " + className, ctx);
        }

        cdata.methodMap.put(destructorId, (args) -> {
            HashMap<String, Value> frame = new HashMap<>();
            var object = (DelphiObject) args.get(0).value;
            var objectAttributes = object.attributeMap;
            for (int i = 1; i < args.size(); i++) {
                frame.put(destructorId.parameterNames.get(i), args.get(i));
            }
            objectAttributes.forEach((attrName, attrVal) -> frame.put(attrName, attrVal));
            memory.add(frame);
            visit(ctx.block());
            memory.pop();
            memory.peek().get(args.get(0).identifier).type = TYPE.VOID;
            return new Value(null, TYPE.REFERENCE);
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
                throw error(procedureName + " not found.", ctx);
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
        if (this.classInfo.containsKey(variableName)) {
            var methods = this.classInfo.get(variableName).methodMap;
            return methods.get(methodId).apply(args);
        }
        else if (topVariables.containsKey(variableName)) {
            var variable = topVariables.get(variableName);
            var object = (DelphiObject)variable.value;
            var methods = this.classInfo.get(object.type).methodMap;
            return methods.get(methodId).apply(new ArrayList<Value>(Arrays.asList(variable)));
        }
        else {
            throw error("Invalid method invocation: " + variableName + "." + methodName, ctx);
        }
    }

    @Override
    public Value visitSignedFactor(DelphiParser.SignedFactorContext ctx){
        Value pureValue = visitChildren(ctx);
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
                throw error(variableName + " is uninitialized.", ctx);
            }
            if (!memberName.isEmpty()) {
                var object = (DelphiObject)variable.value;
                var methods = this.classInfo.get(object.type).methodMap;
                var methodId = new CallableInfo(memberName);
                if (object.attributeMap.containsKey(memberName)) {
                    var attribute = object.attributeMap.get(memberName);
                    if (attribute.isPrivate()) {
                        throw error("Attempting to access a private member: " + memberName, ctx);
                    }
                    return attribute;
                }
                else if (methods.containsKey(methodId)) {
                    return methods.get(methodId).apply(new ArrayList<Value>(Arrays.asList(variable)));
                }
                else {
                    throw error("No such attribute named: " + memberName, ctx);
                }
            }
            return variable;
        }
        else if (this.classInfo.containsKey(variableName)) {
            var methodId = new CallableInfo(memberName);
            var methods = this.classInfo.get(variableName).methodMap;
            if (!methods.containsKey(methodId)) {
                throw error("No such method named: " + memberName, ctx);
            }
            return methods.get(methodId).apply(new ArrayList<>());            
        }
        else {
            throw error("Identifier " + variableName + " not found in context", ctx);
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
        return new Value(null, TYPE.INT);
    }

}

