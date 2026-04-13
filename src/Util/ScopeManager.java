package Util;

import java.util.*;
import java.util.function.Function;

import Interpreter.Value;


public class ScopeManager<T> {
    private Deque<Frame<T>> stack = new ArrayDeque<>();

    public void pushFrame(Frame<T> newFrame){
        if (!stack.isEmpty()) {
            newFrame.scope.parent = Optional.of(stack.peek().scope);
        }
        this.stack.push(newFrame);
    }

    public void pushFrame(Frame<T> newFrame, CallableInfo ci){
        if (!stack.isEmpty()) {
            newFrame.scope.parent = stack.peek().getParent(ci);
        }
        this.stack.push(newFrame);
    }

    public void popFrame(){
        this.stack.pop();
    }

    public Frame<T> top(){
        if(!this.stack.isEmpty()){
            return this.stack.peek();
        }
        else{
            throw new RuntimeException("Interpreter Error: Empty stack");
        }
    }

    public boolean empty() {
        return this.stack.isEmpty();
    }

    public int size() {
        return this.stack.size();
    }

    public void addVariable(String identifier, T variable) {
        if (variable instanceof Value) {
            ((Value)variable).identifier = identifier;
        }
        top().scope.definedLocal.put(identifier, variable);
    }

    public void addCallable(CallableInfo key,  Function<ArrayList<T>, T> function) {
        Frame<T> currentFrame = top();
        currentFrame.scope.definedCallables.put(key, function);
    }

    public Optional<T> getVariable(String valKey){
        return top().getValue(valKey);
    }

    public Optional<Function<ArrayList<T>, T>> getFunction(CallableInfo fn_id){
        return top().getFunction(fn_id);
    }
}