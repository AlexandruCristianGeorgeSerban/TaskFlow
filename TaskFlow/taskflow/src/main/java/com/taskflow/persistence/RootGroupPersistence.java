package com.taskflow.persistence;

import com.taskflow.model.RootGroup;

import java.io.File;
import java.util.List;

public interface RootGroupPersistence {
    List<RootGroup> loadRootGroups(File file) throws Exception;
    void saveRootGroups(List<RootGroup> roots, File file) throws Exception;
}
