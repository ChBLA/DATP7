package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;

public class InfoLog extends Log {
    public InfoLog(String message) {
        super(null, message);
        this.msgType = "Info";
    }

    @Override
    public String getFancyMessage() {
        return msgType + ": " + this.getMessage();
    }
}
