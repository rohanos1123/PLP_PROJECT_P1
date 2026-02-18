package Interpreter;


enum TYPE{
    INT,
    BOOLEAN,
    VOID
};

public class Value {


    private
        boolean isPrivate;

    public

        Object value;
        boolean isReference;
        TYPE type;

        public Value(Object value){
            this.value = value;
            this.isPrivate = false;
            this.type = TYPE.VOID;
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


        Integer toInteger(){
            if(this.value instanceof Integer){
                return (Integer)this.value;
            }
            else{
                throw new RuntimeException("Attempted to convert non-integer type to int");
            }
        }

        boolean isPrivate(){
            return this.isPrivate;
        }



}
