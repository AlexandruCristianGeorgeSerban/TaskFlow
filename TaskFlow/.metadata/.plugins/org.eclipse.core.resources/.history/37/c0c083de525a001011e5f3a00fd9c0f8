package com.taskflow.ui;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

// Model pentru JTree care afișează RootGroup, Category și Task.
public class CategoryTreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;

	public CategoryTreeModel(List<RootGroup> roots) {
		super(new DefaultMutableTreeNode("")); // Root invizibil
		updateRoots(roots);
	}


	// Actualizează nodurile de nivel rădăcină pe baza listei de RootGroup.
	public void updateRoots(List<RootGroup> roots) {
		DefaultMutableTreeNode invRoot = (DefaultMutableTreeNode) getRoot();
		invRoot.removeAllChildren(); // Elimină nodurile existente

		for (RootGroup rg : roots) {
			DefaultMutableTreeNode rgNode = new DefaultMutableTreeNode(rg); // Nod pentru fiecare root
			for (Category c : rg.getCategories()) {
				DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(c); // Nod categorie
				for (Task t : c.getTasks()) {
					cNode.add(new DefaultMutableTreeNode(t)); // Nod task
				}
				rgNode.add(cNode);
			}
			invRoot.add(rgNode);
		}

		nodeStructureChanged(invRoot); // Notifică schimbările modelului
	}
}
