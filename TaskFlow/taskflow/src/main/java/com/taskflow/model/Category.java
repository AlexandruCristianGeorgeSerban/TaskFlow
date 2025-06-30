package com.taskflow.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Reprezintă o categorie de task-uri, cu identificator, nume, culoare și lista de task-uri.
public class Category {
    private UUID id; // Identificator unic al categoriei
    private String name; // Numele categoriei
    private String color; // Culoarea categoriei în format hex
    private List<Task> tasks; // Lista de task-uri din categorie

    public Category() {
        // Inițializează id și lista de task-uri goală
        this.id = UUID.randomUUID();
        this.tasks = new ArrayList<>();
    }

    public Category(String name, String color) {
        // Constructor cu nume și culoare
        this.id = UUID.randomUUID();
        this.name = name;
        this.color = color;
        this.tasks = new ArrayList<>();
    }

    // Getter pentru id
    public UUID getId() {
        return id;
    }

    // Setter pentru id
    public void setId(UUID id) {
        this.id = id;
    }

    // Getter pentru nume
    public String getName() {
        return name;
    }

    // Setter pentru nume
    public void setName(String name) {
        this.name = name;
    }

    // Getter pentru culoare
    public String getColor() {
        return color;
    }
    
    // Setter pentru culoare, validând formatul hex
    public void setColor(String color) {
        if (color == null || color.isBlank()) return;
        String hex = color.trim();
        if (!hex.startsWith("#")) {
            hex = "#" + hex;
        }
        if (hex.length() == 7) {
            this.color = hex;
        }
    }

    // Getter pentru lista de task-uri
    public List<Task> getTasks() {
        return tasks;
    }

    // Setter pentru lista de task-uri
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Adaugă un task în categorie
    public void addTask(Task task) {
        this.tasks.add(task);
    }

    // Elimină un task din categorie
    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    @Override
    public String toString() {
        // Reprezentare textuală a categoriei
        return String.format(
            "Category[id=%s, name='%s', color='%s', tasks=%d]",
            id, name, color, tasks.size()
        );
    }
}
