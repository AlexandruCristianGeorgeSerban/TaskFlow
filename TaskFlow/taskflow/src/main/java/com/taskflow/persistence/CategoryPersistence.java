package com.taskflow.persistence;

import com.taskflow.model.Category;

import java.io.File;
import java.util.List;

// Interfață pentru persistarea și reîncărcarea listelor de categorii.
public interface CategoryPersistence {
    // Încarcă lista de categorii din fișier
    List<Category> load(File file) throws Exception;

    // Salvează lista de categorii în fișier
    void save(List<Category> categories, File file) throws Exception;
}
