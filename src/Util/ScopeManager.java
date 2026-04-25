package Util;

import java.util.*;
import java.util.function.Function;

import Generator.Immediate;
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

    public boolean global() {
        return size() < 2;
    }
    
    public LinkedHashMap<String, T> getAllLocals() {
        LinkedHashMap<String, T> orderedLocals = new LinkedHashMap<>();
        if (!global()) {
            var currScope = top().scope;
            LinkedHashMap<String, T> transferLocals = new LinkedHashMap<>();
            while (currScope.parent.isPresent()) {
                transferLocals.putAll(currScope.definedLocal.reversed()); // reverse each subgroup to prepare for final reversal
                currScope = currScope.parent.get();
            }
            // ensures parameter %0 is mapped to something since no locals can be mapped to %0 due to entrypoint block
            @SuppressWarnings("unchecked") // never call in interpreter so this is ok
            T placeholder = (T)new Immediate(TYPE.REFERENCE, "null");
            orderedLocals.put("0placeholder", placeholder);
            orderedLocals.putAll(transferLocals.reversed());
        }
        return orderedLocals;
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