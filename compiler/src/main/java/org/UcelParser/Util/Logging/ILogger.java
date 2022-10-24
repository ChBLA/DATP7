package org.UcelParser.Util.Logging;

public interface ILogger {

    void setSource(String source);
    void log(Log log);
    boolean hasErrors();
    int getErrorCount();
    void printLogs();

}
