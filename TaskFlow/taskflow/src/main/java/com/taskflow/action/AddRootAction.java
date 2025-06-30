package com.taskflow.action;

import com.taskflow.model.RootGroup;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddRootAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;

    public AddRootAction(MainFrame frame) {
        super("Add Root");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = JOptionPane.showInputDialog(frame, "Root name:");
        if (name == null || name.isBlank()) return;

        Color chosen = JColorChooser.showDialog(frame, "Choose root color", Color.BLACK);
        String hex = (chosen == null)
            ? "#000000"
            : String.format("#%02x%02x%02x", chosen.getRed(), chosen.getGreen(), chosen.getBlue());

        frame.getTaskService().addRoot(new RootGroup(name.trim(), hex));
        frame.refreshTreeAll();
    }
}
