package com.example.firstproject.models;

import java.util.Date;

public class MenstrualCycle {
    private long id;
    private Date startDate;
    private Date endDate;
    private String symptoms;
    private String createdAt;

    public MenstrualCycle() {
    }

    public MenstrualCycle(Date startDate, Date endDate, String symptoms) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.symptoms = symptoms;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Calculate cycle length
    public int getCycleLength() {
        if (startDate == null || endDate == null) return 0;
        long diff = endDate.getTime() - startDate.getTime();
        return (int) (diff / (24 * 60 * 60 * 1000));
    }
}

