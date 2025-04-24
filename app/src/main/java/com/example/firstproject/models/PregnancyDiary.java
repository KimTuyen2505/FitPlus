package com.example.firstproject.models;

import java.util.Date;

public class PregnancyDiary {
    private long id;
    private long pregnancyId;
    private String title;
    private String content;
    private Date entryDate;
    private int pregnancyWeek;

    public PregnancyDiary() {
        this.entryDate = new Date();
    }

    public PregnancyDiary(long pregnancyId, String title, String content, int pregnancyWeek) {
        this.pregnancyId = pregnancyId;
        this.title = title;
        this.content = content;
        this.pregnancyWeek = pregnancyWeek;
        this.entryDate = new Date();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public int getPregnancyWeek() {
        return pregnancyWeek;
    }

    public void setPregnancyWeek(int pregnancyWeek) {
        this.pregnancyWeek = pregnancyWeek;
    }
}
