package com.taskflow.ui;

import com.taskflow.model.Task;
import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TaskTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = {
        "Title", "Description", "Due Date", "Completed"
    };

    private List<Task> tasks = new ArrayList<>();

    public TaskTableModel(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task t = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0: return t.getTitle();
            case 1: return t.getDescription();
            case 2:
                return new SimpleDateFormat("yyyy-MM-dd")
                         .format(t.getDueDate().getTime());
            case 3: return t.isCompleted();
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 2: return String.class;
            case 3: return Boolean.class;
            default: return String.class;
        }
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        fireTableDataChanged();
    }
}
