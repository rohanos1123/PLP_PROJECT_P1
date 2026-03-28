package Interpreter;

import java.util.HashMap;

public class delphiObject {

    String type;
    private HashMap<String, Value> attributeMap = new HashMap<>();

    delphiObject(String type, HashMap<String, Value> initAttr){
        this.type = type;
        for(var entry : initAttr.entrySet()){
            this.attributeMap.put(entry.getKey(), new Value(entry.getValue()));
        }
    }

    boolean hasAttribute(String attributeName) {
        return attributeMap.containsKey(attributeName);
    }

    Value getAttribute(String attributeName) {
        if (!hasAttribute(attributeName)) {
            throw new RuntimeException("Object has no such attribute: " + attributeName);
        }
        return attributeMap.get(attributeName);
    }
}


