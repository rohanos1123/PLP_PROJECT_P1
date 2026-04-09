package Interpreter;

import Util.TYPE;

public class Value {


    private boolean isPrivate = false;

    public Object value;
    public TYPE type = TYPE.VOID;
    public String identifier = "0"; // always an invalid identifier

    public Value(Object value){
        this.value = value;
    }

    public Value(Value oldV){
        this.value = oldV.value;
        this.type = oldV.type;
        this.isPrivate = oldV.isPrivate;
    }

    public Value(Object value, TYPE type){
        this(value);
        this.type = type;
    }

    public Value(Object value, TYPE type,  String identifier){
        this(value, type);
        this.identifier = identifier;
    }

    public Value(Object value, TYPE type, String identifier, boolean isPrivate){
        this(value, type, identifier);
        this.isPrivate = isPrivate;
    }

    public Integer asInteger(){
        if(this.value instanceof Integer){
            return (Integer)this.value;
        }
        else{
            throw new RuntimeException("Attempted to convert non-integer type to int");
        }
    }

    public String asString(){
        switch (type) {
            case INT:
                return String.valueOf(this.asInteger());
        
            case STRING:
                return this.value.toString();
            default:
                return "";
        }
        
    }

    public void setAccess(boolean isPrivate){
        this.isPrivate = isPrivate;
    }

    public void copyValue(Value writeValue){
        this.value = writeValue.value;
        this.type = writeValue.type;
    }

    // ONLY sets the value without chaging type or
    public void setValOnly(Object o){
        this.value = o;
    }

    public boolean isPrivate(){
        return this.isPrivate;
    }

}
