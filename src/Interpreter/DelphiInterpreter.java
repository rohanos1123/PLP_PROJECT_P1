package Interpreter;

import Grammar.DelphiBaseVisitor;
import Grammar.DelphiParser;

import java.util.*;

public class DelphiInterpreter extends DelphiBaseVisitor<Value> {
    Stack<Integer> objectCallContext = new Stack<>();
    ArrayList<DelphiObject> objectMap = new ArrayList<>();
    ArrayList<DelphiParser.ConstructorDeclarationContext> constructorList = new ArrayList<>();

    Stack<HashMap<String, Value>> memory = new Stack<HashMap<String, Value>>();
    HashMap<String, ClassInfo> classInfo = new HashMap<>();

    // Keeps track of information between visitors
    boolean isPrivate = false;
    String currentClass = "";

    // Class Definition
    Scanner sc = new Scanner(System.in);

    // Maps string names to parameter before passing into function:
    HashMap<String, Value> makeParameterMap(DelphiParser.FormalParameterListContext fpl, DelphiParser.ParameterListContext pl){
        HashMap<String, Value> attributeMap = new HashMap<>();
        if(fpl.formalParameterSection().size() != pl.actualParameter().size()){
            throw new RuntimeException("Parameter list size mismatch!");
        }

        for(int i = 0 ; i < fpl.formalParameterSection().size(); i++){
            var namedParam = fpl.formalParameterSection(i);
            String parameterName = namedParam.parameterGroup().identifierList().identifier(0).IDENT().toString();
            attributeMap.put(parameterName, visit(pl.actualParameter(i).expression()));
        }

        return attributeMap;
    }

    boolean XOR(boolean A, boolean B){
        return (!A && B) || (A && !B);
    }

    Value constructObject(Value v, DelphiParser.MethodCallContext ctx){
        // Get the constructor object
        int cindex = v.asInteger();
        var consObj = this.constructorList.get(cindex);

        // Derive class from constructor object & get default attributes:
        String className = consObj.access().identifier(0).IDENT().toString();
        if(!this.classInfo.containsKey(className)){
            throw new RuntimeException("Cannot declared class of name " + className);
        }
        var defaultAtr = this.classInfo.get(className).attributeMap;
        int newIndex = this.objectMap.size();
        this.objectMap.add(new DelphiObject(className ,defaultAtr));

        // Check if parameters are required and make the parameter map
        HashMap<String, Value> parameterMap = new HashMap<>();
        if(XOR(consObj.formalParameterList() != null, ctx.parameterList() != null)){
            throw new RuntimeException("Parameter size mistmatch for constructor");
        }

        if(consObj.formalParameterList() != null && ctx.parameterList() != null){
            parameterMap = makeParameterMap(consObj.formalParameterList(), ctx.parameterList());
        }

        // Push objects onto stack, invoke the constructor block, and pop
        this.objectCallContext.push(newIndex);
        this.memory.push(parameterMap);
        visit(consObj.block());
        this.memory.pop();
        this.objectCallContext.pop();

        return new Value(newIndex, TYPE.REFERENCE);
    }



    @Override
    public Value visitProgram(DelphiParser.ProgramContext ctx){
        // push the initial Stack Frame
        this.memory.push(new HashMap<>());
        visit(ctx.topLevelBlock());
        return new Value(0);
    }

    @Override
    public Value visitBlock(DelphiParser.BlockContext ctx) {
        return super.visitBlock(ctx);
    }

    @Override
    public Value visitExpandedTypeDefinition(DelphiParser.ExpandedTypeDefinitionContext ctx){
        String className = ctx.identifier().IDENT().toString();
        this.currentClass = className;
        ClassInfo ci = new ClassInfo();
        ci.classDefContext = ctx.classType().classDefinition();
        this.classInfo.put(className, ci);

        // Set a template attribute map
        for(var memberList : ctx.classType().classDefinition().memberListPart()){
            this.memory.push(new HashMap<>());
            visit(memberList);
            for(var item : this.memory.peek().entrySet()){
                this.classInfo.get(className).attributeMap.put(item.getKey(), item.getValue());
            }
            this.memory.pop();
        }

        return new Value(0);
    }

    @Override
    public Value visitMemberListPart(DelphiParser.MemberListPartContext ctx){
        isPrivate = false;

        if(ctx.accessSpecifier() != null  && ctx.accessSpecifier().PRIVATE() != null){
            isPrivate = true;
        }
        for(var declarePart : ctx.primaryFieldDeclarationPart()){
            visit(declarePart);
        }

        if(ctx.methodPrototype() != null){
            visit(ctx.methodPrototype());
        }


        for(var obj : this.memory.peek().values()){
            obj.setAccess(isPrivate);
        }

        return new Value(0);
    }

    @Override
    public Value visitMethodPrototype(DelphiParser.MethodPrototypeContext ctx){
        visitChildren(ctx);
        return new Value(0);
    }

    @Override
    public Value visitConstructorPrototype(DelphiParser.ConstructorPrototypeContext ctx){
        String constructorName = ctx.identifier().IDENT().toString();
        this.classInfo.get(currentClass).constructorMap.put(constructorName, new Value(0, TYPE.CPTR, isPrivate));
        return new Value(0);
    }

