package org.UcelParser.Util.Logging;

import java.util.ArrayList;

public interface ILogger {

    void setSource(String source);
    void log(Log log);
    boolean hasErrors();
    int getErrorCount();
    void printLogs();
    ArrayList<Log> getLogs();
}
