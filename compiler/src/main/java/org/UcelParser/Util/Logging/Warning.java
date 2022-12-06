package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class Warning extends Log {
    public Warning(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

    public Warning(int lineStart, int lineStop, int charStart, int charStop, String message) {
        super(lineStart, lineStop, charStart, charStop, message);
    }
}
