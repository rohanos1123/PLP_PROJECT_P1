package Util; 

import java.util.*;
import java.util.function.Function;

import Interpreter.DelphiObject;
import Interpreter.Value;

public class Frame<T> {
    public enum Type {
        FUNCTION,
        CLASS,
        LOCAL,
        OBJECT
    };

    public class ScopeNode<U> {
        public LinkedHashMap<String, U> definedLocal = new LinkedHashMap<>();
        public LinkedHashMap<CallableInfo, Function<ArrayList<U>, U>> definedCallables = new LinkedHashMap<>();
        public Optional<ScopeNode<U>> parent = Optional.<ScopeNode<U>>empty();

        public ScopeNode(){}

        public ScopeNode(ScopeNode<U> parent){
            this.parent = Optional.of(parent);
        }
    }

    public Type scopeType = Type.FUNCTION;
    public ScopeNode<T> scope = new ScopeNode<T>();
    public Optional<TypeInfo<T>> objectTypeInfo = Optional.empty();

    // Stack frame for function call
    public Frame(Type t) {
        this.scopeType = t;
    }

    // Stack frame for method call
    public Frame(Type t, TypeInfo<T> objType){
        this(t);
        this.objectTypeInfo = Optional.of(objType);
    }

    private ScopeNode<T> getParent(ScopeNode<T> curr, CallableInfo ci){
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

    private Optional<T> getFamilyLocal(String key, ScopeNode<T> travNode){
        if(travNode.definedLocal.containsKey(key)){
            return Optional.of(travNode.definedLocal.get(key));
        }
        if(travNode.parent.isPresent()){
            return getFamilyLocal(key, travNode.parent.get());
        }
        return Optional.empty();
    }

    private Optional<Function<ArrayList<T>, T>> getFamilyFunction(CallableInfo key, ScopeNode<T> travNode){
        if(travNode.definedCallables.containsKey(key)){
            return Optional.of(travNode.definedCallables.get(key));
        }
        if(travNode.parent.isPresent()){
            return getFamilyFunction(key, travNode.parent.get());
        }
        return Optional.empty();
    }

    public Optional<ScopeNode<T>> getParent(CallableInfo ci){
        return Optional.of(getParent(scope, ci));
    }

    public Optional<Function<ArrayList<T>, T>> getFunction(CallableInfo fnid){
        if(this.scopeType == Type.OBJECT && objectTypeInfo.isPresent()) {
            if (this.objectTypeInfo.get().hasMethod(fnid)) {
                return Optional.of(this.objectTypeInfo.get().getMethod(fnid));
            }
        }
        return getFamilyFunction(fnid, scope);
    }

    public Optional<T> getValue(String valKey) {
        if (this.scopeType == Type.OBJECT) {
            // Test to receive from self attribute
            T val = getFamilyLocal("Self", scope).get();
            if (val instanceof Value) {
                DelphiObject obj = (DelphiObject)((Value)val).value;
                if (obj.hasAttribute(valKey)) {
                    @SuppressWarnings("unchecked")
                    var attribute = (T)obj.getAttribute(valKey);
                    return Optional.of(attribute);
                }
            }
            else if (objectTypeInfo.isPresent()) {
                var ti = objectTypeInfo.get();
                if (ti.hasAttribute(valKey)) {
                    var attribute = ti.getAttribute(valKey);
                    return Optional.of(attribute);
                }
            }
        }
        return getFamilyLocal(valKey, scope);
    }
};