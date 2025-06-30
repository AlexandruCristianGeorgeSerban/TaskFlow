package com.taskflow.action;

import com.taskflow.model.RootGroup;
import com.taskflow.service.TaskService;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

// Șterge RootGroup-ul selectat și conținutul său.
public class DeleteRootAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală
	private final TaskService svc; // Serviciul pentru operații pe task-uri

	public DeleteRootAction(MainFrame frame, TaskService svc) {
		super("Delete Root"); // Text afișat în UI pentru această acțiune
		this.frame = frame;
		this.svc = svc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Obține calea nodului selectat în arbore
		TreePath sel = frame.getCategoryTree().getSelectionPath();
		if (sel == null) {
			// Afișează mesaj dacă nu există selecție
			JOptionPane.showMessageDialog(frame, "Select a root first.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Obține obiectul userObject și verifică dacă e RootGroup
		Object obj = ((DefaultMutableTreeNode) sel.getLastPathComponent()).getUserObject();
		if (!(obj instanceof RootGroup)) {
			// Afișează mesaj dacă nu s-a selectat un root
			JOptionPane.showMessageDialog(frame, "Please select a root.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		RootGroup rg = (RootGroup) obj; // RootGroup selectat

		// Confirmare ștergere root și tot conținutul
		int ans = JOptionPane.showConfirmDialog(
			frame,
			"Delete root «" + rg.getName() + "» and all its contents?",
			"Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			svc.removeRoot(rg); // Șterge root-ul
			frame.refreshTreeAll(); // Reîmprospătează arborele
		}
	}
}
