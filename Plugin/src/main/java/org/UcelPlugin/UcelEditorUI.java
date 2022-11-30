package org.UcelPlugin;

import org.UcelPlugin.UiComponents.Button;
import org.UcelPlugin.UiComponents.LayoutGenerator;
import org.UcelPlugin.UiComponents.StatusArea;

import javax.swing.*;
import java.awt.*;

public class UcelEditorUI extends BaseWorkspace {
    public UcelEditorUI() {
//        var btn = new JButton("Test");
//        jPanel.add(btn, BorderLayout.PAGE_START);
        var gbc = new GridBagConstraints();

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

    private JPanel jPanel = new JPanel(new GridBagLayout());
    private JPanel topPanel = new JPanel(new GridBagLayout()) {{
        jPanel.add(this, LayoutGenerator.GetLayout(0,0,1,1));
    }};
    private JPanel bottomPanel = new JPanel(new GridBagLayout()) {{
        jPanel.add(this, LayoutGenerator.GetLayout(0,1,1,9));
    }};


    private Button compileButton = new Button(topPanel, LayoutGenerator.GetLayout(0,0,1,1), "Compile");
    public Button getCompileButton() {
        return compileButton;
    }

    private Button sendToSimulatorButton = new Button(topPanel, LayoutGenerator.GetLayout(1,0,1,1), "Send to Simulator");
    public Button getSendToSimulatorButton() {
        return sendToSimulatorButton;
    }

    private Button undoButton = new Button(topPanel, LayoutGenerator.GetLayout(2,0,1,1), "Undo");
    public Button getUndoButton() {
        return undoButton;
    }

    private Button tryBuildButton = new Button(topPanel, LayoutGenerator.GetLayout(3,0,1,1), "Try Build");
    public Button getTryBuildButton() {
        return tryBuildButton;
    }

    private StatusArea statusArea = new StatusArea(bottomPanel, LayoutGenerator.GetLayout(0,0,1,1));
    public StatusArea getStatusArea() {
        return statusArea;
    }
}
