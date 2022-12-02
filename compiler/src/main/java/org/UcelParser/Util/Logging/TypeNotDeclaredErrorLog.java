package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeNotDeclaredErrorLog extends ReferenceErrorLog {
    public TypeNotDeclaredErrorLog(ParserRuleContext ctx, String name) {
        super(ctx, "Type " + name + " is not declared in scope");
    }

    public TypeNotDeclaredErrorLog(int lineStart, int lineStop, int charStart, int charStop, String name) {
        super(lineStart, lineStop, charStart, charStop, "Type " + name + " is not declared in scope");
    }
}
