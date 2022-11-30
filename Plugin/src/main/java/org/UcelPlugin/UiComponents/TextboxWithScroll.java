package org.UcelPlugin.UiComponents;

import javax.swing.*;
import java.awt.*;

public class TextboxWithScroll {
    private JTextArea textArea;
    private JScrollPane scrollPane;

    public TextboxWithScroll(JPanel panel, GridBagConstraints layout) {
        textArea = new JTextArea();
        panel.add(textArea);

        textArea = new JTextArea("");

        scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, layout);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    public void setVisible(boolean shouldShow) {
        textArea.setVisible(shouldShow);
    }

    public void setText(String text) {
        setVisible(true);
        textArea.setText(text);
    }

}
