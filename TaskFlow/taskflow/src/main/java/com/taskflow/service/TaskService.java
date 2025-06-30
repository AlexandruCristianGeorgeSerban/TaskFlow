package com.taskflow.service;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;

import java.util.List;

// Serviciu principal pentru operații pe RootGroup, Category și Task.
public interface TaskService {
    // Returnează toate root-urile
    List<RootGroup> getAllRoots();

    // Adaugă un nou root
    void addRoot(RootGroup root);
    
    // Inserează un root la poziția specificată (0 = începutul listei)
    void insertRoot(int index, RootGroup root);

    // Actualizează un root existent
    void updateRoot(RootGroup root);

    // Adaugă o categorie într-un RootGroup
    void addCategory(RootGroup root, Category category);

    // Actualizează o categorie existentă
    void updateCategory(Category category);

    // Adaugă un task într-o categorie
    void addTask(Category category, Task task);

    // Actualizează un task existent
    void updateTask(Task task);
    
    // Șterge un root
    void removeRoot(RootGroup root);
    
    // Șterge o categorie dintr-un root
    void removeCategory(RootGroup parent, Category cat);
    
    // Șterge un task dintr-o categorie
    void removeTask(Category parent, Task t);
    
    // Mută un root la poziția specificată
    void moveRoot(RootGroup root, int toIndex);
}
