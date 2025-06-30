package com.taskflow.ui;

import com.taskflow.model.Category;
import com.taskflow.model.Task;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CategoryTreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;

    public CategoryTreeModel(List<Category> categories) {
        super(new DefaultMutableTreeNode("Categories"));
        updateCategories(categories);
    }

    public void updateCategories(List<Category> categories) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
        root.removeAllChildren();
        for (Category c : categories) {
            DefaultMutableTreeNode catNode = new DefaultMutableTreeNode(c);
            for (Task t : c.getTasks()) {
                DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(t);
                catNode.add(taskNode);
            }
            root.add(catNode);
        }
        nodeStructureChanged(root);
    }

    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
        Enumeration<?> cats = root.children();
        while (cats.hasMoreElements()) {
            DefaultMutableTreeNode catNode = (DefaultMutableTreeNode) cats.nextElement();
            Object userObj = catNode.getUserObject();
            if (userObj instanceof Category) {
                Category c = (Category) userObj;
                // update tasks from child nodes in case of dynamic changes
                c.getTasks().clear();
                Enumeration<?> tasks = catNode.children();
                while (tasks.hasMoreElements()) {
                    DefaultMutableTreeNode taskNode = (DefaultMutableTreeNode) tasks.nextElement();
                    Object taskObj = taskNode.getUserObject();
                    if (taskObj instanceof Task) {
                        c.addTask((Task) taskObj);
                    }
                }
                categories.add(c);
            }
        }
        return categories;
    }
}
