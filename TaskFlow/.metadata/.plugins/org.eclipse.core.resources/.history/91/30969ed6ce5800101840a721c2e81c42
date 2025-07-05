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

// Acțiune pentru adăugarea unui task într-o categorie selectată.
public class AddTaskAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală a aplicației

	public AddTaskAction(MainFrame frame) {
		super("Add Task"); // Text afișat în UI pentru această acțiune
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 1) Obține categoria selectată
		Category cat = frame.getSelectedCategory();
		if (cat == null) {
			// Afișează mesaj dacă nu e selectată nicio categorie
			JOptionPane.showMessageDialog(frame,
				"Select a category first.",
				"Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// 2) Solicită titlu și descriere task
		String title = JOptionPane.showInputDialog(frame, "Task title:");
		if (title == null || title.isBlank())
			return; // Titlu invalid sau anulare

		String desc = JOptionPane.showInputDialog(frame, "Task description:");
		if (desc == null)
			desc = ""; // Descriere implicită goală

		// 3) Solicită data limită (formate: yyyy-MM-dd sau yyyyMMdd)
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
			// Afișează eroare dacă formatul e invalid
			JOptionPane.showMessageDialog(frame,
				"Invalid date format.\nUse yyyy-MM-dd or yyyyMMdd",
				"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 4) Alege culoarea task-ului (implicit roșu)
		Color chosen = JColorChooser.showDialog(
			frame, "Choose task color", Color.RED);
		String hex = (chosen == null)
			? "#A00000"
			: String.format("#%02x%02x%02x",
				  chosen.getRed(), chosen.getGreen(), chosen.getBlue());

		// 5) Creează și adaugă task-ul
		Task t = new Task(
			title.trim(),
			desc.trim(),
			dueDate,
			false, // Task-ul nu e finalizat
			hex
		);
		frame.getTaskService().addTask(cat, t);

		// 6) Reîmprospătează UI-ul pentru categoria curentă
		frame.refreshAfterChange(cat);
	}
}
