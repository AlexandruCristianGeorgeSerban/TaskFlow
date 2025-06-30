package com.taskflow.action;

import com.taskflow.model.RootGroup;
import com.taskflow.persistence.RootGroupPersistence;
import com.taskflow.persistence.XmlSaxHandler;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class ImportAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;

    public ImportAction(MainFrame frame) {
        super("Import XML");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();

        try {
        	RootGroup imported = new RootGroup("Imported", "#0077CC");
        	frame.getTaskService().addRoot(imported);
        	
            RootGroupPersistence loader = new XmlSaxHandler();
            List<RootGroup> importedRoots = loader.loadRootGroups(file);
            importedRoots.forEach(frame.getTaskService()::addRoot);
            frame.refreshTreeAll();
        } catch (Exception ex) {
            frame.showError(ex);
            ex.printStackTrace();
        }
    }
}
