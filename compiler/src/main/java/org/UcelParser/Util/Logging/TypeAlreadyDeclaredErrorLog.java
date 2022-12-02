package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class TypeAlreadyDeclaredErrorLog extends ReferenceErrorLog{
    public TypeAlreadyDeclaredErrorLog(ParserRuleContext ctx, String name) {
        super(ctx, "Type " + name + " is already declared in scope");
    }

    public TypeAlreadyDeclaredErrorLog(int lineStart, int lineStop, int charStart, int charStop, String name) {
        super(lineStart, lineStop, charStart, charStop, "Type " + name + " is already declared in scope");
    }
}
