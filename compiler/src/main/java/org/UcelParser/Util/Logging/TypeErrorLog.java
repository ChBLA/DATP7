package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeErrorLog extends ErrorLog {
    public TypeErrorLog(ParserRuleContext ctx, String message) {
        super(ctx, "TypeError: " + message);
    }

    public TypeErrorLog(int lineStart, int lineStop, int charStart, int charStop, String message) {
        super(lineStart, lineStop, charStart, charStop, message);
    }
}
