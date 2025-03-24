package com.example.firstproject.models;

public class User {
    private long id;
    private String name;
    private int age;
    private String gender;
    private float height;
    private float weight;
    private String bloodType;

    public User() {
    }

    public User(String name, int age, String gender, float height, float weight) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
    }

    public User(String name, int age, String gender, float height, float weight, String bloodType) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    // Tính BMI
    public float calculateBMI() {
        if (height <= 0) return 0;
        return weight / ((height / 100) * (height / 100));
    }

    // Phân loại BMI
    public String getBMICategory() {
        float bmi = calculateBMI();
        if (bmi < 18.5) {
            return "Thiếu cân";
        } else if (bmi < 25) {
            return "Bình thường";
        } else if (bmi < 30) {
            return "Thừa cân";
        } else {
            return "Béo phì";
        }
    }
}