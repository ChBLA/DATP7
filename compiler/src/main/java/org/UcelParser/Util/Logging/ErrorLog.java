package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class ErrorLog extends Log {

    public ErrorLog(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public ErrorLog(int lineStart, int lineStop, int charStart, int charStop, String message) {
        super(lineStart, lineStop, charStart, charStop, message);
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
