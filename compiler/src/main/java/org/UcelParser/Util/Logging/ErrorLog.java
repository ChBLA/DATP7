package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class ErrorLog extends Log {

    public ErrorLog(ParserRuleContext ctx, String message) {
        super(ctx, message);
    }

}
