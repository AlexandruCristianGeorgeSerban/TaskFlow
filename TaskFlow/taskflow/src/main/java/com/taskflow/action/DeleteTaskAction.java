package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

// Șterge task-ul selectat.
public class DeleteTaskAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală
	private final TaskService svc; // Serviciul pentru operații pe task-uri

	public DeleteTaskAction(MainFrame frame, TaskService svc) {
		super("Delete Task"); // Text afișat în UI
		this.frame = frame;
		this.svc = svc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Preluăm calea nodului selectat în arbore
		TreePath sel = frame.getCategoryTree().getSelectionPath();
		if (sel == null) {
			// Dacă nu e selectat niciun task, afișăm mesaj
			JOptionPane.showMessageDialog(frame, "Select a task first.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) sel.getLastPathComponent();
		Object obj = node.getUserObject();
		if (!(obj instanceof Task)) {
			// Dacă nu e selectat un task, afișăm mesaj
			JOptionPane.showMessageDialog(frame, "Please select a task.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		Task t = (Task) obj; // Task selectat

		// Nodul părinte reprezintă categoria
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
		Category cat = (Category) parent.getUserObject(); // Categoria task-ului

		// Confirmare ștergere task
		int ans = JOptionPane.showConfirmDialog(
			frame,
			"Delete task «" + t.getTitle() + "»?",
			"Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			svc.removeTask(cat, t); // Ștergere prin serviciu
			frame.refreshAfterChange(cat); // Reîmprospătăm UI-ul
		}
	}
}
