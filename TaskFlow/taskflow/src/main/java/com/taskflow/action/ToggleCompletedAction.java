package com.taskflow.action;

import com.taskflow.model.Task;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ToggleCompletedAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;

    public ToggleCompletedAction(MainFrame frame) {
        super("Toggle Completed");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Task t = frame.getSelectedTask();
        if (t == null) return;
        t.setCompleted(!t.isCompleted());
        frame.getTaskService().updateTask(t);
        frame.refreshAfterChange(frame.getSelectedCategory());
    }
}
