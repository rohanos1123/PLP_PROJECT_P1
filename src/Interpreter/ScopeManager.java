package Interpreter;


import com.sun.source.tree.Scope;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;


public class ScopeManager{
    HashMap<String, Value> globals = new HashMap<>();
    HashMap<CallableInfo, Function<ArrayList<Value>, Value>> globalCallables = new HashMap<>();
    Deque<Frame> stack = new ArrayDeque<>();

    // ScopeNode Stuff
    ScopeNode currentNode = new ScopeNode();

    // Keep track of Node data structure
    private static class ScopeNode{
        boolean isRoot = false;
        HashMap<String, Value> definedLocal = new HashMap<>();
        HashMap<CallableInfo, Function<ArrayList<Value>, Value>> definedCallables = new HashMap<>();
        Optional<ScopeNode> parent = Optional.<ScopeNode>empty();

        public ScopeNode(){}

        public ScopeNode(ScopeNode parent){
            this.parent = Optional.of(parent);
        }
    }

    private ScopeNode getParent(ScopeNode curr, CallableInfo ci){
        if(curr.definedCallables.containsKey(ci)){
            return curr;
        }
        else{
            if(curr.parent.isPresent()){
                return this.getParent(curr.parent.get(), ci);
            }
            throw new RuntimeException("ScopeNode error: Attempting to access parent");
        }
    }

    private Optional<Value> getFamilyLocal(String key){
        ScopeNode travNode = this.currentNode;
        do{
            if(travNode.definedLocal.containsKey(key)){
                return Optional.of(travNode.definedLocal.get(key));
            }
            
            if(travNode.parent.isPresent()){
                travNode = travNode.parent.get();
            }
          
        }while(travNode.parent.isPresent());

        throw new RuntimeException("Value ScopeNode Error: Key Not Found!");
    }

     private Optional<Function<ArrayList<Value>, Value>> getFamilyFunction(CallableInfo key){
        ScopeNode travNode = this.currentNode;

        do{
            if(travNode.definedCallables.containsKey(key)){
                return Optional.of(travNode.definedCallables.get(key));
            }
            if(travNode.parent.isPresent()){
                travNode = travNode.parent.get();
            }

        }while(travNode.parent.isPresent()); 

        throw new RuntimeException("Function ScopeNode Error: Key Not Found!");
    }



    public void pushFrame(Frame newFrame){
        this.stack.push(newFrame);
    }

    public void pushFrame(Frame newFrame, CallableInfo ci){
        ScopeNode parentNode = this.getParent(this.currentNode, ci);
        this.currentNode = new ScopeNode(parentNode);
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

    private Optional<Function<ArrayList<Value>, Value>> searchGlobalCallables(CallableInfo fn_id){
        if(this.globalCallables.containsKey(fn_id)){
            return Optional.of(this.globalCallables.get(fn_id));
        }
        else{
            return Optional.empty();
        }
    }

    public void addVariable(Value variable) {
        this.currentNode.definedLocal.put(variable.identifier, variable);

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
        this.currentNode.definedCallables.put(key, function);

        if (stack.isEmpty()) {
            this.globalCallables.put(key, function);
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
                return this.getFamilyLocal(valKey);
            }
            case Frame.Type.OBJECT :
            case Frame.Type.FUNCTION : {
                // Only search Latest scope (Object scope for objects but handled in frame function)
                Optional<Value> obj = currentFrame.getValue(valKey);
                if(obj.isPresent()){
                    return obj;
                }

                // Search globals as last resort
                return this.getFamilyLocal(valKey);
            }
        }

        return Optional.empty();
    }

    public Optional<Function<ArrayList<Value>, Value>> getFunction(CallableInfo fn_id){
        if (stack.isEmpty()) return searchGlobalCallables(fn_id);

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
                return this.getFamilyFunction(fn_id); 
            }
            case Frame.Type.OBJECT :
            case Frame.Type.FUNCTION : {
                // Only search Latest scope (Object scope for objects but handled in frame function)
                var obj = currentFrame.getFunction(fn_id);
                if(obj.isPresent()){
                    return obj;
                }

                // Search globals as last resort
                return this.getFamilyFunction(fn_id); 
            }
        }

        return Optional.empty();
    }

    public void popFrame(){
        this.stack.pop();
    }
}