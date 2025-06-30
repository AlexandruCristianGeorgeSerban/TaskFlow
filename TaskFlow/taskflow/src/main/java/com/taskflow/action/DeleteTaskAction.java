package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DeleteTaskAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
    private final MainFrame frame;
    private final TaskService svc;

    public DeleteTaskAction(MainFrame frame, TaskService svc) {
        super("Delete Task");
        this.frame = frame;
        this.svc   = svc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath sel = frame.getCategoryTree().getSelectionPath();
        if (sel == null) {
            JOptionPane.showMessageDialog(frame, "Select a task first.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)sel.getLastPathComponent();
        Object o = node.getUserObject();
        if (!(o instanceof Task)) {
            JOptionPane.showMessageDialog(frame, "Please select a task.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Task t = (Task)o;
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
        Category cat = (Category)parent.getUserObject();

        int ans = JOptionPane.showConfirmDialog(
            frame,
            "Delete task «" + t.getTitle() + "»?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) {
            svc.removeTask(cat, t);
            frame.refreshAfterChange(cat);
        }
    }
}
