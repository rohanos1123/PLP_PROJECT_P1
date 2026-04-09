import Interpreter.DelphiInterpreter;
import Interpreter.DelphiInterpreter.delphiRuntimeError;
import grammar.DelphiLexer;
import grammar.DelphiParser;
import org.antlr.v4.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main{

    public static void main(String[] args) throws Exception{
        if (args.length != 2) {
            throw new RuntimeException("Invalid number of arguments provided. " +
                                       "Please provide the execution mode [compile | interpret] and the target file.");
        }

        CharStream input = CharStreams.fromFileName(args[1]);

        DelphiLexer lexer = new DelphiLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DelphiParser parser = new DelphiParser(tokens);

        ParseTree tree = parser.program();
        
        if (args[0].equals("interpret")) {
            DelphiInterpreter interpreter = new DelphiInterpreter();
            try {
                interpreter.visit(tree);
            } catch (delphiRuntimeError e) {
                System.err.println("delphi Runtime Error: " + e.getMessage());
            }
        }
        else if (args[0].equals("compile")) {
            // compile here
        }
        else {
            throw new RuntimeException("Bad execution mode provided.");
        }
    }

}