package com.example.firstproject.models;

public class HealthMeasurement {
    private long id;
    private String measurementType;
    private float measurementValue;
    private String measurementDate;
    private String createdAt;

    public HealthMeasurement() {
    }

    public HealthMeasurement(String measurementType, float measurementValue, String measurementDate) {
        this.measurementType = measurementType;
        this.measurementValue = measurementValue;
        this.measurementDate = measurementDate;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public float getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(float measurementValue) {
        this.measurementValue = measurementValue;
    }

    public String getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(String measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

