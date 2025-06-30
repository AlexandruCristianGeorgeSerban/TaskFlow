package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditCategoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;

    public EditCategoryAction(MainFrame frame) {
        super("Edit Category");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Category cat = frame.getSelectedCategory();
        if (cat == null) return;

        String name = JOptionPane.showInputDialog(frame, "Category name:", cat.getName());
        if (name == null || name.isBlank()) return;

        Color chosen = JColorChooser.showDialog(frame, "Choose category color", Color.decode(cat.getColor()));
        String hex = (chosen == null)
            ? cat.getColor()
            : String.format("#%02x%02x%02x", chosen.getRed(), chosen.getGreen(), chosen.getBlue());

        cat.setName(name.trim());
        cat.setColor(hex);
        frame.refreshTreeAll();
    }
}
