package Interpreter; 

import java.util.*;
import java.util.function.Function;

public class Frame{
    public enum Type {
        FUNCTION,
        LOCAL,
        OBJECT
    };

    public class ScopeNode{
        HashMap<String, Value> definedLocal = new HashMap<>();
        HashMap<CallableInfo, Function<ArrayList<Value>, Value>> definedCallables = new HashMap<>();
        Optional<ScopeNode> parent = Optional.<ScopeNode>empty();

        public ScopeNode(){}

        public ScopeNode(ScopeNode parent){
            this.parent = Optional.of(parent);
        }
    }

    Type scopeType = Type.FUNCTION;
    ScopeNode scope = new ScopeNode();
    Optional<TypeInfo> objectTypeInfo = Optional.<TypeInfo>empty();

    // Stack frame for function call
    public Frame(Type t) {
        this.scopeType = t;
    }

    // Stack frame for method call
    public Frame(Type t, TypeInfo objType){
        this(t);
        this.objectTypeInfo = Optional.of(objType);
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

    private Optional<Value> getFamilyLocal(String key, ScopeNode travNode){
        if(travNode.definedLocal.containsKey(key)){
            return Optional.of(travNode.definedLocal.get(key));
        }
        if(travNode.parent.isPresent()){
            return getFamilyLocal(key, travNode.parent.get());
        }
        return Optional.empty();
    }

    private Optional<Function<ArrayList<Value>, Value>> getFamilyFunction(CallableInfo key, ScopeNode travNode){
        if(travNode.definedCallables.containsKey(key)){
            return Optional.of(travNode.definedCallables.get(key));
        }
        if(travNode.parent.isPresent()){
            return getFamilyFunction(key, travNode.parent.get());
        }
        return Optional.empty();
    }

    public Optional<ScopeNode> getParent(CallableInfo ci){
        return Optional.of(getParent(scope, ci));
    }

    public Optional<Function<ArrayList<Value>, Value>> getFunction(CallableInfo fnid){
        if(this.scopeType == Type.OBJECT && objectTypeInfo.isPresent()) {
            if (this.objectTypeInfo.get().hasMethod(fnid)) {
                return Optional.of(this.objectTypeInfo.get().getMethod(fnid));
            }
        }
        return getFamilyFunction(fnid, scope);
    }

    public Optional<Value> getValue(String valKey) {
        if (this.scopeType == Type.OBJECT) {
            // Test to receive from self attribute
            DelphiObject obj = (DelphiObject) getFamilyLocal("Self", scope).get().value;
            if (obj.hasAttribute(valKey)) {
                return Optional.of(obj.getAttribute(valKey));
            }
        }
        return getFamilyLocal(valKey, scope);
    }
};