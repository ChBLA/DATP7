package org.UcelPlugin;

import javax.swing.*;

public class UcelEditorUI {

    public UcelEditorUI() {
        setJPanel(new JPanel());
    }

    private JPanel jPanel;
    public JPanel getJPanel() {
        return jPanel;
    }
    private void setJPanel(JPanel value) {
        jPanel = value;
    }
}
