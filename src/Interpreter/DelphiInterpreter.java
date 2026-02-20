package Interpreter;

import Grammar.DelphiBaseVisitor;
import Grammar.DelphiParser;

import java.util.*;

public class DelphiInterpreter extends DelphiBaseVisitor<Value> {
    Stack<Integer> objectCallContext = new Stack<>();
    ArrayList<DelphiObject> objectMap = new ArrayList<>();

    Stack<HashMap<String, Value>> memory = new Stack<HashMap<String, Value>>();
    HashMap<String, ClassInfo> classInfo = new HashMap<>();

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
        boolean isPrivate = false;

        if(ctx.accessSpecifier() != null  && ctx.accessSpecifier().PRIVATE() != null){
            isPrivate = true;
        }
        for(var declarePart : ctx.primaryFieldDeclarationPart()){
            visit(declarePart);
        }

        for(var obj : this.memory.peek().values()){
            obj.setAccess(isPrivate);
        }

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
        this.classInfo.get(className).constructorMap.put(constructorName, ctx);
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
        Value v = visit(ctx.expression());
        writeTo.setValue(v);
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
            obj.setValue(new Value(val));
        }

        return new Value(0);
    }

    @Override
    public Value visitMethodCall(DelphiParser.MethodCallContext ctx){
        var identList = ctx.access().identifier();
        String object = identList.getFirst().IDENT().toString();
        String method = identList.get(1).IDENT().toString();


        if(this.memory.peek().containsKey(object)){
            // Method Call
            Value ptr = this.memory.peek().get(object);
            int refIndex = 0;
            if(ptr.isReference){
                refIndex = ptr.asInteger();
            }
            else{
                throw new RuntimeException("Attempting to access an attribute of a non-object");
            }
        }
        else if (this.classInfo.containsKey(object)) {
            // Static Object method / Constructor

            var infoObj = this.classInfo.get(object);
            var consObj = infoObj.constructorMap.get(method);

            // Make the new Parameter map
            HashMap<String, Value> parameterMap = makeParameterMap(consObj.formalParameterList(),ctx.parameterList());

            // Get the index ptr for new Object
            int objIndex = this.objectMap.size();
            DelphiObject dObj = new DelphiObject(object, infoObj.attributeMap);
            this.objectMap.add(dObj);

            this.objectCallContext.push(objIndex);
            this.memory.push(parameterMap);
            visit(consObj.block());
            this.memory.pop();
            this.objectCallContext.pop();

            return new Value(objIndex, TYPE.REFERENCE);
        }
        else{
            throw new RuntimeException("Attempting to access an attribute of an unknown object");
        }

        return new Value(0);
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
    public Value visitVariable(DelphiParser.VariableContext ctx) {
        if(!ctx.DOT().isEmpty()){
            String king = ctx.identifier(0).IDENT().toString();
            Value newValue = this.memory.peek().get(king);

            for(var ident : ctx.identifier().subList(1, ctx.identifier().size())){
                    if(!newValue.isReference){
                        throw new RuntimeException("Attempting to access a non-reference");
                    }

                    this.objectCallContext.push(newValue.asInteger());
                    newValue = visit(ident);
                    this.objectCallContext.pop();
                    if(newValue.isPrivate()){
                       throw new RuntimeException("Attempting to access a private member: " + ident.IDENT().toString());
                    }
                }
                return newValue;
            }
        else{
            return visitChildren(ctx);
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

