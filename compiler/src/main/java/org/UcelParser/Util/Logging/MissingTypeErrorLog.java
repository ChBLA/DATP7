package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class MissingTypeErrorLog extends CompilerErrorLog {
    public MissingTypeErrorLog(ParserRuleContext ctx, String name) {
        super(ctx, "Type for " + name + " is missing or not assigned correctly");
    }
}
