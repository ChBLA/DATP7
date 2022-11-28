package org.UcelParser.ManualParser;

import org.UcelParser.UCELParser_Generated.UCELParser;
import org.UcelParser.Util.Logging.ILogger;
import org.UcelParser.Util.Logging.SyntaxErrorLog;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

public class ErrorListener extends BaseErrorListener {
    private ILogger logger;
    public ErrorListener(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        logger.log(new SyntaxErrorLog(line, charPositionInLine, msg));
    }

}
