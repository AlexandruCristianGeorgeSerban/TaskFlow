package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.model.Task;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddTaskAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;

    public AddTaskAction(MainFrame frame) {
        super("Add Task");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Category cat = frame.getSelectedCategory();
        if (cat == null) {
            JOptionPane.showMessageDialog(frame,
                "Select a category first.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String title = JOptionPane.showInputDialog(frame, "Task title:");
        if (title == null || title.isBlank()) return;

        String desc = JOptionPane.showInputDialog(frame, "Task description:");
        if (desc == null) desc = "";

        String dueStr = JOptionPane.showInputDialog(
            frame, "Due date (yyyy-MM-dd or yyyyMMdd):");
        Calendar dueDate;
        try {
            Date d;
            if (dueStr.contains("-")) {
                d = new SimpleDateFormat("yyyy-MM-dd").parse(dueStr);
            } else {
                d = new SimpleDateFormat("yyyyMMdd").parse(dueStr);
            }
            dueDate = Calendar.getInstance();
            dueDate.setTime(d);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(frame,
                "Invalid date format.\nUse yyyy-MM-dd or yyyyMMdd",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Color chosen = JColorChooser.showDialog(
            frame, "Choose task color", Color.RED);
        String hex = (chosen == null)
            ? "#A00000"
            : String.format("#%02x%02x%02x",
                  chosen.getRed(), chosen.getGreen(), chosen.getBlue());

        Task t = new Task(
            title.trim(),
            desc.trim(),
            dueDate,
            false,
            hex
        );
        frame.getTaskService().addTask(cat, t);
        frame.refreshAfterChange(cat);
    }
}
