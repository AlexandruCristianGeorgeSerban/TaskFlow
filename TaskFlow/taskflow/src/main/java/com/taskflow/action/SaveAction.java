package com.taskflow.action;

import com.taskflow.ui.MainFrame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

// Acțiune pentru salvarea fișierului curent sau salvare ca XML dacă nu există deja unul.
public class SaveAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    private final MainFrame frame; // Fereastra principală a aplicației

    public SaveAction(MainFrame frame) {
        super("Save"); // Text afișat în UI pentru această acțiune
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File current = frame.getCurrentFile(); // Fișierul curent salvat anterior sau null
        if (current != null) {
            // Suprascrie fișierul deja deschis
            frame.saveDataToFile(current);
        } else {
            // Dacă nu avem un fișier curent, deschide dialogul (Save As)
            JFileChooser chooser = new JFileChooser();
            File ld = frame.getLastDir();
            if (ld != null) chooser.setCurrentDirectory(ld);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int ans = chooser.showSaveDialog(frame);
            if (ans != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xml")) {
                file = new File(file.getParentFile(), file.getName() + ".xml");
            }
            // Salvează calea fișierului curent în frame pentru apeluri ulterioare de Save
            frame.setCurrentFile(file);
            frame.setLastDir(file.getParentFile());
            frame.saveDataToFile(file);
        }
    }
}
