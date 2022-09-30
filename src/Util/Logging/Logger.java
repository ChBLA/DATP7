import java.util.ArrayList;

public class Logger {

    private ArrayList<Log> logs;
    private int errorCount, warningCount;

    public Logger() {
        logs = new ArrayList<>();
        errorCount = 0;
        warningCount = 0;
    }

    public void log(Log log) {
        if(log instanceof ErrorLog) errorCount++;

        logs.add(log);
    }

    public int GetErrorCount() {
        return errorCount;
    }

    public void printLogs() {
        System.out.println(formatLogs());
    }

    public String formatLogs() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("The compiled code has %d warnings and %d errors\n"),
                                            warningCount, errorCount);

        for (Log log : logs) {
            String logString = "";
            if(log instanceof ErrorLog) logString = formatErrorLog((ErrorLog) log);
            else logString = formatLog(log);

            stringBuilder.append(logString);
            stringBuilder.append('\n');
        }

        return stringBuilder.toString();
    }
    
    private String formatLog(Log log) {
        return log.getMessage();
    }

    private String formatErrorLog(ErrorLog log) {
        return log.getMessage();
    }

}
