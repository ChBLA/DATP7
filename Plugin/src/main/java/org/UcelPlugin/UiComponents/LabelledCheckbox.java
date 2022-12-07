package org.UcelPlugin.UiComponents;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.function.Consumer;

public class LabelledCheckbox {

    JPanel container;
    JCheckBox checkBox;
    JLabel label;
    boolean isChecked;
    public boolean getIsChecked() {
        return isChecked;
    }

    public LabelledCheckbox(JPanel parent, GridBagConstraints layout, String labelText, boolean startChecked) {
        container = new JPanel();
        checkBox = new JCheckBox();
        label = new JLabel(labelText);

        container.add(label);
        container.add(checkBox);

        parent.add(container, layout);

        this.isChecked = startChecked;
        addOnChange((isChecked) -> {
            this.isChecked = (boolean)isChecked;
        });
    }

    public void addOnChange(Consumer onChange) {
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isSelected = e.getStateChange()==1;
                onChange.accept(isSelected);
            }
        });
    }

}
