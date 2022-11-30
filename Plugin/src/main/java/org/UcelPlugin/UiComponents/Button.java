package org.UcelPlugin.UiComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class Button {
    private JButton element;

    public Button(JPanel panel, GridBagConstraints layout, String text) {
        element = new JButton(text);
        panel.add(element, layout);
    }

    public void addOnClick(Consumer onClick) {
        element.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClick.accept(e);
            }
        });
    }

    public void addOnClickAsync(Consumer onClick) {
        addOnClick((e) -> {
            new Thread(() -> {
                onClick.accept(e);
            }).start();
        });
    }
}
