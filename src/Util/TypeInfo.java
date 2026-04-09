package Util;

import java.util.*;
import java.util.function.Function;

import Interpreter.Value;

public class TypeInfo {
    public enum InheritanceType {
        ABSTRACT,
        SEALED,
        DEFAULT
    }

    public enum Type {
        CLASS,
        INTERFACE
    }

    public Type type = Type.CLASS;
    public InheritanceType inheritanceType = InheritanceType.DEFAULT;
    public ArrayList<TypeInfo> parents = new ArrayList<>(); // first element is parent, all others are interfaces
    
    private HashMap<String, Value> attributeMap = new HashMap<>();
    private HashMap<CallableInfo, Function<ArrayList<Value>, Value>> methodMap = new HashMap<>();

    public TypeInfo(Type type) {
        this.type = type;
    }

    public void registerAttribute(String name, Value value) {
        attributeMap.put(name, value);
    }

    public void registerMethod(CallableInfo name, Function<ArrayList<Value>, Value> method) {
        methodMap.put(name, method);
    }

    public Value getAttribute(String attributeName) {
        if (attributeMap.containsKey(attributeName)) {
            return attributeMap.get(attributeName);
        }
        if (!parents.isEmpty()) {
            return parents.get(0).getAttribute(attributeName);
        }
        return null;
    }

    public HashMap<String, Value> getAttributes() {
        HashMap<String, Value> attributes = new HashMap<>();
        attributes.putAll(attributeMap);
        if (!parents.isEmpty()) {
            attributes.putAll(parents.get(0).getAttributes());
        }
        return attributes;
    }

    public Function<ArrayList<Value>, Value> getMethod(CallableInfo methodId) {
        if (methodMap.containsKey(methodId)) {
            return methodMap.get(methodId);
        }
        for (var parent : parents) {
            var method = parent.getMethod(methodId);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    public boolean hasAttribute(String attributeName) {
        return getAttribute(attributeName) != null;
    }

    public boolean hasMethod(CallableInfo methodId) {
        return getMethod(methodId) != null;
    }
    
    public boolean hasUninheritedAttribute(String attributeName) {
        return attributeMap.containsKey(attributeName);
    }

    public boolean hasUninheritedMethod(CallableInfo methodId) {
        return methodMap.containsKey(methodId);
    }
}
