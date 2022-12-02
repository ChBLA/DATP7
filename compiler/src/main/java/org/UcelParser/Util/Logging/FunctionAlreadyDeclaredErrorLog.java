package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class FunctionAlreadyDeclaredErrorLog extends ReferenceErrorLog{
    public FunctionAlreadyDeclaredErrorLog(ParserRuleContext ctx, String name) {
        super(ctx, "Function " + name + " is already declared in scope");
    }

    public FunctionAlreadyDeclaredErrorLog(int lineStart, int lineStop, int charStart, int charStop, String name) {
        super(lineStart, lineStop, charStart, charStop, "Function " + name + " is already declared in scope");
    }
}
