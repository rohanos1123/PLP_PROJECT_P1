package Interpreter;

import java.util.HashMap;

public class DelphiObject {

    String type;
    HashMap<String, Value> attributeMap = new HashMap<>();

    DelphiObject(String type, HashMap<String, Value> initAttr){
        this.type = type;
        for(var entry : initAttr.entrySet()){
            this.attributeMap.put(entry.getKey(), new Value(entry.getValue()));
        }
    }
}


