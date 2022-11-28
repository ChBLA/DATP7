package org.UcelParser.Util.Exception;

import org.UcelParser.Util.Logging.ErrorLog;
import org.UcelParser.Util.Logging.Log;

import java.util.ArrayList;

public class ErrorsFoundException extends Exception {
    public ErrorsFoundException() {
        super();
    }

    private ArrayList<Log> logs;
    public ArrayList<Log> getLogs() {
        return logs;
    }

    public ErrorsFoundException(String msg, ArrayList<Log> logs) {
        super(msg);
        this.logs = logs;
    }

    public ErrorsFoundException(String msg) {
        this(msg, new ArrayList<>() {{
            new ErrorLog(null, msg);
        }});
    }
}
