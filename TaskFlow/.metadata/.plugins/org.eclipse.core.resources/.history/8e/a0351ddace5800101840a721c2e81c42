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

// Permite editarea unui task selectat, inclusiv titlu, descriere, dată-limită și culoare.
public class EditTaskAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală a aplicației

	public EditTaskAction(MainFrame frame) {
		super("Edit Task"); // Text afișat în UI pentru această acțiune
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// 1) Obține task-ul selectat în UI
		Task t = frame.getSelectedTask();
		if (t == null) {
			// Dacă nu există task selectat, aflșează mesaj și oprește
			JOptionPane.showMessageDialog(frame,
				"Select a task to edit.",
				"Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// 2) Solicită editarea titlului și descrierii, preumplând cu valorile curente
		String title = JOptionPane.showInputDialog(
			frame, "Task title:", t.getTitle());
		if (title == null || title.isBlank())
			return; // Titlu invalid sau anulare

		String desc = JOptionPane.showInputDialog(
			frame, "Task description:", t.getDescription());
		if (desc == null)
			desc = ""; // Descriere implicită goală

		// 3) Solicită editarea datei-limită (formate acceptate: yyyy-MM-dd sau yyyyMMdd)
		String currentDue = new SimpleDateFormat("yyyy-MM-dd").format(t.getDueDate().getTime());
		String dueStr = JOptionPane.showInputDialog(
			frame, "Due date (yyyy-MM-dd or yyyyMMdd):", currentDue);
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

		// 4) Afișează selectorul de culoare pentru task (implicit culoarea curentă)
		Color chosen = JColorChooser.showDialog(
			frame, "Choose task color", Color.decode(t.getColorHex()));
		String hex = (chosen == null)
			? t.getColorHex()
			: String.format("#%02x%02x%02x",
				 chosen.getRed(), chosen.getGreen(), chosen.getBlue());

		// 5) Aplică modificările pe obiectul task și salvează
		t.setTitle(title.trim());
		t.setDescription(desc.trim());
		t.setDueDate(dueDate);
		t.setColorHex(hex);
		frame.getTaskService().updateTask(t);

		// 6) Reîmprospătează UI-ul pentru categoria curentă
		Category cat = frame.getSelectedCategory();
		if (cat != null)
			frame.refreshAfterChange(cat);
	}
}
