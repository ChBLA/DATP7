package IntegrationTests;

import org.UcelParser.Util.*;
import org.UcelParser.Util.Logging.ILogger;
import org.UcelParser.Util.Logging.Log;
import org.UcelParser.Util.Logging.Logger;

import java.util.ArrayList;

public class TestLogger implements ILogger {
    @Override
    public void setSource(String source) {

    }

    @Override
    public void log(Log log) {

    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    @Override
    public void printLogs() {

    }

    public ArrayList<Log> getLogs() {
        return new ArrayList<>();
    }
}
