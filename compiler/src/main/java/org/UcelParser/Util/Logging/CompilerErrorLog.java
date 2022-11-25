package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class CompilerErrorLog extends ErrorLog {

    private StackTraceElement[] stackTrace;
    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public CompilerErrorLog(ParserRuleContext ctx, String message) {
        super(ctx, message);
        stackTrace = Thread.currentThread().getStackTrace();
    }
}
