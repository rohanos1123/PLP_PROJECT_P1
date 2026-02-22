package Interpreter;

import java.util.*;

public class CallableInfo {
    String name;
    ArrayList<TYPE> parameterTypes = new ArrayList<>();
    boolean variadic = false;
    /* not relevant for identification */
    ArrayList<String> parameterNames = new ArrayList<>();
    TYPE returnType = TYPE.VOID;

    public CallableInfo(String name) {
        this.name = name;
    }

    public CallableInfo(String name, ArrayList<TYPE> parameterTypes) {
        this(name);
        this.parameterTypes = parameterTypes;
    }

    public CallableInfo(String name, ArrayList<TYPE> parameterTypes, boolean variadic) {
        this(name, parameterTypes);
        this.variadic = variadic;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CallableInfo)) return false;
        CallableInfo other = (CallableInfo) o;
        return this.name.equals(other.name)
            && (this.parameterTypes.equals(other.parameterTypes) || this.variadic == other.variadic)
            ;
    }

    @Override
    public int hashCode() {
        if (this.variadic) {
            return Objects.hash(this.name);
        }
        return Objects.hash(this.name, this.parameterTypes);
    }

}
