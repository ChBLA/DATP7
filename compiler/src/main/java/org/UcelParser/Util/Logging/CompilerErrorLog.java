package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;

public class CompilerErrorLog extends ErrorLog {

    private StackTraceElement[] stackTrace;
    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public CompilerErrorLog(ParserRuleContext ctx, String message) {
        super(ctx, message);
        stackTrace = Arrays.stream(Thread.currentThread().getStackTrace()).skip(2).toArray(StackTraceElement[]::new);
    }
}
