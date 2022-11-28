package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class SyntaxErrorLog extends ErrorLog {
    public SyntaxErrorLog(int line, int charColumn, String message) {
        super(line, line, charColumn, charColumn+1, message);
    }


}
