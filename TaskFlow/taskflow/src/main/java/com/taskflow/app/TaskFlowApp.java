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

public class TaskFlowApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {}

        TaskService taskService = new DefaultTaskService();

        String dataFilePath = "data.xml";
        File dataFile = new File(dataFilePath);
        List<RootGroup> roots;
        if (dataFile.exists()) {
            try {
                RootGroupPersistence loader = new XmlSaxHandler();
                roots = loader.loadRootGroups(dataFile);
            } catch (Exception ex) {
                System.err.println("Failed to load roots: " + ex.getMessage());
                roots = Collections.emptyList();
            }
        } else {
            roots = Collections.emptyList();
        }

        for (RootGroup rg : roots) {
            taskService.addRoot(rg);
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(taskService);
            frame.setVisible(true);
        });
    }
}
