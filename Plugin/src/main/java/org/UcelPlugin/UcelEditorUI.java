package org.UcelPlugin;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class UcelEditorUI {
    public UcelEditorUI(UcelEditorWorkspace workspace) {
        setJPanel(new JPanel());
        this.loadCompileButton();
        this.workspace = workspace;
    }

    private UcelEditorWorkspace workspace;
    private JPanel jPanel;
    public JPanel getJPanel() {
        return jPanel;
    }
    private void setJPanel(JPanel value) {
        jPanel = value;
    }

    private JButton compileButton;
    private void loadCompileButton() {
        compileButton = new JButton("Compile");
        compileButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workspace.compileCurrentProject();
            }
        });
        jPanel.add(compileButton);
    }

}
