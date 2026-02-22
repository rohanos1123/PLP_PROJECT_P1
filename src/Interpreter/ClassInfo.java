package Interpreter;
import Grammar.DelphiParser;

import java.util.*;
import java.util.function.Function;

public class ClassInfo {
    DelphiParser.ClassDefinitionContext classDefContext;
    HashMap<String, Value> attributeMap = new HashMap<>();
    HashMap<CallableInfo, Function<ArrayList<Value>, Value>> methodMap = new HashMap<>();
}
