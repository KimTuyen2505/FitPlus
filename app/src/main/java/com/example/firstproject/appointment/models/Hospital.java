package com.example.firstproject.appointment.models;

public class Hospital {
    private long id;
    private String name;
    private String address;
    private float rating;
    private String[] specialties;

    public Hospital(long id, String name, String address, float rating, String[] specialties) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.specialties = specialties;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String[] getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String[] specialties) {
        this.specialties = specialties;
    }
}
