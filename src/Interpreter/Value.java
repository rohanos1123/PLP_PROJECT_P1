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
        boolean isPrivate;

    public

        Object value;
        TYPE type;

        public Value(Object value){
            this.value = value;
            this.isPrivate = false;
            this.type = TYPE.VOID;
        }

        public Value(Value oldV){
            this.value = oldV.value;
            this.type = oldV.type;
            this.isPrivate = oldV.isPrivate;
        }

        public Value(Object value, TYPE type){
            this.value = value;
            this.isPrivate = false;
            this.type = type;
        }

        public Value(Object value, TYPE type,  boolean isPrivate){
                this.value = value;
                this.isPrivate = isPrivate;
                this.type = type;
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
