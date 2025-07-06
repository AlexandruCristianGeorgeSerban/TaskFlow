package com.taskflow.action;

import com.taskflow.model.Task;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;

// Comută starea de finalizare a unui task selectat.
public class ToggleCompletedAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală a aplicației

	public ToggleCompletedAction(MainFrame frame) {
		super("Toggle Completed"); // Text afișat în UI pentru această acțiune
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Task task = frame.getSelectedTask(); // Task-ul selectat
		if (task == null)
			return; // Oprește dacă nu există selecție

		task.setCompleted(!task.isCompleted()); // Inversează starea de finalizare
		frame.getTaskService().updateTask(task); // Salvează modificarea
		// Reîmprospătează UI-ul pentru categoria curentă
		frame.refreshAfterChange(frame.getSelectedCategory());
	}
}