    @Override
    public Value visitPrimaryFieldDeclarationPart(DelphiParser.PrimaryFieldDeclarationPartContext ctx){
        visitChildren(ctx);
        return new Value(0);
    }

    @Override
    public Value visitVariableDeclarationPart(DelphiParser.VariableDeclarationPartContext ctx){
        // Visit the first declaration:
        visit(ctx.variableDeclaration(0));
        for(int i = 1 ;i < ctx.variableDeclaration().size(); i++){
            visit(ctx.variableDeclaration(i));
        }
        return new Value(0);
    }

    @Override
    public Value visitConstructorDeclaration(DelphiParser.ConstructorDeclarationContext ctx){
        // Constructor Declaration
        String className = ctx.access().identifier(0).IDENT().toString();
        String constructorName = ctx.access().identifier(1).IDENT().toString();
        int consIndex = this.constructorList.size();
        ClassInfo cdata = this.classInfo.get(className);

        if(cdata.constructorMap.containsKey(constructorName)){
            cdata.constructorMap.get(constructorName).setValOnly(consIndex);
        }
        else{
            throw new RuntimeException("Constructor " + constructorName + " not declared in class " + className);
        }

        this.constructorList.add(ctx);

        return new Value(0);
    }

    @Override
    public Value visitVariableDeclaration(DelphiParser.VariableDeclarationContext ctx){
        // Get the type
        for(var identifier : ctx.identifierList().identifier()){
            this.memory.peek().put(identifier.IDENT().toString(), new Value(0, TYPE.INT));
        }
        return new Value(0);
    }

    @Override
    public Value visitAssignmentStatement(DelphiParser.AssignmentStatementContext ctx){
        Value writeTo = visit(ctx.variable());
        if(writeTo.type == TYPE.FPTR){
            throw new RuntimeException("Cannot write new function to FPTR");
        }

        Value v = visit(ctx.expression());
        writeTo.copyValue(v);
        return new Value(0);
    }

    @Override
    public Value visitProcedureStatement(DelphiParser.ProcedureStatementContext ctx){
        if(ctx.identifier().IDENT().toString().equals("WriteLn")){
            for(var param : ctx.parameterList().actualParameter()){
                System.out.print(visit(param.expression()).asString());
            }
            System.out.println();
        }
        else if(ctx.identifier().IDENT().toString().equals("ReadLn")){
            Value obj = visit(ctx.parameterList().actualParameter(0).expression());
            int val = sc.nextInt();
            obj.copyValue(new Value(val));
        }

        return new Value(0);
    }

    @Override
    public Value visitMethodCall(DelphiParser.MethodCallContext ctx){
        var identList = ctx.access().identifier();
        String object = identList.getFirst().IDENT().toString();
        String method = identList.get(1).IDENT().toString();

        Value v = visit(ctx.access());

        if(v.type == TYPE.CPTR){
            return constructObject(v, ctx);
        }
        else{
            if(ctx.parameterList() == null){
                return v;
            }
            throw new RuntimeException("Invalid function call for accessed type");
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

    @Override
    public Value visitVariable(DelphiParser.VariableContext ctx){
        return visitChildren(ctx);
    }

    @Override
    public Value visitAccess(DelphiParser.AccessContext ctx){
        String king = ctx.identifier(0).IDENT().toString();

        if(this.memory.peek().containsKey(king)) {
            // Accessing an object
            Value newValue = this.memory.peek().get(king);

            for (var ident : ctx.identifier().subList(1, ctx.identifier().size())) {
                if (!(newValue.type == TYPE.REFERENCE)) {
                    throw new RuntimeException("Attempting to access a non-reference");
                }
                this.objectCallContext.push(newValue.asInteger());
                newValue = visit(ident);
                this.objectCallContext.pop();
                if (newValue.isPrivate()) {
                    throw new RuntimeException("Attempting to access a private member: " + ident.IDENT().toString());
                }
            }
            return newValue;
        }
        else if(this.classInfo.containsKey(king)){
            // Accessing a static class member (assuming
            ClassInfo ci = this.classInfo.get(king);
            String accessor = ctx.identifier(1).IDENT().toString();

            if(ci.constructorMap.containsKey(accessor)){
                return ci.constructorMap.get(accessor);
            }
            else{
                throw new RuntimeException("Static member access other than constructors is forbidden");
            }
        }
        else{
            throw new RuntimeException("Unknown access to object " + king);
        }


    }

    @Override
    public Value visitIdentifier(DelphiParser.IdentifierContext ctx){

        String idStr = ctx.IDENT().toString();
        Value targValue;

        if(this.memory.peek().containsKey(idStr)){
            targValue = this.memory.peek().get(idStr);
        }
        else {
            if(this.objectCallContext.empty()){
                throw new RuntimeException("Identifier "  + idStr + " not found in context");
            }
            else{
                int objIndex = this.objectCallContext.peek();
                var targObject = this.objectMap.get(objIndex);
                if(targObject.attributeMap.containsKey(idStr)){
                    targValue = this.objectMap.get(objIndex).attributeMap.get(idStr);
                }
                else{
                    throw new RuntimeException("Identifier " + idStr + " not found in context");
                }
            }
        }

        return targValue;
    }










}

