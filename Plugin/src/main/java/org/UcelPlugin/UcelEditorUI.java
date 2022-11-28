package org.UcelPlugin;

import org.UcelParser.Util.Logging.Log;
import org.UcelPlugin.UiComponents.Button;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UcelEditorUI extends BaseWorkspace {
    public UcelEditorUI() {
        this.loadErrorLog();
    }

    @Override
    public String getTitle() { return "UCEL"; }
    @Override
    public String getTitleToolTip() {
        return "Uppaal Component Extension Language - Editor";
    }

    @Override
    public Component getComponent() {
        return jPanel;
    }

    private JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    public JPanel getJPanel() {
        return jPanel;
    }
    private void setJPanel(JPanel value) {
        jPanel = value;
    }


    private Button compileButton = new Button(jPanel, "Compile");
    public Button getCompileButton() {
        return compileButton;
    }

    private Button compileX100Button = new Button(jPanel, "Compile x100");
    public Button getCompileX100Button() {
        return compileX100Button;
    }

    private Button undoButton = new Button(jPanel, "Undo");
    public Button getUndoButton() {
        return undoButton;
    }

    private Button tryBuildButton = new Button(jPanel, "Try Build");
    public Button getTryBuildButton() {
        return tryBuildButton;
    }

    //region Errorlog
    JTextArea errorLogTextPanel;
    JScrollPane errorScrollBar;

    private void loadErrorLog() {
        errorLogTextPanel = new JTextArea("");

        errorScrollBar = new JScrollPane(errorLogTextPanel);
        jPanel.add(errorScrollBar);

        errorScrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        errorScrollBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resetErrorBlockSize();
    }
    void resetErrorBlockSize() {
        errorScrollBar.setBorder(BorderFactory.createEmptyBorder(30, 5, 5, 5));
        errorScrollBar.setPreferredSize(new Dimension(jPanel.getWidth()-10,jPanel.getHeight() - 35));
    }

    @Override
    public void setActive(boolean b) {
        resetErrorBlockSize();
    }

    public void resetErrorLog() {
        errorLogTextPanel.setText("");
    }
    public void setErrors(ArrayList<Log> logs) {
        var collectiveErrorLog = "";
        for(var log: logs) {
            collectiveErrorLog += log.getFancyMessage() + "\n";
        }
        setError(collectiveErrorLog);
    }

    public void setError(Exception ex) {
        String errText = ex.getMessage();
        for(var trace: ex.getStackTrace()) {
            errText += "\n\t" + trace.toString();
        }

        errorLogTextPanel.setText(errText);
    }

    public void setError(String text) {
        errorLogTextPanel.setText(text);
    }

    public void setSuccess() {
        errorLogTextPanel.setText("Success");
    }
    //endregion
}
