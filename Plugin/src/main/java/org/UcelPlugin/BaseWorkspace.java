package org.UcelPlugin;

import com.uppaal.plugin.PluginWorkspace;

import javax.swing.*;
import java.awt.*;

public class BaseWorkspace implements PluginWorkspace {
    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public String getTitleToolTip() {
        return null;
    }

    @Override
    public Component getComponent() {
        return null;
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
        return 0;
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
