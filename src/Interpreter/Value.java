package Interpreter;

enum TYPE{
    INT,        // Integer
    STRING,     // String
    ARRAY,      // Array
    REFERENCE,  // Object Reference
    FPTR,       // Function/Method Pointer
    VOID        // Unassigned (void)
};

public class Value {


    private
        boolean isPrivate = false;

    public
        Object value;
        TYPE type = TYPE.VOID;
        String identifier = "0"; // always an invalid identifier
    
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

        Integer asInteger(){
            if(this.value instanceof Integer){
                return (Integer)this.value;
            }
            else{
                throw new RuntimeException("Attempted to convert non-integer type to int");
            }
        }

        String asString(){
            return String.valueOf(this.asInteger());
        }

        void setAccess(boolean isPrivate){
            this.isPrivate = isPrivate;
        }

        void copyValue(Value writeValue){
            this.value = writeValue.value;
            this.type = writeValue.type;
        }

        // ONLY sets the value without chaging type or
        void setValOnly(Object o){
            this.value = o;
        }




    boolean isPrivate(){
        return this.isPrivate;
    }



}
