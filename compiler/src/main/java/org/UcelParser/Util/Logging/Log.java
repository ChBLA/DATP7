package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Log {

    private String message;
    private int lineStart, charStart, lineStop, charStop;
    private ParserRuleContext ctx;

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
