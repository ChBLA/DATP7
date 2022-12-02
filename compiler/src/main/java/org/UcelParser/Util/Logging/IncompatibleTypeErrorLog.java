package org.UcelParser.Util.Logging;

import org.UcelParser.Util.Type;
import org.antlr.v4.runtime.ParserRuleContext;

public class IncompatibleTypeErrorLog extends TypeErrorLog {
    public IncompatibleTypeErrorLog(ParserRuleContext ctx, Type left, Type right, String operator) {
        super(ctx, String.format("Cannot apply %s between types %s and %s", operator, left, right));
    }

    public IncompatibleTypeErrorLog(int lineStart, int lineStop, int charStart, int charStop, Type left, Type right, String operator) {
        super(lineStart, lineStop, charStart, charStop, String.format("Cannot apply operator %s between types %s and %s", operator, left, right));
    }
}
