package com.example.firstproject.models;

import java.util.Date;

public class MedicalRecord {
    private long id;
    private String title;
    private String description;
    private Date date;
    private String doctor;
    private long userId;

    public MedicalRecord() {
        this.date = new Date();
    }

    public MedicalRecord(String title, String description, Date date, String doctor) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.doctor = doctor;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}


