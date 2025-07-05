package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.model.RootGroup;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


// Acțiune pentru adăugarea unei categorii într-un RootGroup selectat.
public class AddCategoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
    private final MainFrame frame; // Fereastra principală a aplicației

    public AddCategoryAction(MainFrame frame) {
        super("Add Category"); // Textul ce apare în UI pentru această acțiune
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Preluăm grupul rădăcină selectat în UI
        RootGroup rg = frame.getSelectedRoot();
        if (rg == null) {
            // Dacă nu există grup selectat, afișăm un mesaj de informare
            JOptionPane.showMessageDialog(frame, "Select a root first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Solicităm utilizatorului denumirea noii categorii
        String name = JOptionPane.showInputDialog(frame, "Category name:");
        if (name == null || name.isBlank())
            return; // Oprim execuția dacă inputul e invalid

        // Afișăm selectorul de culoare (implicit albastru)
        Color chosen = JColorChooser.showDialog(frame, "Choose category color", Color.BLUE);
        // Convertim culoarea aleasă în format hex; dacă e null, folosim albastru
        String hex = (chosen == null)
            ? "#0000FF"
            : String.format("#%02x%02x%02x", chosen.getRed(), chosen.getGreen(), chosen.getBlue());

        // Adăugăm categoria în lista de categorii a grupului
        rg.getCategories().add(new Category(name.trim(), hex));
        // Reîmprospătăm arborele din UI pentru a afișa noua categorie
        frame.refreshTreeAll();
    }
}
