package Interpreter;

import java.util.*;
import java.util.function.Function;

public class TypeInfo {
    enum InheritanceType {
        ABSTRACT,
        SEALED,
        DEFAULT
    }

    enum Type {
        CLASS,
        INTERFACE
    }

    Type type = Type.CLASS;
    InheritanceType inheritanceType = InheritanceType.DEFAULT;
    ArrayList<TypeInfo> parents = new ArrayList<>(); // first element is parent, all others are interfaces
    private HashMap<String, Value> attributeMap = new HashMap<>();
    private HashMap<CallableInfo, Function<ArrayList<Value>, Value>> methodMap = new HashMap<>();

    public TypeInfo(Type type) {
        this.type = type;
    }

    void registerAttribute(String name, Value value) {
        attributeMap.put(name, value);
    }

    void registerMethod(CallableInfo name, Function<ArrayList<Value>, Value> method) {
        methodMap.put(name, method);
    }

    Value getAttribute(String attributeName) {
        if (attributeMap.containsKey(attributeName)) {
            return attributeMap.get(attributeName);
        }
        if (!parents.isEmpty()) {
            return parents.get(0).getAttribute(attributeName);
        }
        return null;
    }

    HashMap<String, Value> getAttributes() {
        HashMap<String, Value> attributes = new HashMap<>();
        attributes.putAll(attributeMap);
        if (!parents.isEmpty()) {
            attributes.putAll(parents.get(0).getAttributes());
        }
        return attributes;
    }

    Function<ArrayList<Value>, Value> getMethod(CallableInfo methodId) {
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

    boolean hasAttribute(String attributeName) {
        return getAttribute(attributeName) != null;
    }

    boolean hasMethod(CallableInfo methodId) {
        return getMethod(methodId) != null;
    }
    
    boolean hasUninheritedAttribute(String attributeName) {
        return attributeMap.containsKey(attributeName);
    }

    boolean hasUninheritedMethod(CallableInfo methodId) {
        return methodMap.containsKey(methodId);
    }
}
