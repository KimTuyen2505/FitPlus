package com.example.firstproject.models;
import java.util.Date;

public class HealthMeasurement {
    private long id;
    private String type; // "weight", "height", "blood_pressure", "heart_rate", etc.
    private float value;
    private Date date;

    // Các loại đo lường
    public static final String TYPE_WEIGHT = "weight";
    public static final String TYPE_HEIGHT = "height";
    public static final String TYPE_BLOOD_PRESSURE = "blood_pressure";
    public static final String TYPE_HEART_RATE = "heart_rate";

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

    // Kiểm tra xem giá trị nhịp tim có bình thường không
    public boolean isHeartRateNormal() {
        if (!TYPE_HEART_RATE.equals(type)) {
            return true;
        }

        // Nhịp tim bình thường cho người trưởng thành: 60-100 nhịp/phút
        return value >= 60 && value <= 100;
    }

    // Lấy thông báo về trạng thái nhịp tim
    public String getHeartRateStatus() {
        if (!TYPE_HEART_RATE.equals(type)) {
            return "";
        }

        if (value < 60) {
            return "Nhịp tim thấp (nhịp chậm)";
        } else if (value <= 100) {
            return "Nhịp tim bình thường";
        } else {
            return "Nhịp tim cao (nhịp nhanh)";
        }
    }
}
