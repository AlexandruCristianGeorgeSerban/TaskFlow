package com.taskflow.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Category {
    private UUID id;
    private String name;
    private String color;
    private List<Task> tasks;

    public Category() {
        this.id = UUID.randomUUID();
        this.tasks = new ArrayList<>();
    }

    public Category(String name, String color) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.color = color;
        this.tasks = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    @Override
    public String toString() {
        return String.format(
            "Category[id=%s, name='%s', color='%s', tasks=%d]",
            id, name, color, tasks.size()
        );
    }
}
