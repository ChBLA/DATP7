package org.UcelPlugin;

import org.UcelParser.Util.Logging.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.function.Consumer;

public class UcelEditorUI extends BaseWorkspace {
    public UcelEditorUI() {
        setJPanel(new JPanel());
        this.loadCompileButton();
        this.loadCompileButtonX100();
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

    //region Errorlog
    JTextArea errorLogTextPanel;

    private void loadErrorLog() {
        errorLogTextPanel = new JTextArea("");

        JScrollPane scrollPane = new JScrollPane(errorLogTextPanel);
        jPanel.add(scrollPane);
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(400,500));
    }
    public void resetErrorLog() {
        errorLogTextPanel.setText("");
    }
    public void setErrors(ArrayList<Log> logs) {
        var collectiveErrorLog = "";
        for(var log: logs) {
            collectiveErrorLog += log.getMessage() + "\n";
        }
        errorLogTextPanel.setText(collectiveErrorLog);
    }
    //endregion
}
