package org.UcelPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class UcelEditorUI extends BaseWorkspace {
    public UcelEditorUI() {
        setJPanel(new JPanel());
        this.loadCompileButton();
        this.loadCompileButtonX100();
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

}
