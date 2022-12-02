package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeConstructorNotFound extends TypeErrorLog {
    public TypeConstructorNotFound(ParserRuleContext ctx, String name) {
        super(ctx, "Constructor for type " + name + " could not be found");
    }

    public TypeConstructorNotFound(int lineStart, int lineStop, int charStart, int charStop, String name) {
        super(lineStart, lineStop, charStart, charStop, "Constructor for type " + name + " could not be found");
    }
}
