package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class ReferenceErrorLog extends ErrorLog {
    public ReferenceErrorLog(ParserRuleContext ctx, String message) {
        super(ctx, "ReferenceError: " + message);
    }

    public ReferenceErrorLog(int lineStart, int lineStop, int charStart, int charStop, String message) {
        super(lineStart, lineStop, charStart, charStop, message);
    }
}
