package com.taskflow.model;

import java.util.ArrayList;
import java.util.List;

// Reprezintă un grup root de categorii, cu nume și culoare în format hex.
public class RootGroup {
    private String name; // Numele grupului root
    private String colorHex; // Culoarea grupului în format hex
    private final List<Category> categories = new ArrayList<>(); // Lista de categorii din root

    public RootGroup(String name, String colorHex) {
        this.name = name;
        setColorHex(colorHex); // Folosește setter-ul pentru validare
    }

    // Getter pentru nume
    public String getName() {
        return name;
    }

    // Setter pentru nume
    public void setName(String name) {
        this.name = name;
    }

    // Getter pentru culoare hex
    public String getColorHex() {
        return colorHex;
    }

    // Setter pentru culoare hex, validează formatul (#xxxxxx)
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

    // Getter pentru lista de categorii
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public String toString() {
        return name; // Afișează doar numele în reprezentarea textuală
    }
}
