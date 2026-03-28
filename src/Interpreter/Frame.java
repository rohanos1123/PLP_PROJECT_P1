package Interpreter; 

import java.util.*;
import java.util.function.Function;

public class Frame{
    public enum Type {
        FUNCTION,
        LOCAL,
        OBJECT
    };

    // Only returns the local stack memory
    private Optional<Value> getLocalMemory(String value){
        if(this.memory.containsKey(value)){
            return Optional.of(this.memory.get(value));
        }
        else{
            return Optional.empty();
        }
    }

    HashMap<String, Value> memory = new HashMap<>();
    HashMap<CallableInfo, Function<ArrayList<Value>, Value>> callables = new HashMap<>();
    Type scopeType = Type.FUNCTION;
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

    public Optional<Function<ArrayList<Value>, Value>> getFunction(CallableInfo fnid){
        if(this.callables.containsKey(fnid)){
            return Optional.of(this.callables.get(fnid));
        }
        else{
            if(this.scopeType == Type.OBJECT && objectTypeInfo.isPresent()) {
                if (this.objectTypeInfo.get().hasMethod(fnid)) {
                    return Optional.of(this.objectTypeInfo.get().getMethod(fnid));
                }
            }
            return Optional.empty();
        }
    }

    public Optional<Value> getValue(String valKey) {
        Optional<Value> localValue = this.getLocalMemory(valKey);
        if (localValue.isPresent()) {
            return localValue;
        }
        else {
            if (this.scopeType == Type.OBJECT) {
                // Test to receive from self attribute
                delphiObject obj = (delphiObject) this.memory.get("Self").value;
                if (obj.hasAttribute(valKey)) {
                    return Optional.of(obj.getAttribute(valKey));
                }
                else {
                    return Optional.empty();
                }
            }
            return Optional.empty();
        }
    }
};