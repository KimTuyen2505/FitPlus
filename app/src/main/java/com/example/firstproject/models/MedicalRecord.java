package com.example.firstproject.models;

public class MedicalRecord {
    private long id;
    private String title;
    private String date;
    private String doctor;
    private String notes;

    public MedicalRecord() {
    }

    public MedicalRecord(String title, String date, String doctor, String notes) {
        this.title = title;
        this.date = date;
        this.doctor = doctor;
        this.notes = notes;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
