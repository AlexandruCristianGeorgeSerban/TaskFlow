package com.taskflow.ui;

import com.taskflow.model.Task;
import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

// Model pentru JTable care afișează lista de Task și suportă filtrare.
public class TaskTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    // Denumirile coloanelor: Titlu, Descriere, Dată-limită, Finalizat
    private static final String[] COLUMN_NAMES = {
        "Title", "Description", "Due Date", "Completed"
    };

    private List<Task> tasks = new ArrayList<>(); // Lista de task-uri afișate

    public TaskTableModel(List<Task> tasks) {
        this.tasks = tasks; // Inițializează cu lista dată
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length; // Numărul de coloane
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column]; // Numele coloanei
    }

    @Override
    public int getRowCount() {
        return tasks.size(); // Numărul de task-uri
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task t = tasks.get(rowIndex); // Task-ul pentru rândul dat
        switch (columnIndex) {
            case 0: return t.getTitle();
            case 1: return t.getDescription(); // Descrierea task-ului
            case 2:
                // Formatează data-limită ca text
                return new SimpleDateFormat("yyyy-MM-dd")
                         .format(t.getDueDate().getTime());
            case 3: return t.isCompleted(); // Checkbox pentru finalizare
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 2: return String.class;   // Dată ca String
            case 3: return Boolean.class;  // Checkbox
            default: return String.class;  // Text
        }
    }

    // Actualizează lista de task-uri și notifică tabelul
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        fireTableDataChanged();
    }
}
