package org.UcelPlugin.UiComponents;

import java.awt.*;

public class LayoutGenerator {
    public static GridBagConstraints GetLayout(int x, int y, int width, int height) {
        var gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;

        gbc.weightx = width;
        gbc.weighty = height;

        return gbc;
    }
}
