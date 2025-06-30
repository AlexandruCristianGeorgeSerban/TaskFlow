package com.taskflow.persistence;

import com.taskflow.model.Category;

import java.io.File;
import java.util.List;

public interface CategoryPersistence {
    List<Category> load(File file) throws Exception;
    void save(List<Category> categories, File file) throws Exception;
}
