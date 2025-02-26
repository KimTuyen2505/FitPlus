package com.example.firstproject.models;

import java.util.Date;

public class HealthMeasurement {
    private long id;
    private String type; // "weight", "height", "blood_pressure", etc.
    private float value;
    private Date date;

    public HealthMeasurement() {
        this.date = new Date();
    }

    public HealthMeasurement(String type, float value) {
        this.type = type;
        this.value = value;
        this.date = new Date();
    }

    public HealthMeasurement(String type, float value, Date date) {
        this.type = type;
        this.value = value;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

