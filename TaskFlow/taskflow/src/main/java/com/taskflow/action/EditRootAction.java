package com.taskflow.action;

import com.taskflow.model.RootGroup;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// Permite editarea numelui și culorii unui RootGroup selectat.
public class EditRootAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală a aplicației

	public EditRootAction(MainFrame frame) {
		super("Edit Root"); // Text afișat în UI pentru această acțiune
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		RootGroup rg = frame.getSelectedRoot(); // RootGroup selectat
		if (rg == null)
			return; // Oprește dacă nu e selectat niciun root

		// Solicită noul nume, preumplând cu numele curent
		String name = JOptionPane.showInputDialog(frame, "Root name:", rg.getName());
		if (name == null || name.isBlank())
			return; // Nume invalid sau anulare

		// Afișează selectorul de culoare, cu culoarea curentă implicită
		Color chosen = JColorChooser.showDialog(frame, "Choose root color", Color.decode(rg.getColorHex()));
		// Păstrează culoarea veche dacă utilizatorul anulează
		String hex = (chosen == null)
			? rg.getColorHex()
			: String.format("#%02x%02x%02x", chosen.getRed(), chosen.getGreen(), chosen.getBlue());

		rg.setName(name.trim()); // Actualizează numele
		rg.setColorHex(hex); // Actualizează culoarea
		frame.refreshTreeAll(); // Reîmprospătează arborele din UI
	}
}
