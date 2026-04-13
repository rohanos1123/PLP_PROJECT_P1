package Util;
import org.antlr.v4.runtime.ParserRuleContext;

public class DelphiError extends RuntimeException {
    private static String createLogMsg(Object msg, ParserRuleContext ctx) {
        int line = 0, column = 0;
        if (ctx != null) {
            line = ctx.getStart().getLine();
            column = ctx.getStart().getCharPositionInLine();
        }
        return "Line: " + line + ", Column: " + column + ": " + msg.toString();
    }

    @SuppressWarnings("unused")
    private static void log(Object msg, ParserRuleContext ctx) {
        System.err.println(createLogMsg(msg, ctx));
    }

    public DelphiError(Object msg, ParserRuleContext ctx) {
        super(createLogMsg(msg, ctx));
    }
}