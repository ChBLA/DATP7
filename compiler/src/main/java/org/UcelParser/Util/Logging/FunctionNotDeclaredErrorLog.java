package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class FunctionNotDeclaredErrorLog extends ReferenceErrorLog {
    public FunctionNotDeclaredErrorLog(ParserRuleContext ctx, String name) {
        super(ctx, "Variable " + name + " is not declared in scope");
    }

    public FunctionNotDeclaredErrorLog(int lineStart, int lineStop, int charStart, int charStop, String name) {
        super(lineStart, lineStop, charStart, charStop, "Variable " + name + " is not declared in scope");
    }
}
