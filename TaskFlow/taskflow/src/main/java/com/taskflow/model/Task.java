package com.taskflow.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

// Reprezintă un task cu id, titlu, descriere, dată-limită, status și culoare.
public class Task {
    private String id; // Identificator unic al task-ului
    private String title; // Titlul task-ului
    private String description; // Descrierea task-ului
    private Calendar dueDate; // Data-limită
    private boolean completed; // Starea de finalizare
    private String colorHex; // Culoarea iconiței în format hex

    public Task() {
        this.id = UUID.randomUUID().toString(); // Generează id unic
        this.dueDate = Calendar.getInstance(); // Data implicită: ziua curentă
        this.completed = false; // Implicit task nefinalizat
        this.colorHex = "#A00000"; // Culoare implicită: roșu închis
    }

    public Task(String title, String description, Calendar dueDate, boolean completed, String colorHex) {
        this(); // Inițializează id și setările implicite
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        setColorHex(colorHex); // Folosește setter-ul pentru validare
    }

    public Task(String title, String description, Calendar dueDate, boolean completed) {
        this(title, description, dueDate, completed, "#A00000"); // Culoare implicită
    }

    // Getter pentru id
    public String getId() {
        return id;
    }

    // Setter pentru id
    public void setId(String id) {
        this.id = id;
    }

    // Getter pentru titlu
    public String getTitle() {
        return title;
    }

    // Setter pentru titlu
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter pentru descriere
    public String getDescription() {
        return description;
    }

    // Setter pentru descriere
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter pentru data-limită
    public Calendar getDueDate() {
        return dueDate;
    }

    // Setter pentru data-limită
    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    // Verifică dacă task-ul e finalizat
    public boolean isCompleted() {
        return completed;
    }

    // Setează starea de finalizare
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Getter pentru culoare hex a iconiței
    public String getColorHex() {
        return colorHex;
    }

    // Setter pentru culoare, validează formatul (#RRGGBB)
    public void setColorHex(String colorHex) {
        if (colorHex == null || colorHex.isBlank()) return; // Culoare invalidă
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
        // Reprezentare textuală cu data-limită și starea task-ului
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(dueDate.getTime());
        return title + " (due " + dateStr + ", " + (completed ? "done" : "pending") + ")";
    }
}
