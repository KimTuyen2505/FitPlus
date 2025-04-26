package com.example.firstproject.doctor.models;

public class Doctor {
    private long id;
    private String name;
    private String specialty;
    private String hospital;
    private float rating;
    private int consultations;
    private boolean available;

    public Doctor(long id, String name, String specialty, String hospital, float rating, int consultations, boolean available) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.hospital = hospital;
        this.rating = rating;
        this.consultations = consultations;
        this.available = available;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getConsultations() {
        return consultations;
    }

    public void setConsultations(int consultations) {
        this.consultations = consultations;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
