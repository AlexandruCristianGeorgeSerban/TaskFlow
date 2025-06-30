package com.taskflow.service;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;

import java.util.ArrayList;
import java.util.List;

public class DefaultTaskService implements TaskService {
    private final List<RootGroup> roots;

    public DefaultTaskService() {
        this.roots = new ArrayList<>();
    }
    
    public DefaultTaskService(List<RootGroup> initialRoots) {
        this.roots = new ArrayList<>(initialRoots);
    }

    @Override
    public List<RootGroup> getAllRoots() {
    	return roots;
    }

    @Override
    public void addRoot(RootGroup root) {
        roots.add(root);
    }
    
    @Override
    public void insertRoot(int index, RootGroup root) {
        if (index < 0) index = 0;
        if (index > roots.size()) index = roots.size();
        roots.add(index, root);
    }

    @Override
    public void updateRoot(RootGroup root) {
    }

    @Override
    public void addCategory(RootGroup root, Category category) {
        root.getCategories().add(category);
    }

    @Override
    public void updateCategory(Category category) {
    }

    @Override
    public void addTask(Category category, Task task) {
        category.getTasks().add(task);
    }

    @Override
    public void updateTask(Task task) {
    }
    
    @Override
    public void removeRoot(RootGroup root) {
        roots.remove(root);
    }

    @Override
    public void removeCategory(RootGroup parent, Category cat) {
        parent.getCategories().remove(cat);
    }

    @Override
    public void removeTask(Category parent, Task t) {
        parent.getTasks().remove(t);
    }
}
