package Interpreter;

import java.util.HashMap;

public class DelphiObject {

    public String type;
    private HashMap<String, Value> attributeMap = new HashMap<>();

    public DelphiObject(String type, HashMap<String, Value> initAttr){
        this.type = type;
        for(var entry : initAttr.entrySet()){
            this.attributeMap.put(entry.getKey(), new Value(entry.getValue()));
        }
    }

    public boolean hasAttribute(String attributeName) {
        return attributeMap.containsKey(attributeName);
    }

    public Value getAttribute(String attributeName) {
        if (!hasAttribute(attributeName)) {
            throw new RuntimeException("Object has no such attribute: " + attributeName);
        }
        return attributeMap.get(attributeName);
    }
}


