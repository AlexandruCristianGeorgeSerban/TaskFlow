package com.taskflow.action;

import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class DeleteFileAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;

    public DeleteFileAction(MainFrame frame) {
        super("Delete File/Folder…");
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser ch = new JFileChooser();
        ch.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (ch.showDialog(frame,"Delete")!=JFileChooser.APPROVE_OPTION) return;
        File f = ch.getSelectedFile();
        if (!f.exists()) {
            JOptionPane.showMessageDialog(frame,"Not found: "+f,"Info",JOptionPane.INFORMATION_MESSAGE);
        } else if (JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog(
            frame,"Delete?\n"+f,"Confirm",JOptionPane.YES_NO_OPTION)) {
            frame.deleteRecursively(f);
            frame.refreshTreeAll();
        }
    }
}
