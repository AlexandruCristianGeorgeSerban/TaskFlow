package com.taskflow.action;

import com.taskflow.model.RootGroup;
import com.taskflow.persistence.RootGroupPersistence;
import com.taskflow.persistence.XmlDomHandler;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class ExportAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

    private final MainFrame frame;

    public ExportAction(MainFrame frame) {
        super("Export XML");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();

        try {
            List<RootGroup> roots = frame.getTaskService().getAllRoots();
            RootGroupPersistence saver = new XmlDomHandler();
            saver.saveRootGroups(roots, file);
        } catch (Exception ex) {
            frame.showError(ex);
        }
    }
}
