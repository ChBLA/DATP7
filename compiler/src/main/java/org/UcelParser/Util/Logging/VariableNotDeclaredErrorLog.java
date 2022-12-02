package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class VariableNotDeclaredErrorLog extends ReferenceErrorLog {
    public VariableNotDeclaredErrorLog(ParserRuleContext ctx, String name) {
        super(ctx, "Variable " + name + " is not declared in scope");
    }

    public VariableNotDeclaredErrorLog(int lineStart, int lineStop, int charStart, int charStop, String name) {
        super(lineStart, lineStop, charStart, charStop, "Variable " + name + " is not declared in scope");
    }
}
