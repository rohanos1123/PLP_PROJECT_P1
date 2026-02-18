package Interpreter;
import Grammar.DelphiParser;

import java.util.*;

public class ClassInfo {
    HashMap<String, DelphiParser.ClassDefinitionContext> classDefinitionContextHashMap;
    HashMap<String, DelphiParser.FunctionDeclarationContext> functionMap;
    HashMap<String, DelphiParser.ConstructorDeclarationContext> constructorMap;


}
