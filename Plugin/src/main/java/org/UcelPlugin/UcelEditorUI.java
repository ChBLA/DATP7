package org.UcelPlugin;

import org.UcelParser.Util.Logging.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.function.Consumer;

public class UcelEditorUI extends BaseWorkspace {
    public UcelEditorUI() {
        setJPanel(new JPanel(new FlowLayout(FlowLayout.LEFT)));
        this.loadCompileButton();
        this.loadCompileButtonX100();
        this.loadUndoButton();
        this.loadTryBuildButton();
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

    private JPanel jPanel;
    public JPanel getJPanel() {
        return jPanel;
    }
    private void setJPanel(JPanel value) {
        jPanel = value;
    }

    //region Compile Button
    private JButton compileButton;
    private void loadCompileButton() {
        compileButton = new JButton("Compile");
        jPanel.add(compileButton);
    }
    public void addCompileAction(Consumer onCompile) {
        compileButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCompile.accept(e);
            }
        });
    }
    //endregion

    //region Compile Button x100
    private JButton compileButtonX100;
    private void loadCompileButtonX100() {
        compileButtonX100 = new JButton("Compile x100");
        jPanel.add(compileButtonX100);
    }
    public void addCompileActionX100(Consumer onCompile) {
        compileButtonX100.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCompile.accept(e);
            }
        });
    }
    //endregion

    //region Undo Button
    private JButton undoButton;
    private void loadUndoButton() {
        undoButton = new JButton("Undo");
        jPanel.add(undoButton);
    }
    public void addUndoAction(Consumer onUndo) {
        undoButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onUndo.accept(e);
            }
        });
    }
    //endregion

    //region "Try Build" Button
    private JButton tryBuildButton;
    private void loadTryBuildButton() {
        tryBuildButton = new JButton("Try Build");
        jPanel.add(tryBuildButton);
    }
    public void addTryBuildAction(Consumer onTryBuild) {
        tryBuildButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onTryBuild.accept(e);
            }
        });
    }
    //endregion

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
        errorLogTextPanel.setText(collectiveErrorLog);
    }

    public void setSuccess() {
        errorLogTextPanel.setText("Success");
    }
    //endregion
}
