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
    
    public ArrayList<T> getLocalAliases(boolean includeCurrentLocals) {
        ArrayList<T> aliases = new ArrayList<>();
        if (!global()) {
            var currScope = top().scope;
            // add all aliases for previous scopes
            var prevAliases = currScope.localAliasMap;
            aliases.addAll(prevAliases.values());
            // add all current locals
            if (includeCurrentLocals) {
                var currLocals = currScope.definedLocal;
                aliases.addAll(currLocals.values());
            }
        }
        return aliases;
    }

    public void populateAliasMap() {
        ArrayList<T> transferredLocals = new ArrayList<>();
        if (!global()) {
            var currScope = top().scope;
            var prevScope = currScope.parent.get(); // guaranteed since size() >= 2
            // add all variables from prior scope's alias map
            var grandfatherLocals = prevScope.localAliasMap;
            transferredLocals.addAll(grandfatherLocals.keySet());
            // add all locals from last scope (excluding global scope)
            if (size() > 2) {
                var prevLocals = prevScope.definedLocal;
                transferredLocals.addAll(prevLocals.values());
            }
        }
        top().populateAliasMap(transferredLocals);
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