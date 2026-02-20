package Interpreter;
import Grammar.DelphiParser;

import java.util.*;

public class ClassInfo {
    DelphiParser.ClassDefinitionContext classDefContext;
    HashMap<String, DelphiParser.ConstructorDeclarationContext> constructorMap = new HashMap<>();

    HashMap<String, Value> attributeMap = new HashMap<>();




}
