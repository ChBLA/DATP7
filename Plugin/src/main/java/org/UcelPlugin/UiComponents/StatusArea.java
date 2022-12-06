package org.UcelPlugin.UiComponents;

import org.UcelParser.Util.Logging.Log;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StatusArea {
    private TextboxWithScroll element;
    public StatusArea(JPanel panel, GridBagConstraints layout) {
        element = new TextboxWithScroll(panel, layout);
    }

    public void setCompiling() {
        element.setText("Compiling...");
    }

    public void setSuccess() {
        element.setText("Success");
    }

    public void setUndoSuccess() {
        element.setText("Undone Successfully");
    }

    public void setError(Throwable ex) {
        String errText = ex.getMessage();
        for(var trace: ex.getStackTrace()) {
            errText += "\n\t" + trace.toString();
        }

        element.setText(errText);
    }

    public void setErrors(ArrayList<Log> logs) {
        var collectiveErrorLog = "";
        for(var log: logs) {
            collectiveErrorLog += log.getFancyMessage() + "\n";
        }
        element.setText(collectiveErrorLog);
    }

}
