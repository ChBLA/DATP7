package org.UcelPlugin.UiComponents;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class StyledTextArea {

    private DefaultStyledDocument doc;
    private JTextPane textPane;
    private JScrollPane scrollPane;

    public StyledTextArea(JPanel panel, GridBagConstraints layout) {

        doc = new DefaultStyledDocument();

        textPane = new JTextPane(doc);

        scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, layout);
    }

    public void reset() {
        try {
            doc.remove(0, doc.getLength());
        }
        catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendString(String str, AttributeSet attributes) {
        try {
            doc.insertString(doc.getLength(), str, attributes);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    public void setText(String str, AttributeSet attributes) {
        reset();
        appendString(str, attributes);
    }

}
