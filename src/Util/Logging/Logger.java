import java.awt.*;
import java.util.ArrayList;

public class Logger {

    private ArrayList<Log> logs;
    private String[] lines;
    private int errorCount, warningCount;
    private boolean testMode;

    public Logger() {
        this("");
        testMode = true;
    }

    public Logger(String input) {
        testMode = false;
        logs = new ArrayList<>();
        errorCount = 0;
        warningCount = 0;
        lines = input.split("\n");
    }

    public void log(Log log) {
        if(log instanceof ErrorLog) errorCount++;

        logs.add(log);

        if(!testMode) log.unpack();
    }

    public int GetErrorCount() {
        return errorCount;
    }

    public void printLogs() {
        formatLogs(true);
    }

    public String formatLogs(boolean print) {
        StringBuilder stringBuilder = new StringBuilder();

        String firstLine = String.format("The compiled code has %d warnings and %d errors\n", warningCount, errorCount);
        stringBuilder.append(firstLine);
        fancyPrint(print, Color.Reset, firstLine);

        for (Log log : logs) {
            String logString = "";
            Color color = Color.White;
            if(log instanceof ErrorLog) {
                logString = formatErrorLog((ErrorLog) log);
                color = Color.Red;
            }
            else logString = formatLog(log);

            stringBuilder.append(logString);
            stringBuilder.append('\n');
            fancyPrint(print, color, logString);
        }

        return stringBuilder.toString();
    }



    private String formatLog(Log log) {
        return log.getMessage();
    }

    private String formatErrorLog(ErrorLog log) {
        if(log.getLineStart() == log.getLineStop()) {
            int line = (log.getLineStart() + 1), character = (log.getCharStart() + 1), width = log.getCharStop() - log.getCharStart() + 1;
            if (width > 1) width += 1;
            return "Error at " + line + ":" + character + ": " + log.getMessage() + "\n" + lines[log.getLineStart()] +
                    "\n" + repeat(" ", log.getCharStart()) +
                    repeat("^", width);
        }

        return log.getMessage();
    }

    private void fancyPrint(boolean print, Color color, String s) {
        if(print) System.out.println(color.getConsoleCode() + s + Color.Reset.getConsoleCode());
    }

    private String repeat(String s, int n) {
        String result = "";
        for (int i = 0; i < n; i++) result += s;
        return result;
    }

    private enum Color {
        Black(0) , Red(1), Green(2), Yellow(3),
        Blue(4), Purple(5), Cyan(6), White(7),
        Reset("\033[0m");

        private String consoleCode;
        private Color(int consoleCode){
            this.consoleCode = String.format("\033[0;3%dm", consoleCode);
        }

        private Color(String consoleCode){
            this.consoleCode = consoleCode;
        }

        public String getConsoleCode() {
            return consoleCode;
        }
    }

}
