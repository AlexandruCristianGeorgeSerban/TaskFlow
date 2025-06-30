package com.taskflow.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    private Calendar dueDate;
    private boolean completed;
    private String colorHex;

    public Task() {
        this.id = UUID.randomUUID().toString();
        this.dueDate = Calendar.getInstance();
        this.completed = false;
        this.colorHex = "#A00000";
    }

    public Task(String title, String description, Calendar dueDate, boolean completed, String colorHex) {
        this();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        setColorHex(colorHex);
    }

    public Task(String title, String description, Calendar dueDate, boolean completed) {
        this(title, description, dueDate, completed, "#A00000");
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getDueDate() {
        return dueDate;
    }
    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getColorHex() {
        return colorHex;
    }
    public void setColorHex(String colorHex) {
        if (colorHex == null || colorHex.isBlank()) return;
        String hex = colorHex.trim();
        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }
        if (hex.length() == 7) {
            this.colorHex = hex;
        }
    }

    @Override
    public String toString() {
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(dueDate.getTime());
        return title + " (due " + dateStr + ", " + (completed ? "done" : "pending") + ")";
    }
}
