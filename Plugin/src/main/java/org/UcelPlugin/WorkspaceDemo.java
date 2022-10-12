package org.UcelPlugin;

import com.uppaal.plugin.PluginWorkspace;

import javax.swing.*;
import java.awt.*;

/**
 * The class describes the tab to be loaded into UPPAAL GUI.
 */
public class WorkspaceDemo implements PluginWorkspace
{
    static final String hello = "Hello Uppaal Plugin World!";
    private JPanel panel = new JPanel();
    private JColorChooser colorPicker = new JColorChooser();
    private JButton colorButton = new JButton("Set Color");

    public WorkspaceDemo() {

        //panel = new EditorBase(panel, "ost1", "ost2")


        panel.add(new JLabel(hello));
        panel.add(new JButton("button1"));
        panel.add(new JButton("button2"));


        colorButton.addActionListener(e -> {
            //colorPicker.setVisible(true);
            //Color color=JColorChooser.showDialog(panel,"Choose",Color.CYAN);
            //panel.setBackground(color);
            panel.setBackground(colorPicker.getColor());
        });
        panel.add(colorButton);

        panel.add(colorPicker);
        colorPicker.addPropertyChangeListener(evt -> {
            panel.setBackground(colorPicker.getColor());
        });
        //panel.add(colorPicker);

    }

    /** The title for the tab title. */
    public String getTitle() { return "Plugin Demo"; }

    /** Icon image for the tab title. */
    public Icon getIcon() { return null; }

    /** Tooltip when mouse hovers above the tab title. */
    public String getTitleToolTip() { return hello; }

    /** The component to be placed into the tab. */
    public Component getComponent() { return panel; }
    /**
     * The development index controls the placement of tab among other tabs.
     * Editor is at 100,
     * Symbolic simulator is at 200,
     * Concrete simulator is at 300,
     * Verifier is at 400,
     * Test generator is at 500.
     */
    public int getDevelopmentIndex() { return 50; }

    /** Enables/disables the zoom tools accordingly. */
    public boolean getCanZoom() { return true; }
    public boolean getCanZoomToFit() { return true; }
    public double getZoom() { return 1.0; }

    /** Called when user asked to zoom to specific scale. */
    public void setZoom(double value) {
        System.out.println("zoom to "+value);
    }

    /** Called when user asked to zoom to fit into the area dedicated for this tab component. */
    public void zoomToFit() {
        System.out.println("zoom to fit");
    }

    /** Called when user asked to increase the scale. */
    public void zoomIn() {
        System.out.println("zoom-in");
    }

    /** Called when user asked to decrease the scale. */
    public void zoomOut() {
        System.out.println("zoom-out");
    }

    /**
     * Called whenever the status of this workspace changes.
     * @param selected indicates whether the workspace is openned.
     */
    public void setActive(boolean selected)
    {
        if (selected) {
            System.out.println(getTitle()+" is active");
        } else {
            System.out.println(getTitle()+" is inactive");
        }
    }
}
