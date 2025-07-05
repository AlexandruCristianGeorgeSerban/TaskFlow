package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

// Permite editarea numelui și culorii unei categorii selectate.
public class EditCategoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală a aplicației

	public EditCategoryAction(MainFrame frame) {
		super("Edit Category"); // Text afișat în UI pentru această acțiune
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Category cat = frame.getSelectedCategory(); // Categoria selectată
		if (cat == null)
			return; // Oprește dacă nu e selectată nicio categorie

		// Solicită noul nume, preumple cu numele curent
		String name = JOptionPane.showInputDialog(frame, "Category name:", cat.getName());
		if (name == null || name.isBlank())
			return; // Titlu invalid sau anulare

		// Afișează selectorul de culoare, cu culoarea curentă implicită
		Color chosen = JColorChooser.showDialog(frame, "Choose category color", Color.decode(cat.getColor()));
		// Păstrează culoarea veche dacă utilizatorul anulează
		String hex = (chosen == null)
			? cat.getColor()
			: String.format("#%02x%02x%02x", chosen.getRed(), chosen.getGreen(), chosen.getBlue());

		cat.setName(name.trim()); // Actualizează numele
		cat.setColor(hex); // Actualizează culoarea
		frame.refreshTreeAll(); // Reîmprospătează arborele din UI
	}
}
