package com.taskflow.action;

import com.taskflow.model.Category;
import com.taskflow.model.RootGroup;
import com.taskflow.service.TaskService;
import com.taskflow.ui.MainFrame;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

// Șterge categoria selectată și toate task-urile asociate.
public class DeleteCategoryAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final MainFrame frame; // Fereastra principală a aplicației
	private final TaskService svc; // Serviciul pentru operații pe task-uri

	public DeleteCategoryAction(MainFrame frame, TaskService svc) {
		super("Delete Category"); // Text afișat în UI pentru această acțiune
		this.frame = frame;
		this.svc = svc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Obține calea nodului selectat în arbore
		TreePath sel = frame.getCategoryTree().getSelectionPath();
		if (sel == null) {
			// Afișează mesaj dacă nu e selectată nicio categorie
			JOptionPane.showMessageDialog(frame, "Select a category first.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// Obține obiectul din nod și verifică tipul
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) sel.getLastPathComponent();
		Object obj = node.getUserObject();
		if (!(obj instanceof Category)) {
			// Afișează mesaj dacă nu s-a selectat o categorie
			JOptionPane.showMessageDialog(frame, "Please select a category.", "Info", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		Category cat = (Category) obj; // Categoria selectată

		// Nodul părinte trebuie să fie un RootGroup
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
		RootGroup rg = (RootGroup) parent.getUserObject();

		// Confirmare ștergere categorie și task-uri
		int ans = JOptionPane.showConfirmDialog(
			frame,
			"Delete category «" + cat.getName() + "» and all its tasks?",
			"Confirm Delete", JOptionPane.YES_NO_OPTION);
		if (ans == JOptionPane.YES_OPTION) {
			svc.removeCategory(rg, cat); // Șterge categoria și task-urile
			frame.refreshAfterChange(null); // Reîmprospătează UI-ul
		}
	}
}
