package com.taskflow.service;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;

import java.util.ArrayList;
import java.util.List;

// Implementare simplă a serviciului de task-uri folosind liste în memorie.
public class DefaultTaskService implements TaskService {
    private final List<RootGroup> roots; // Lista de grupuri root gestionate

    public DefaultTaskService() {
        this.roots = new ArrayList<>(); // Inițializează lista goală
    }
    
    public DefaultTaskService(List<RootGroup> initialRoots) {
        this.roots = new ArrayList<>(initialRoots); // Copiază lista inițială
    }

    @Override
    public List<RootGroup> getAllRoots() {
        return roots; // Returnează toate grupurile root
    }

    @Override
    public void addRoot(RootGroup root) {
        roots.add(root); // Adaugă un grup root
    }
    
    @Override
    public void insertRoot(int index, RootGroup root) {
        // Normalizează index-ul între limitele valide
        if (index < 0) index = 0;
        if (index > roots.size()) index = roots.size();
        roots.add(index, root); // Inserează la poziția dorită
    }

    @Override
    public void updateRoot(RootGroup root) {
        // Modificările sunt deja reflectate prin referință în listă
    }

    @Override
    public void addCategory(RootGroup root, Category category) {
        root.getCategories().add(category); // Adaugă o categorie la root
    }

    @Override
    public void updateCategory(Category category) {
        // Modificările categoriei sunt reflectate prin referință
    }

    @Override
    public void addTask(Category category, Task task) {
        category.getTasks().add(task); // Adaugă un task la categorie
    }

    @Override
    public void updateTask(Task task) {
        // Modificările task-ului sunt reflectate prin referință
    }
    
    @Override
    public void removeRoot(RootGroup root) {
        roots.remove(root); // Șterge un grup root
    }

    @Override
    public void removeCategory(RootGroup parent, Category cat) {
        parent.getCategories().remove(cat); // Șterge o categorie
    }

    @Override
    public void removeTask(Category parent, Task t) {
        parent.getTasks().remove(t); // Șterge un task
    }
    
    @Override
    public void moveRoot(RootGroup root, int toIndex) {
        int from = roots.indexOf(root);
        if (from == -1) return; // Dacă nu există, nu facem nimic
        // Normalizează toIndex în limitele listei
        if (toIndex < 0) toIndex = 0;
        if (toIndex > roots.size() - 1) toIndex = roots.size() - 1;
        // Mută root-ul la noua poziție
        roots.remove(from);
        roots.add(toIndex, root);
    }
}
