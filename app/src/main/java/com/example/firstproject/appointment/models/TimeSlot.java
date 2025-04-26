package com.example.firstproject.appointment.models;

public class TimeSlot {
    private long id;
    private String time;
    private boolean available;

    public TimeSlot(long id, String time, boolean available) {
        this.id = id;
        this.time = time;
        this.available = available;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
