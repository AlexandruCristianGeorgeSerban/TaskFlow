package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.model.RootGroup;
import com.taskflow.service.TaskService;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

public class DeleteCategoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;
    private final TaskService svc;

    public DeleteCategoryAction(MainFrame frame, TaskService svc) {
        super("Delete Category");
        this.frame = frame;
        this.svc   = svc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath sel = frame.getCategoryTree().getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(frame, "Select a category first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)sel.getLastPathComponent();
        Object o = node.getUserObject();
        if (!(o instanceof Category)) {
            JOptionPane.showMessageDialog(frame, "Please select a category.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Category cat = (Category)o;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
        RootGroup rg = (RootGroup)parent.getUserObject();

        int ans = JOptionPane.showConfirmDialog(
            frame,
            "Delete category «" + cat.getName() + "» and all its tasks?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            svc.removeCategory(rg, cat);
            frame.refreshAfterChange(null);
        }
    }
}
