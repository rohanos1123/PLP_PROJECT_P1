package Interpreter;


import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;


public class ScopeManager{
    Deque<Frame> stack = new ArrayDeque<>();

    public void pushFrame(Frame newFrame){
        if (!stack.isEmpty()) {
            newFrame.scope.parent = Optional.of(stack.peek().scope);
        }
        this.stack.push(newFrame);
    }

    public void pushFrame(Frame newFrame, CallableInfo ci){
        if (!stack.isEmpty()) {
            newFrame.scope.parent = stack.peek().getParent(ci);
        }
        this.stack.push(newFrame);
    }

    public void popFrame(){
        this.stack.pop();
    }

    public Frame top(){
        if(!this.stack.isEmpty()){
            return this.stack.peek();
        }
        else{
            throw new RuntimeException("Interpreter Error: Empty stack");
        }
    }

    public void addVariable(Value variable) {
        Frame currentFrame = top();
        currentFrame.scope.definedLocal.put(variable.identifier, variable);
    }

    public void addVariable(String identifier, Value variable) {
        variable.identifier = identifier;
        addVariable(variable);
    }

    public void addCallable(CallableInfo key,  Function<ArrayList<Value>, Value> function) {
        Frame currentFrame = top();
        currentFrame.scope.definedCallables.put(key, function);
    }

    public Optional<Value> getVariable(String valKey){
        return top().getValue(valKey);
    }

    public Optional<Function<ArrayList<Value>, Value>> getFunction(CallableInfo fn_id){
        return top().getFunction(fn_id);
    }
}