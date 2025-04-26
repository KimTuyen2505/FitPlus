package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.firstproject.models.User;

import java.util.Date;

public class UserDAO {
    private static final String TAG = "UserDAO";
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Open database connection
    public void open() {
        try {
            database = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e(TAG, "Error opening database: " + e.getMessage());
        }
    }

    // Close database connection
    public void close() {
        try {
            if (database != null && database.isOpen()) {
                database.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing database: " + e.getMessage());
        }
    }

    // Add new user
    public long insertUser(User user) {
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

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

            // Add new fields
            values.put(DatabaseHelper.COLUMN_USER_ADDRESS, user.getAddress());
            values.put(DatabaseHelper.COLUMN_USER_EMERGENCY_CONTACT, user.getEmergencyContact());
            values.put(DatabaseHelper.COLUMN_USER_MEDICAL_HISTORY, user.getMedicalHistory());
            values.put(DatabaseHelper.COLUMN_USER_ALLERGIES, user.getAllergies());
            values.put(DatabaseHelper.COLUMN_USER_INSURANCE, user.getInsuranceNumber());

            return database.insert(DatabaseHelper.TABLE_USER, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user: " + e.getMessage());
            return -1;
        }
    }

    // Update user information
    public int updateUser(User user) {
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

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

            // Add new fields
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
        } catch (Exception e) {
            Log.e(TAG, "Error updating user: " + e.getMessage());
            return 0;
        }
    }

    // Get user by ID
    public User getUser(long userId) {
        User user = null;

        try {
            if (database == null || !database.isOpen()) {
                open();
            }

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
        } catch (Exception e) {
            Log.e(TAG, "Error getting user: " + e.getMessage());
        }

        return user;
    }

    // Get first user (for single-user app)
    public User getFirstUser() {
        User user = null;

        try {
            if (database == null || !database.isOpen()) {
                open();
            }

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
        } catch (Exception e) {
            Log.e(TAG, "Error getting first user: " + e.getMessage());
        }

        return user;
    }

    // Get current user
    public User getCurrentUser() {
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            Cursor cursor = database.query(
                    DatabaseHelper.TABLE_USER,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_USER_ID + " DESC",
                    "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                User user = cursorToUser(cursor);
                cursor.close();
                return user;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting current user: " + e.getMessage());
        }

        return null;
    }

    // Convert cursor to User object
    private User cursorToUser(Cursor cursor) {
        try {
            User user = new User();

            user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_AGE)));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_GENDER)));
            user.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_HEIGHT)));
            user.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_WEIGHT)));

            // Check new columns
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

            // Read new fields
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
        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to user: " + e.getMessage());
            return null;
        }
    }
}
