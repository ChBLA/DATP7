package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Arrays;

import static com.ibm.icu.impl.Utility.repeat;

public class Log {

    private String message;
    private int lineStart, charStart, lineStop, charStop;
    private boolean isPacked;
    private ParserRuleContext ctx;
    public ParserRuleContext getCtx() {
        return ctx;
    }

    public Log(ParserRuleContext ctx, String message) {
        saveLogCallerLocation();
        this.message = message;
        this.ctx = ctx;
        isPacked = true;
    }

    public Log(int lineStart, int lineStop, int charStart, int charStop, String message) {
        saveLogCallerLocation();
        this.lineStart = lineStart - 1;
        this.lineStop = lineStop - 1;
        this.charStart = charStart;
        this.charStop = charStop;
        isPacked = false;

        this.message = message;
    }

    public void unpack() {
        if(isPacked) {
            Token start = ctx.getStart();
            Token stop = ctx.getStop();

            if(start != null) {
                this.lineStart = start.getLine() - 1;
                this.charStart = start.getCharPositionInLine();
            } else {
                this.lineStart = 0;
                this.charStart = 0;
            }

            if(stop != null) {
                this.lineStop = stop.getLine() - 1;
                this.charStop = stop.getCharPositionInLine();
            } else {
                this.lineStop = 0;
                this.charStop = 0;
            }
        }
    }

    private StackTraceElement[] callerLocation;
    private void saveLogCallerLocation() {
        var stack = Thread.currentThread().getStackTrace();
        callerLocation = stack;
    }

    public String getMessage() {
        return message;
    }

    public String getFancyMessage() {
        var ctx = getCtx();
        if(ctx == null || ctx.getStart() == null || ctx.getStart().getTokenSource() == null)
            return getMessage();

        String[] lines = ctx.getStart().getTokenSource().getInputStream().toString()
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
