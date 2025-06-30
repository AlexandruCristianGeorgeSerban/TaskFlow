package com.taskflow.service;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;

import java.util.List;

public interface TaskService {
    List<RootGroup> getAllRoots();

    void addRoot(RootGroup root);
    
    void insertRoot(int index, RootGroup root);

    void updateRoot(RootGroup root);

    void addCategory(RootGroup root, Category category);

    void updateCategory(Category category);

    void addTask(Category category, Task task);

    void updateTask(Task task);
    
    void removeRoot(RootGroup root);
    
    void removeCategory(RootGroup parent, Category cat);
    
    void removeTask(Category parent, Task t);
    
    void moveRoot(RootGroup root, int toIndex);
}
