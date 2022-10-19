package org.UcelPlugin;

import com.uppaal.plugin.PluginWorkspace;

import javax.swing.*;
import java.awt.*;

public class UcelEditorWorkspace implements PluginWorkspace {

    public UcelEditorWorkspace() {

    }

    private UcelEditorUI editorUi = new UcelEditorUI();
    private UcelEditorUI getUi() {
        return editorUi;
    }

    @Override
    public String getTitle() {
        return "UCEL";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getTitleToolTip() {
        return "Uppaal Component Extension Language - Editor";
    }

    @Override
    public Component getComponent() {
        return getUi().getJPanel();
    }

    @Override
    public int getDevelopmentIndex() {
        return 0;
    }

    @Override
    public boolean getCanZoom() {
        return false;
    }

    @Override
    public boolean getCanZoomToFit() {
        return false;
    }

    @Override
    public double getZoom() {
        return 1;
    }

    @Override
    public void setZoom(double v) {

    }

    @Override
    public void zoomToFit() {

    }

    @Override
    public void zoomIn() {

    }

    @Override
    public void zoomOut() {

    }

    @Override
    public void setActive(boolean b) {

    }
}
