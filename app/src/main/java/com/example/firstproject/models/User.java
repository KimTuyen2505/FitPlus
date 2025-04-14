package com.example.firstproject.models;

import java.util.Date;

public class User {
    private long id;
    private String name;
    private int age;
    private String gender;
    private float height;
    private float weight;
    private String bloodType;
    private Date birthdate;
    private int heartRate;
    private String doctorName;
    private String doctorPhone;
    private String medications;
    private String profileImageUri;

    // Thêm các trường mới
    private String address;
    private String emergencyContact;
    private String medicalHistory;
    private String allergies;
    private String insuranceNumber;

    public User() {
    }

    public User(String name, int age, String gender, float height, float weight) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPhone() {
        return doctorPhone;
    }

    public void setDoctorPhone(String doctorPhone) {
        this.doctorPhone = doctorPhone;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    // Getter và setter cho các trường mới
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
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

    // Tính tuổi từ ngày sinh
    public int calculateAge() {
        if (birthdate == null) {
            return age;
        }

        Date now = new Date();
        long timeDiff = now.getTime() - birthdate.getTime();
        double yearsDiff = timeDiff / (1000.0 * 60 * 60 * 24 * 365.25);
        return (int) yearsDiff;
    }
}
