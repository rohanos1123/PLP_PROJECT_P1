package Util;

import java.util.*;
import java.util.function.Function;

public class TypeInfo<T> {
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
    public ArrayList<TypeInfo<T>> parents = new ArrayList<>(); // first element is parent, all others are interfaces
    
    private HashMap<String, T> attributeMap = new HashMap<>();
    private HashMap<CallableInfo, Function<ArrayList<T>, T>> methodMap = new HashMap<>();

    public TypeInfo(Type type) {
        this.type = type;
    }

    public void registerAttribute(String name, T value) {
        attributeMap.put(name, value);
    }

    public void registerMethod(CallableInfo name, Function<ArrayList<T>, T> method) {
        methodMap.put(name, method);
    }

    public T getAttribute(String attributeName) {
        if (attributeMap.containsKey(attributeName)) {
            return attributeMap.get(attributeName);
        }
        if (!parents.isEmpty()) {
            return parents.get(0).getAttribute(attributeName);
        }
        return null;
    }

    public HashMap<String, T> getAttributes() {
        HashMap<String, T> attributes = new HashMap<>();
        attributes.putAll(attributeMap);
        if (!parents.isEmpty()) {
            attributes.putAll(parents.get(0).getAttributes());
        }
        return attributes;
    }

    public Function<ArrayList<T>, T> getMethod(CallableInfo methodId) {
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
