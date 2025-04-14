package com.example.firstproject.models;

public class HealthSuggestion {
    private int id;
    private String title;
    private String content;
    private String source;
    private int categoryId;

    public HealthSuggestion() {
    }

    public HealthSuggestion(int id, String title, String content, String source, int categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.source = source;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
