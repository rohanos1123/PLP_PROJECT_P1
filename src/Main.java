import Interpreter.DelphiInterpreter;
import Grammar.DelphiLexer;
import Grammar.DelphiParser;
import org.antlr.v4.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main{

    public static void main(String[] args) throws Exception{

        CharStream input = CharStreams.fromFileName("test.pas");

        DelphiLexer lexer = new DelphiLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DelphiParser parser = new DelphiParser(tokens);

        ParseTree tree = parser.program();

        DelphiInterpreter interpreter = new DelphiInterpreter();
        interpreter.visit(tree);


    }



}