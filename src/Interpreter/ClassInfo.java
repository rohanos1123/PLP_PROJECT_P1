package Interpreter;

import java.util.*;
import java.util.function.Function;

public class ClassInfo {
    enum InheritanceType {
        ABSTRACT,
        SEALED,
        DEFAULT
    }

    InheritanceType inheritanceType = InheritanceType.DEFAULT;
    HashMap<String, Value> attributeMap = new HashMap<>();
    HashMap<CallableInfo, Function<ArrayList<Value>, Value>> methodMap = new HashMap<>();
}
