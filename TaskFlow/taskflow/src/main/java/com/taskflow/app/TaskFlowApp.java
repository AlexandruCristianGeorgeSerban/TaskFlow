package com.taskflow.app;

import com.taskflow.model.RootGroup;
import com.taskflow.persistence.RootGroupPersistence;
import com.taskflow.persistence.XmlSaxHandler;
import com.taskflow.service.DefaultTaskService;
import com.taskflow.service.TaskService;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

// Punctul de intrare în aplicația TaskFlow.
public class TaskFlowApp {
    public static void main(String[] args) {
        // 1) Setăm Look and Feel-ul sistemului
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {}

        // 2) Creăm și inițializăm serviciul de task-uri
        TaskService taskService = new DefaultTaskService();

        // 3) Încărcăm root-urile din fișierul XML folosind SAX
        String dataFilePath = "src/test/resources/data.xml"; // Calea fișierului de date
        File dataFile = new File(dataFilePath);
        List<RootGroup> roots;
        if (dataFile.exists()) {
            try {
                RootGroupPersistence loader = new XmlSaxHandler(); // Handler SAX pentru XML
                roots = loader.loadRootGroups(dataFile);
            } catch (Exception ex) {
                // Afișăm eroarea la încărcare și inițializăm lista goală
                System.err.println("Failed to load roots: " + ex.getMessage());
                roots = Collections.emptyList();
            }
        } else {
            roots = Collections.emptyList(); // Nu există fișierul, lista rămâne goală
        }

        // 4) Adăugăm toate root-urile în serviciu
        for (RootGroup rg : roots) {
            taskService.addRoot(rg);
        }

        // 5) Inițializăm și afișăm interfața grafică în firul Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(taskService);
            
         // Dacă dataFile a existat la start, setează-l ca last directory si current file
            if (dataFile.exists()) {
            	frame.setCurrentFile(dataFile);
                frame.setLastDir(dataFile.getParentFile());
                frame.refreshTreeAll();
            }

            frame.setVisible(true);
        });
    }
}