package org.UcelPlugin.UiComponents;

import org.UcelParser.Util.Logging.ErrorLog;
import org.UcelParser.Util.Logging.InfoLog;
import org.UcelParser.Util.Logging.Log;
import org.UcelParser.Util.Logging.Warning;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import java.awt.*;
import java.util.ArrayList;

public class StatusArea {
    private StyledTextArea element;
    public StatusArea(JPanel panel, GridBagConstraints layout) {
        element = new StyledTextArea(panel, layout);
    }

    public void setCompiling() {
        element.setText("Compiling...", DEFAULT_STYLE);
    }


    private String checkmark = new String(Character.toChars(10003)); // ✓
    public void setSuccess() {
        element.setText(checkmark + " Success", SUCCESS_STYLE);
    }

    public void setUndoSuccess() {
        element.setText(checkmark + " Undone Successfully", SUCCESS_STYLE);
    }

    public void setError(Throwable ex) {
        element.setText(ex.getMessage(), ERROR_STYLE);
        for(var trace: ex.getStackTrace()) {
            element.appendString("\n\t" + trace.toString(), ERROR_STYLE);
        }
    }

    public void setLogs(ArrayList<Log> logs, boolean success) {
        if(success) {
            setSuccess();
            element.appendString("\n\n", DEFAULT_STYLE);
        }
        else {
            element.reset();
        }

        for(var log: logs)
            appendLog(log);
    }

    public void appendLog(Log log) {
        AttributeSet style;

        if(log instanceof ErrorLog)
            style = ERROR_STYLE;
        else if(log instanceof Warning)
            style = WARNING_STYLE;
        else if(log instanceof InfoLog)
            style = INFO_STYLE;
        else
            style = DEFAULT_STYLE;

        element.appendString(log.getFancyMessage() + "\n", style);
    }

    protected AttributeSet DEFAULT_STYLE = TextStyles.DEFAULT;
    protected AttributeSet ERROR_STYLE = TextStyles.RED;
    protected AttributeSet WARNING_STYLE = TextStyles.DARK_YELLOW;
    protected AttributeSet INFO_STYLE = TextStyles.BLUE;
    protected AttributeSet SUCCESS_STYLE = TextStyles.DARK_GREEN;

}
