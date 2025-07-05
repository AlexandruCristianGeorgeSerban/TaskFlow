package com.taskflow.action;

import com.taskflow.model.RootGroup;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// Acțiune pentru adăugarea unui RootGroup nou.
public class AddRootAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală a aplicației

	public AddRootAction(MainFrame frame) {
		super("Add Root"); // Textul din UI pentru această acțiune
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Solicită numele noului root
		String name = JOptionPane.showInputDialog(frame, "Root name:");
		if (name == null || name.isBlank())
			return; // Oprim execuția dacă inputul e invalid

		// Afișează selectorul de culoare (implicit negru)
		Color chosen = JColorChooser.showDialog(frame, "Choose root color", Color.BLACK);
		// Convertim culoarea în format hex; dacă e null, folosim negru
		String hex = (chosen == null)
			? "#000000"
			: String.format("#%02x%02x%02x", chosen.getRed(), chosen.getGreen(), chosen.getBlue());

		// Adaugă noul root prin serviciul de task-uri
		frame.getTaskService().addRoot(new RootGroup(name.trim(), hex));
		// Reîmprospătează arborele din UI pentru a afișa modificările
		frame.refreshTreeAll();
	}
}
