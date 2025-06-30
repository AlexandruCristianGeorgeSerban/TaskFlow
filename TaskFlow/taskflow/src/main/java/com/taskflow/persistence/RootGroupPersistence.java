package com.taskflow.persistence;

import com.taskflow.model.RootGroup;

import java.io.File;
import java.util.List;

// Operații de persistență pentru RootGroup, inclusiv categoriile și task-urile acestora.
public interface RootGroupPersistence {
    // Încarcă lista de grupuri root din fișier
    List<RootGroup> loadRootGroups(File file) throws Exception;

    // Salvează lista de grupuri root în fișier
    void saveRootGroups(List<RootGroup> roots, File file) throws Exception;
}
