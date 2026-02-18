package Interpreter;

import Grammar.DelphiBaseVisitor;
import Grammar.DelphiParser;

import java.util.*;

public class DelphiInterpreter extends DelphiBaseVisitor<Value> {
    Stack<Integer> objectCallContext;
    Stack<HashMap<String, Value>> memory;


    HashMap<String, DelphiObject> objectMap = new HashMap<>();
    HashMap<String, ClassInfo> classInfo = new HashMap<>();

    // Class Definitions
    HashMap<String, DelphiParser.ClassDefinitionContext> classDefinitionContextHashMap;
    boolean inPrivate = false;

    @Override
    public Value visitProgram(DelphiParser.ProgramContext ctx){
        visit(ctx.block());
        return new Value(0);
    }

    @Override
    public Value visitBlock(DelphiParser.BlockContext ctx) {
        for(DelphiParser.ClassDefinitionContext cdCtx : ctx.classDefinition()){
            visit(cdCtx);
        }
        return new Value(0);
    }

    @Override
    public Value visitClassDefinition(DelphiParser.ClassDefinitionContext ctx){
        String className = ctx.identifier().IDENT().toString();
        for(var stmt : ctx.classdefStatements()){
            visit(stmt);
        }
        return new Value(0);
    }

    @Override
    public Value visitAccessSpecifier(DelphiParser.AccessSpecifierContext ctx){
        if(ctx.PRIVATE() != null){
            this.inPrivate = true;
        }
        else if(ctx.PUBLIC() != null){
            this.inPrivate = false;
        }
        else{
            throw new RuntimeException("Invalid access specifier");
        }
        return new Value(0);
    }
}
