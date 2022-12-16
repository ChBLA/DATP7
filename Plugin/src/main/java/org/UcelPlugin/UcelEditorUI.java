package org.UcelPlugin;

import org.UcelPlugin.UiComponents.Button;
import org.UcelPlugin.UiComponents.LabelledCheckbox;
import org.UcelPlugin.UiComponents.LayoutGenerator;
import org.UcelPlugin.UiComponents.StatusArea;

import javax.swing.*;
import java.awt.*;

public class UcelEditorUI extends BaseWorkspace {
    public UcelEditorUI() {

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

    //region Top Panel
    //region Button list
    private JPanel topPanel = new JPanel(new GridBagLayout()) {{
        jPanel.add(this, LayoutGenerator.GetLayout(0,0,1,1));
    }};

    private Button compileToEditorButton = new Button(topPanel, LayoutGenerator.GetLayout(0,0,1,1), "Compile to Editor");
    public Button getCompileToEditorButton() {
        return compileToEditorButton;
    }

    private Button compileToEngineButton = new Button(topPanel, LayoutGenerator.GetLayout(1,0,1,1), "Compile to Engine");
    public Button getCompileToEngineButton() {
        return compileToEngineButton;
    }

    private Button undoButton = new Button(topPanel, LayoutGenerator.GetLayout(2,0,1,1), "Undo");
    public Button getUndoButton() {
        return undoButton;
    }

    private Button tryBuildButton = new Button(topPanel, LayoutGenerator.GetLayout(3,0,1,1), "Try Build");
    public Button getTryBuildButton() {
        return tryBuildButton;
    }

    //endregion
    //region Right side
    private LabelledCheckbox liveFeedbackCheckbox = new LabelledCheckbox(topPanel, LayoutGenerator.GetLayout(4,0,1,1), "Live Feedback", false);
    public LabelledCheckbox getLiveFeedbackCheckbox() {
        return liveFeedbackCheckbox;
    }

    private Button benchmarkButton = new Button(topPanel, LayoutGenerator.GetLayout(4,1,1,1), "Benchmark");
    public Button getBenchmarkButton() {
        return benchmarkButton;
    }
    //endregion
    //endregion

    //region Bottom Panel
    private JPanel bottomPanel = new JPanel(new GridBagLayout()) {{
        jPanel.add(this, LayoutGenerator.GetLayout(0,1,1,9));
    }};

    private StatusArea statusArea = new StatusArea(bottomPanel, LayoutGenerator.GetLayout(0,0,1,1));
    public StatusArea getStatusArea() {
        return statusArea;
    }
    //endregion
}
