import Interpreter.DelphiInterpreter;
import Interpreter.DelphiInterpreter.DelphiRuntimeError;
import Grammar.DelphiLexer;
import Grammar.DelphiParser;
import org.antlr.v4.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main{

    public static void main(String[] args) throws Exception{
        if (args.length != 1) {
            throw new RuntimeException("Invalid number of arguments provided. Please provide the file to run.");
        }

        CharStream input = CharStreams.fromFileName(args[0]);

        DelphiLexer lexer = new DelphiLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DelphiParser parser = new DelphiParser(tokens);

        ParseTree tree = parser.program();

        DelphiInterpreter interpreter = new DelphiInterpreter();
        try {
            interpreter.visit(tree);
        } catch (DelphiRuntimeError e) {
            System.err.println("Delphi Runtime Error: " + e.getMessage());
        }


    }



}