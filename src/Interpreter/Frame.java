package Interpreter; 

import java.util.*; 

public class Frame{
    enum Type {
        FUNCTION, 
        LOCAL, 
        OBJECT 
    }; 

    HashMap<String, Value> memory; 
    Hashmap<CallableInfo, Function<ArrayList<Value>, Value>> callables; 
    Type t; 

    public Function<ArrayList<Value>, Value> getFunction(CallableInfo name){
        this.callables.get(CallableInfo); 
    }

    public Value getObject(String object){
        if(t == Type.OBJECT){
            typeInfo.
        }

    }



} 