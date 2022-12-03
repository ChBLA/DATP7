package org.UcelParser.Util.Logging;

import org.UcelParser.Util.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.stringtemplate.v4.ST;

import java.util.Arrays;
import java.util.List;

public class WrongTypeErrorLog extends TypeErrorLog {
    public WrongTypeErrorLog(ParserRuleContext ctx, Type expected, Type actual, String context) {
        super(ctx, String.format("Expected type %s for %s, got %s", expected, context, actual));
    }

    public WrongTypeErrorLog(ParserRuleContext ctx, List<Type> expected, Type actual, String context) {
        super(ctx, String.format("Expected type %s for %s, got %s", Arrays.toString(expected.toArray()), context, actual));
    }

    public WrongTypeErrorLog(ParserRuleContext ctx, String name, Type actual) {
        super(ctx, String.format("%s cannot be of type %s", name, actual));
    }

    public WrongTypeErrorLog(ParserRuleContext ctx, String expected, String actual, String context) {
        super(ctx, String.format("Expected type %s for %s, got %s", expected, context, actual));
    }

    public WrongTypeErrorLog(int lineStart, int lineStop, int charStart, int charStop, Type expected, Type actual, String context) {
        super(lineStart, lineStop, charStart, charStop, String.format("Expected type %s for %s, got %s", expected, context, actual));
    }
}
