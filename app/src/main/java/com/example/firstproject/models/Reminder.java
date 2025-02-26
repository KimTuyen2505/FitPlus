package com.example.firstproject.models;

public class Reminder {
    private long id;
    private String title;
    private String time;
    private String frequency;
    private boolean isActive;

    public Reminder() {
        this.isActive = true;
    }

    public Reminder(String title, String time, String frequency) {
        this.title = title;
        this.time = time;
        this.frequency = frequency;
        this.isActive = true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

