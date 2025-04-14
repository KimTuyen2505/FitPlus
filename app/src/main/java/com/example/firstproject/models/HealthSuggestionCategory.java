package com.example.firstproject.models;

public class HealthSuggestionCategory {
    private int id;
    private String name;
    private String iconName;
    private String description;
    private int color;
    private boolean isSelected;

    public HealthSuggestionCategory() {
    }

    public HealthSuggestionCategory(int id, String name, String iconName, String description, int color) {
        this.id = id;
        this.name = name;
        this.iconName = iconName;
        this.description = description;
        this.color = color;
        this.isSelected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
