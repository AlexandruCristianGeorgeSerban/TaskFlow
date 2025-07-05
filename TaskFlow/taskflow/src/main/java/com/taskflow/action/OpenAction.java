package com.taskflow.action;

import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Acțiune pentru deschiderea unui fișier XML existent.
 * Deschide un JFileChooser, setează frame.currentFile și încarcă datele.
 */
public class OpenAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private final MainFrame frame; // Fereastra principală a aplicației

    public OpenAction(MainFrame frame) {
        super("Open"); // Text afișat în UI pentru această acțiune
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        File ld = frame.getLastDir();                // folosim getter-ul pentru directorul precedent
        if (ld != null) chooser.setCurrentDirectory(ld);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            // Salvează calea fișierului curent în frame pentru operațiuni ulterioare de Save
            frame.setCurrentFile(file);
            // Actualizează directorul curent în frame pentru a deschide în aceeași locație data viitoare
            frame.setLastDir(file.getParentFile());     // folosim setter-ul
            // Încarcă datele din fișier în aplicație
            frame.loadDataFromFile(file);
        }
    }
}
