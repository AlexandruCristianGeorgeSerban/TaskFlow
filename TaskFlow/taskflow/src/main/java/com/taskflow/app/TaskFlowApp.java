package com.taskflow.app;

import com.taskflow.model.Category;
import com.taskflow.persistence.XmlSaxHandler;
import com.taskflow.ui.MainFrame;
import com.taskflow.util.PropertiesManager;

import javax.swing.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class TaskFlowApp {
    public static void main(String[] args) {
        try {
            String lafClass = PropertiesManager.get("ui.lookAndFeel", UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(lafClass);
        } catch (Exception e) {
            System.err.println("Failed to set Look & Feel: " + e.getMessage());
        }

        List<Category> categories;
        String dataFilePath = PropertiesManager.get("data.file", "data.xml");
        File dataFile = new File(dataFilePath);
        if (dataFile.exists()) {
            try {
                categories = new XmlSaxHandler().loadCategories(dataFile);
            } catch (Exception e) {
                System.err.println("Failed to load data: " + e.getMessage());
                categories = Collections.emptyList();
            }
        } else {
            categories = Collections.emptyList();
        }

        final List<Category> initCats = categories;
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(initCats);
            frame.setVisible(true);
        });
    }
}
