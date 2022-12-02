package org.UcelParser.Util.Logging;

import org.UcelParser.Util.Type;
import org.antlr.v4.runtime.ParserRuleContext;

public class WrongAssignmentTypeErrorLog extends TypeErrorLog {
    public WrongAssignmentTypeErrorLog(ParserRuleContext ctx, Type expected, Type actual) {
        super(ctx, String.format("Cannot assign type %s to instance of type %s", actual, expected));
    }

    public WrongAssignmentTypeErrorLog(ParserRuleContext ctx, String expected, String actual) {
        super(ctx, String.format("Cannot assign type %s to instance of type %s", actual, expected));
    }

    public WrongAssignmentTypeErrorLog(int lineStart, int lineStop, int charStart, int charStop, Type expected, Type actual) {
        super(lineStart, lineStop, charStart, charStop, String.format("Cannot assign type %s to instance of type %s", actual, expected));
    }
}
