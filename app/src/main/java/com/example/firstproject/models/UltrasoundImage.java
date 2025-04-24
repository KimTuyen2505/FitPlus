package com.example.firstproject.models;

import java.util.Date;

public class UltrasoundImage {
    private long id;
    private long pregnancyId;
    private String imageUri;
    private Date imageDate;
    private int pregnancyWeek;
    private String notes;

    public UltrasoundImage() {
        this.imageDate = new Date();
    }

    public UltrasoundImage(long pregnancyId, String imageUri, int pregnancyWeek, String notes) {
        this.pregnancyId = pregnancyId;
        this.imageUri = imageUri;
        this.pregnancyWeek = pregnancyWeek;
        this.notes = notes;
        this.imageDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPregnancyId() {
        return pregnancyId;
    }

    public void setPregnancyId(long pregnancyId) {
        this.pregnancyId = pregnancyId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Date getImageDate() {
        return imageDate;
    }

    public void setImageDate(Date imageDate) {
        this.imageDate = imageDate;
    }

    public int getPregnancyWeek() {
        return pregnancyWeek;
    }

    public void setPregnancyWeek(int pregnancyWeek) {
        this.pregnancyWeek = pregnancyWeek;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
