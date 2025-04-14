package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.User;

import java.util.Date;

public class UserDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Sửa lại phương thức open() để đảm bảo database không null
    public void open() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    // Sửa lại phương thức close() để kiểm tra trước khi đóng
    public void close() {
        if (database != null && database.isOpen()) {
            dbHelper.close();
            database = null;
        }
    }

    // Thêm người dùng mới
    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_USER_AGE, user.calculateAge());
        values.put(DatabaseHelper.COLUMN_USER_GENDER, user.getGender());
        values.put(DatabaseHelper.COLUMN_USER_HEIGHT, user.getHeight());
        values.put(DatabaseHelper.COLUMN_USER_WEIGHT, user.getWeight());
        values.put(DatabaseHelper.COLUMN_USER_BLOOD_TYPE, user.getBloodType());

        if (user.getBirthdate() != null) {
            values.put(DatabaseHelper.COLUMN_USER_BIRTHDATE, user.getBirthdate().getTime());
        }

        values.put(DatabaseHelper.COLUMN_USER_HEART_RATE, user.getHeartRate());
        values.put(DatabaseHelper.COLUMN_USER_DOCTOR_NAME, user.getDoctorName());
        values.put(DatabaseHelper.COLUMN_USER_DOCTOR_PHONE, user.getDoctorPhone());
        values.put(DatabaseHelper.COLUMN_USER_MEDICATIONS, user.getMedications());
        values.put(DatabaseHelper.COLUMN_USER_PROFILE_IMAGE, user.getProfileImageUri());

        // Thêm các trường mới
        values.put(DatabaseHelper.COLUMN_USER_ADDRESS, user.getAddress());
        values.put(DatabaseHelper.COLUMN_USER_EMERGENCY_CONTACT, user.getEmergencyContact());
        values.put(DatabaseHelper.COLUMN_USER_MEDICAL_HISTORY, user.getMedicalHistory());
        values.put(DatabaseHelper.COLUMN_USER_ALLERGIES, user.getAllergies());
        values.put(DatabaseHelper.COLUMN_USER_INSURANCE, user.getInsuranceNumber());

        return database.insert(DatabaseHelper.TABLE_USER, null, values);
    }

    // Cập nhật thông tin người dùng
    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_USER_AGE, user.calculateAge());
        values.put(DatabaseHelper.COLUMN_USER_GENDER, user.getGender());
        values.put(DatabaseHelper.COLUMN_USER_HEIGHT, user.getHeight());
        values.put(DatabaseHelper.COLUMN_USER_WEIGHT, user.getWeight());
        values.put(DatabaseHelper.COLUMN_USER_BLOOD_TYPE, user.getBloodType());

        if (user.getBirthdate() != null) {
            values.put(DatabaseHelper.COLUMN_USER_BIRTHDATE, user.getBirthdate().getTime());
        }

        values.put(DatabaseHelper.COLUMN_USER_HEART_RATE, user.getHeartRate());
        values.put(DatabaseHelper.COLUMN_USER_DOCTOR_NAME, user.getDoctorName());
        values.put(DatabaseHelper.COLUMN_USER_DOCTOR_PHONE, user.getDoctorPhone());
        values.put(DatabaseHelper.COLUMN_USER_MEDICATIONS, user.getMedications());
        values.put(DatabaseHelper.COLUMN_USER_PROFILE_IMAGE, user.getProfileImageUri());

        // Thêm các trường mới
        values.put(DatabaseHelper.COLUMN_USER_ADDRESS, user.getAddress());
        values.put(DatabaseHelper.COLUMN_USER_EMERGENCY_CONTACT, user.getEmergencyContact());
        values.put(DatabaseHelper.COLUMN_USER_MEDICAL_HISTORY, user.getMedicalHistory());
        values.put(DatabaseHelper.COLUMN_USER_ALLERGIES, user.getAllergies());
        values.put(DatabaseHelper.COLUMN_USER_INSURANCE, user.getInsuranceNumber());

        return database.update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );
    }

    // Lấy thông tin người dùng
    public User getUser(long userId) {
        User user = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USER,
                null,
                DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    // Lấy người dùng đầu tiên (cho ứng dụng đơn người dùng)
    public User getFirstUser() {
        User user = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USER,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_USER_ID + " ASC",
                "1"
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    public User getCurrentUser() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USER,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_USER_ID + " DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    // Chuyển đổi Cursor thành đối tượng User
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)));
        user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_AGE)));
        user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_GENDER)));
        user.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_HEIGHT)));
        user.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_WEIGHT)));

        // Kiểm tra các cột mới
        int bloodTypeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_BLOOD_TYPE);
        if (bloodTypeIndex != -1) {
            user.setBloodType(cursor.getString(bloodTypeIndex));
        }

        int birthdateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_BIRTHDATE);
        if (birthdateIndex != -1 && !cursor.isNull(birthdateIndex)) {
            long birthdateMillis = cursor.getLong(birthdateIndex);
            user.setBirthdate(new Date(birthdateMillis));
        }

        int heartRateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_HEART_RATE);
        if (heartRateIndex != -1) {
            user.setHeartRate(cursor.getInt(heartRateIndex));
        }

        int doctorNameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_DOCTOR_NAME);
        if (doctorNameIndex != -1) {
            user.setDoctorName(cursor.getString(doctorNameIndex));
        }

        int doctorPhoneIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_DOCTOR_PHONE);
        if (doctorPhoneIndex != -1) {
            user.setDoctorPhone(cursor.getString(doctorPhoneIndex));
        }

        int medicationsIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_MEDICATIONS);
        if (medicationsIndex != -1) {
            user.setMedications(cursor.getString(medicationsIndex));
        }

        int profileImageIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_PROFILE_IMAGE);
        if (profileImageIndex != -1) {
            user.setProfileImageUri(cursor.getString(profileImageIndex));
        }

        // Đọc các trường mới
        int addressIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ADDRESS);
        if (addressIndex != -1) {
            user.setAddress(cursor.getString(addressIndex));
        }

        int emergencyContactIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_EMERGENCY_CONTACT);
        if (emergencyContactIndex != -1) {
            user.setEmergencyContact(cursor.getString(emergencyContactIndex));
        }

        int medicalHistoryIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_MEDICAL_HISTORY);
        if (medicalHistoryIndex != -1) {
            user.setMedicalHistory(cursor.getString(medicalHistoryIndex));
        }

        int allergiesIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ALLERGIES);
        if (allergiesIndex != -1) {
            user.setAllergies(cursor.getString(allergiesIndex));
        }

        int insuranceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_INSURANCE);
        if (insuranceIndex != -1) {
            user.setInsuranceNumber(cursor.getString(insuranceIndex));
        }

        return user;
    }
}
