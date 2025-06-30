package com.taskflow.action;

import com.taskflow.model.RootGroup;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EditRootAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;

    public EditRootAction(MainFrame frame) {
        super("Edit Root");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RootGroup rg = frame.getSelectedRoot();
        if (rg == null) return;

        String name = JOptionPane.showInputDialog(frame, "Root name:", rg.getName());
        if (name == null || name.isBlank()) return;

        Color chosen = JColorChooser.showDialog(frame, "Choose root color", Color.decode(rg.getColorHex()));
        String hex = (chosen == null)
            ? rg.getColorHex()
            : String.format("#%02x%02x%02x", chosen.getRed(), chosen.getGreen(), chosen.getBlue());

        rg.setName(name.trim());
        rg.setColorHex(hex);
        frame.refreshTreeAll();
    }
}
