package com.example.firstproject.models;

import java.util.Date;

public class MenstrualCycle {
    private long id;
    private Date startDate;
    private Date endDate;
    private String symptoms;

    public MenstrualCycle() {
    }

    public MenstrualCycle(Date startDate) {
        this.startDate = startDate;
    }

    public MenstrualCycle(Date startDate, Date endDate, String symptoms) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.symptoms = symptoms;
    }

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

    // Tính độ dài chu kỳ (tính từ ngày bắt đầu đến ngày bắt đầu chu kỳ tiếp theo)
    public int calculateCycleLength(Date nextCycleStartDate) {
        if (startDate == null || nextCycleStartDate == null) {
            return 0;
        }

        long diffInMillis = nextCycleStartDate.getTime() - startDate.getTime();
        return (int) (diffInMillis / (1000 * 60 * 60 * 24));
    }

    // Tính độ dài chu kỳ kinh nguyệt (từ ngày bắt đầu đến ngày kết thúc)
    public int calculatePeriodLength() {
        if (startDate == null || endDate == null) {
            return 0;
        }

        long diffInMillis = endDate.getTime() - startDate.getTime();
        return (int) (diffInMillis / (1000 * 60 * 60 * 24)) + 1; // +1 vì tính cả ngày bắt đầu
    }
}

