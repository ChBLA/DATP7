package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import static com.ibm.icu.impl.Utility.repeat;

public class Log {

    private String message;
    private int lineStart, charStart, lineStop, charStop;
    private ParserRuleContext ctx;
    public ParserRuleContext getCtx() {
        return ctx;
    }

    public Log(ParserRuleContext ctx, String message) {
        this.message = message;
        this.ctx = ctx;
    }

    public void unpack() {
        Token start = ctx.getStart();
        Token stop = ctx.getStop();

        this.lineStart = start.getLine() - 1;
        this.lineStop = stop.getLine() - 1;
        this.charStart = start.getCharPositionInLine();
        this.charStop = stop.getCharPositionInLine();
    }

    public String getMessage() {
        return message;
    }

    public String getFancyMessage() {
        String[] lines = getCtx().getStart().getTokenSource().getInputStream().toString()
                .split("\\n\\r?");

        if(getLineStart() == getLineStop()) {
            int line = (getLineStart() + 1), character = (getCharStart() + 1), width = getCharStop() - getCharStart() + 1;
            if (width > 1) width += 1;
            return "Error at " + line + ":" + character + ": " + getMessage()
                    + "\n" + lines[getLineStart()] +
                    "\n" + repeat(" ", getCharStart()) +
                    repeat("^", width);
        }

        return getMessage();
    }

    public int getLineStart() {
        return lineStart;
    }

    public int getLineStop() {
        return lineStop;
    }

    public int getCharStart() {
        return charStart;
    }

    public int getCharStop() {
        return charStop;
    }
}
