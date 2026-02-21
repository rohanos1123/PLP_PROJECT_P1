package Interpreter;
import Grammar.DelphiParser;

import java.util.*;

public class ClassInfo {
    DelphiParser.ClassDefinitionContext classDefContext;
    HashMap<String, Value> attributeMap = new HashMap<>();
    HashMap<String, Value> constructorMap = new HashMap<>();
}
