package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class VariableAlreadyDeclaredErrorLog extends ReferenceErrorLog {
    public VariableAlreadyDeclaredErrorLog(ParserRuleContext ctx, String name) {
        super(ctx, "Variable " + name + " already declared in scope");
    }

    public VariableAlreadyDeclaredErrorLog(int lineStart, int lineStop, int charStart, int charStop, String name) {
        super(lineStart, lineStop, charStart, charStop, "Variable " + name + " already declared in scope");
    }
}
