package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class MissingReferenceErrorLog extends CompilerErrorLog {
    public MissingReferenceErrorLog(ParserRuleContext ctx, String context) {
        super(ctx, "Missing reference for " + context);
    }
}
