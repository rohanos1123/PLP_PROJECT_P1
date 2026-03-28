package Interpreter;


import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;


public class ScopeManager{
    HashMap<String, Value> globals = new HashMap<>();
    HashMap<CallableInfo, Function<ArrayList<Value>, Value>> globalFunctions = new HashMap<>();
    Deque<Frame> stack = new ArrayDeque<>();

    public void pushFrame(Frame newFrame){
        this.stack.push(newFrame);
    }

    private Optional<Value> searchGlobalValues(String valKey){
        if(this.globals.containsKey(valKey)){
            return Optional.of(this.globals.get(valKey));
        }
        else{
            return Optional.empty();
        }
    }

    public Frame top(){
        if(!this.stack.isEmpty()){
            return this.stack.peek();
        }
        else{
            throw new RuntimeException("Interpreter Error: Empty stack");
        }
    }

    private Optional<Function<ArrayList<Value>, Value>> searchGlobalFunctions(CallableInfo fn_id){
        if(this.globalFunctions.containsKey(fn_id)){
            return Optional.of(this.globalFunctions.get(fn_id));
        }
        else{
            return Optional.empty();
        }
    }

    public void addVariable(Value variable) {
        if (stack.isEmpty()) {
            this.globals.put(variable.identifier, variable);
        }
        else {
            Frame currentFrame = top();
            currentFrame.memory.put(variable.identifier, variable);
        }
    }

    public void addVariable(String identifier, Value variable) {
        variable.identifier = identifier;
        addVariable(variable);
    }

    public void addCallable(CallableInfo key,  Function<ArrayList<Value>, Value> function) {
        if (stack.isEmpty()) {
            this.globalFunctions.put(key, function);
        }
        else {
            Frame currentFrame = top();
            currentFrame.callables.put(key, function);
        }
    }

    public Optional<Value> getVariable(String valKey){
        if (stack.isEmpty()) return searchGlobalValues(valKey);

        Frame currentFrame = top();
        switch(currentFrame.scopeType){
            case Frame.Type.LOCAL : {
                // Iteratively search through parent scopes
                Iterator<Frame> it = stack.descendingIterator();
                while (it.hasNext()) {
                    currentFrame = it.next();
                    Optional<Value> targ = currentFrame.getValue(valKey);
                    if (targ.isPresent()) {
                        return targ;
                    }
                    if(currentFrame.scopeType == Frame.Type.FUNCTION){
                        break;
                    }
                }
                // Attempt to find the key in the globals
                return this.searchGlobalValues(valKey);
            }
            case Frame.Type.OBJECT :
            case Frame.Type.FUNCTION : {
                // Only search Latest scope (Object scope for objects but handled in frame function)
                Optional<Value> obj = currentFrame.getValue(valKey);
                if(obj.isPresent()){
                    return obj;
                }

                // Search globals as last resort
                return this.searchGlobalValues(valKey);
            }
        }

        return Optional.empty();
    }

    public Optional<Function<ArrayList<Value>, Value>> getFunction(CallableInfo fn_id){
        if (stack.isEmpty()) return searchGlobalFunctions(fn_id);

        Frame currentFrame = top();
        switch(currentFrame.scopeType){
            case Frame.Type.LOCAL : {
                // Iteratively search through parent scopes
                Iterator<Frame> it = stack.descendingIterator();
                while (it.hasNext()) {
                    currentFrame = it.next();
                    var targ = currentFrame.getFunction(fn_id);
                    if (targ.isPresent()) {
                        return targ;
                    }
                    if(currentFrame.scopeType == Frame.Type.FUNCTION){
                        break;
                    }
                }
                // Attempt to find the key in the globals
                return this.searchGlobalFunctions(fn_id);
            }
            case Frame.Type.OBJECT :
            case Frame.Type.FUNCTION : {
                // Only search Latest scope (Object scope for objects but handled in frame function)
                var obj = currentFrame.getFunction(fn_id);
                if(obj.isPresent()){
                    return obj;
                }

                // Search globals as last resort
                return this.searchGlobalFunctions(fn_id);
            }
        }

        return Optional.empty();
    }

    public void popFrame(){
        this.stack.pop();
    }
}