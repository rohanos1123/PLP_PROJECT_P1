package Interpreter;

import java.util.HashMap;

public class delphiObject {

    String type;
    HashMap<String, Value> attributeMap = new HashMap<>();

    delphiObject(String type, HashMap<String, Value> initAttr){
        this.type = type;
        for(var entry : initAttr.entrySet()){
            this.attributeMap.put(entry.getKey(), new Value(entry.getValue()));
        }
    }
}


