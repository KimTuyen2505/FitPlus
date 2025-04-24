package com.example.firstproject.models;

import java.util.Date;

public class PregnancyData {
    private long id;
    private long userId;
    private Date startDate;
    private Date dueDate;
    private String babyName;
    private boolean isConfirmed;
    private Date lastUpdated;

    public PregnancyData() {
        this.lastUpdated = new Date();
    }

    public PregnancyData(Date startDate, String babyName) {
        this.startDate = startDate;
        this.babyName = babyName;
        this.isConfirmed = true;
        this.lastUpdated = new Date();

        // Calculate due date (280 days from start date)
        if (startDate != null) {
            long dueTime = startDate.getTime() + (280L * 24 * 60 * 60 * 1000);
            this.dueDate = new Date(dueTime);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;

        // Recalculate due date when start date changes
        if (startDate != null) {
            long dueTime = startDate.getTime() + (280L * 24 * 60 * 60 * 1000);
            this.dueDate = new Date(dueTime);
        }
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Calculate pregnancy duration in days
    public int getDurationDays() {
        if (startDate == null) return 0;

        long diffInMillis = System.currentTimeMillis() - startDate.getTime();
        return (int) (diffInMillis / (1000 * 60 * 60 * 24));
    }

    // Calculate pregnancy duration in weeks and days
    public int[] getDurationWeeksAndDays() {
        int totalDays = getDurationDays();
        int weeks = totalDays / 7;
        int days = totalDays % 7;

        return new int[]{weeks, days};
    }

    // Get baby size based on week
    public String getBabySize() {
        int[] duration = getDurationWeeksAndDays();
        int week = duration[0];

        if (week < 5 || week > 40) return "N/A";

        String[] sizes = {
                "0.1 cm", "0.3 cm", "1 cm", "1.6 cm", "2.3 cm",
                "3.1 cm", "4.1 cm", "5.4 cm", "7.4 cm", "8.7 cm",
                "10.1 cm", "11.6 cm", "13 cm", "14.2 cm", "15.3 cm",
                "16.4 cm", "26.7 cm", "27.8 cm", "28.9 cm", "30 cm",
                "34.6 cm", "35.6 cm", "36.6 cm", "37.6 cm", "38.6 cm",
                "39.9 cm", "41.1 cm", "42.4 cm", "43.7 cm", "45 cm",
                "46.2 cm", "47.4 cm", "48.6 cm", "49.8 cm", "50.7 cm", "51.2 cm"
        };

        return sizes[week - 5];
    }

    // Get baby weight based on week
    public String getBabyWeight() {
        int[] duration = getDurationWeeksAndDays();
        int week = duration[0];

        if (week < 5 || week > 40) return "N/A";

        String[] weights = {
                "< 1 g", "< 1 g", "1 g", "1 g", "2 g",
                "5 g", "10 g", "14 g", "23 g", "43 g",
                "70 g", "100 g", "140 g", "190 g", "240 g",
                "300 g", "360 g", "430 g", "501 g", "600 g",
                "660 g", "760 g", "875 g", "1000 g", "1150 g",
                "1300 g", "1500 g", "1700 g", "1900 g", "2150 g",
                "2400 g", "2600 g", "2900 g", "3100 g", "3300 g", "3400 g"
        };

        return weights[week - 5];
    }
}
