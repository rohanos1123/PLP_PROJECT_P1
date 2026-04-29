import Interpreter.DelphiInterpreter;
import Util.DelphiError;
import grammar.DelphiLexer;
import grammar.DelphiParser;
import org.antlr.v4.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.v4.runtime.tree.ParseTree;

import Generator.DelphiGenerator;

public class Main{

    public static void main(String[] args) throws Exception{
        if (args.length != 2) {
            throw new RuntimeException("Invalid number of arguments provided. " +
                                       "Please provide the execution mode [compile | interpret] and the target file.");
        }

        String filename = args[1];
        CharStream input = CharStreams.fromFileName(filename);

        DelphiLexer lexer = new DelphiLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DelphiParser parser = new DelphiParser(tokens);

        ParseTree tree = parser.program();
        
        if (args[0].equals("interpret")) {
            DelphiInterpreter interpreter = new DelphiInterpreter();
            try {
                interpreter.visit(tree);
            } catch (DelphiError e) {
                System.err.println("Delphi Runtime Error: " + e.getMessage());
            }
        }
        else if (args[0].equals("compile")) {
            try {
                if (filename.startsWith("tests/")) {
                    filename = filename.replace("tests/", "output/");
                }
                DelphiGenerator generator = new DelphiGenerator(filename.replace(".pas", ".ll"));
                generator.visit(tree);
            } catch (DelphiError e) {
                System.err.println("Delphi Compilation Error: " + e.getMessage());
            }
        }
        else {
            throw new RuntimeException("Invalid execution mode: " + args[0]);
        }
    }

}