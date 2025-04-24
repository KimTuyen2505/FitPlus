package com.example.firstproject.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "health_management.db";
    private static final int DATABASE_VERSION = 2; // Increased version for new tables

    // Bảng User
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_AGE = "age";
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_HEIGHT = "height";
    public static final String COLUMN_USER_WEIGHT = "weight";
    public static final String COLUMN_USER_BLOOD_TYPE = "blood_type";
    public static final String COLUMN_USER_BIRTHDATE = "birthdate";
    public static final String COLUMN_USER_HEART_RATE = "heart_rate";
    public static final String COLUMN_USER_DOCTOR_NAME = "doctor_name";
    public static final String COLUMN_USER_DOCTOR_PHONE = "doctor_phone";
    public static final String COLUMN_USER_MEDICATIONS = "medications";
    public static final String COLUMN_USER_PROFILE_IMAGE = "profile_image";
    // Thêm các cột mới
    public static final String COLUMN_USER_ADDRESS = "address";
    public static final String COLUMN_USER_EMERGENCY_CONTACT = "emergency_contact";
    public static final String COLUMN_USER_MEDICAL_HISTORY = "medical_history";
    public static final String COLUMN_USER_ALLERGIES = "allergies";
    public static final String COLUMN_USER_INSURANCE = "insurance";

    // Bảng Account
    public static final String TABLE_ACCOUNT = "account";
    public static final String COLUMN_ACCOUNT_ID = "id";
    public static final String COLUMN_ACCOUNT_EMAIL = "email";
    public static final String COLUMN_ACCOUNT_PASSWORD = "password";
    public static final String COLUMN_ACCOUNT_USER_ID = "user_id";

    // Bảng HealthMeasurement
    public static final String TABLE_HEALTH_MEASUREMENT = "health_measurement";
    public static final String COLUMN_MEASUREMENT_ID = "id";
    public static final String COLUMN_MEASUREMENT_TYPE = "type";
    public static final String COLUMN_MEASUREMENT_VALUE = "value";
    public static final String COLUMN_MEASUREMENT_DATE = "date";
    public static final String COLUMN_MEASUREMENT_USER_ID = "user_id";

    // Bảng Reminder
    public static final String TABLE_REMINDER = "reminder";
    public static final String COLUMN_REMINDER_ID = "id";
    public static final String COLUMN_REMINDER_TITLE = "title";
    public static final String COLUMN_REMINDER_TIME = "time";
    public static final String COLUMN_REMINDER_FREQUENCY = "frequency";
    public static final String COLUMN_REMINDER_TYPE = "type";
    public static final String COLUMN_REMINDER_PROGRESS = "progress";
    public static final String COLUMN_REMINDER_TARGET = "target";
    public static final String COLUMN_REMINDER_CURRENT = "current";
    public static final String COLUMN_REMINDER_USER_ID = "user_id";
    public static final String COLUMN_REMINDER_IS_ACTIVE = "is_active";

    // Bảng MedicalRecord
    public static final String TABLE_MEDICAL_RECORD = "medical_record";
    public static final String COLUMN_MEDICAL_RECORD_ID = "id";
    public static final String COLUMN_MEDICAL_RECORD_TITLE = "title";
    public static final String COLUMN_MEDICAL_RECORD_DESCRIPTION = "description";
    public static final String COLUMN_MEDICAL_RECORD_DATE = "date";
    public static final String COLUMN_MEDICAL_RECORD_DOCTOR = "doctor";
    public static final String COLUMN_MEDICAL_RECORD_HOSPITAL = "hospital";
    public static final String COLUMN_MEDICAL_RECORD_USER_ID = "user_id";

    // Bảng MenstrualCycle
    public static final String TABLE_MENSTRUAL_CYCLE = "menstrual_cycle";
    public static final String COLUMN_MENSTRUAL_CYCLE_ID = "id";
    public static final String COLUMN_MENSTRUAL_CYCLE_START_DATE = "start_date";
    public static final String COLUMN_MENSTRUAL_CYCLE_END_DATE = "end_date";
    public static final String COLUMN_MENSTRUAL_CYCLE_SYMPTOMS = "symptoms";
    public static final String COLUMN_MENSTRUAL_CYCLE_NOTES = "notes";
    public static final String COLUMN_MENSTRUAL_CYCLE_USER_ID = "user_id";

    // Bảng HealthSuggestionCategory (mới)
    public static final String TABLE_HEALTH_SUGGESTION_CATEGORY = "health_suggestion_category";
    public static final String COLUMN_HEALTH_SUGGESTION_CATEGORY_ID = "id";
    public static final String COLUMN_HEALTH_SUGGESTION_CATEGORY_NAME = "name";
    public static final String COLUMN_HEALTH_SUGGESTION_CATEGORY_ICON = "icon";
    public static final String COLUMN_HEALTH_SUGGESTION_CATEGORY_DESCRIPTION = "description";
    public static final String COLUMN_HEALTH_SUGGESTION_CATEGORY_COLOR = "color";

    // Bảng HealthSuggestion (mới)
    public static final String TABLE_HEALTH_SUGGESTION = "health_suggestion";
    public static final String COLUMN_HEALTH_SUGGESTION_ID = "id";
    public static final String COLUMN_HEALTH_SUGGESTION_TITLE = "title";
    public static final String COLUMN_HEALTH_SUGGESTION_CONTENT = "content";
    public static final String COLUMN_HEALTH_SUGGESTION_SOURCE = "source";
    public static final String COLUMN_HEALTH_SUGGESTION_CATEGORY_FK = "category_id"; // Đổi tên để tránh trùng lặp

    // Bảng PregnancyData (mới)
    public static final String TABLE_PREGNANCY_DATA = "pregnancy_data";
    public static final String COLUMN_PREGNANCY_ID = "id";
    public static final String COLUMN_PREGNANCY_USER_ID = "user_id";
    public static final String COLUMN_PREGNANCY_START_DATE = "start_date";
    public static final String COLUMN_PREGNANCY_DUE_DATE = "due_date";
    public static final String COLUMN_PREGNANCY_BABY_NAME = "baby_name";
    public static final String COLUMN_PREGNANCY_IS_CONFIRMED = "is_confirmed";
    public static final String COLUMN_PREGNANCY_LAST_UPDATED = "last_updated";

    // Bảng PregnancyDiary (mới)
    public static final String TABLE_PREGNANCY_DIARY = "pregnancy_diary";
    public static final String COLUMN_PREGNANCY_DIARY_ID = "id";
    public static final String COLUMN_PREGNANCY_DIARY_PREGNANCY_ID = "pregnancy_id";
    public static final String COLUMN_PREGNANCY_DIARY_TITLE = "title";
    public static final String COLUMN_PREGNANCY_DIARY_CONTENT = "content";
    public static final String COLUMN_PREGNANCY_DIARY_ENTRY_DATE = "entry_date";
    public static final String COLUMN_PREGNANCY_DIARY_WEEK = "pregnancy_week";

    // Bảng UltrasoundImage (mới)
    public static final String TABLE_ULTRASOUND_IMAGE = "ultrasound_image";
    public static final String COLUMN_ULTRASOUND_ID = "id";
    public static final String COLUMN_ULTRASOUND_PREGNANCY_ID = "pregnancy_id";
    public static final String COLUMN_ULTRASOUND_IMAGE_URI = "image_uri";
    public static final String COLUMN_ULTRASOUND_DATE = "image_date";
    public static final String COLUMN_ULTRASOUND_WEEK = "pregnancy_week";
    public static final String COLUMN_ULTRASOUND_NOTES = "notes";

    // Thêm phương thức getInstance để đảm bảo chỉ có một instance của DatabaseHelper
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Sửa constructor thành private để thực hiện Singleton pattern
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng User với các trường mới
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_AGE + " INTEGER,"
                + COLUMN_USER_GENDER + " TEXT,"
                + COLUMN_USER_HEIGHT + " REAL,"
                + COLUMN_USER_WEIGHT + " REAL,"
                + COLUMN_USER_BLOOD_TYPE + " TEXT,"
                + COLUMN_USER_BIRTHDATE + " INTEGER,"
                + COLUMN_USER_HEART_RATE + " INTEGER,"
                + COLUMN_USER_DOCTOR_NAME + " TEXT,"
                + COLUMN_USER_DOCTOR_PHONE + " TEXT,"
                + COLUMN_USER_MEDICATIONS + " TEXT,"
                + COLUMN_USER_PROFILE_IMAGE + " TEXT,"
                + COLUMN_USER_ADDRESS + " TEXT,"
                + COLUMN_USER_EMERGENCY_CONTACT + " TEXT,"
                + COLUMN_USER_MEDICAL_HISTORY + " TEXT,"
                + COLUMN_USER_ALLERGIES + " TEXT,"
                + COLUMN_USER_INSURANCE + " TEXT"
                + ")";
        db.execSQL(CREATE_USER_TABLE);

        // Tạo bảng Account
        String CREATE_ACCOUNT_TABLE = "CREATE TABLE " + TABLE_ACCOUNT + "("
                + COLUMN_ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ACCOUNT_EMAIL + " TEXT UNIQUE,"
                + COLUMN_ACCOUNT_PASSWORD + " TEXT,"
                + COLUMN_ACCOUNT_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_ACCOUNT_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_ACCOUNT_TABLE);

        // Tạo bảng HealthMeasurement
        String CREATE_HEALTH_MEASUREMENT_TABLE = "CREATE TABLE " + TABLE_HEALTH_MEASUREMENT + "("
                + COLUMN_MEASUREMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MEASUREMENT_TYPE + " TEXT,"
                + COLUMN_MEASUREMENT_VALUE + " REAL,"
                + COLUMN_MEASUREMENT_DATE + " INTEGER,"
                + COLUMN_MEASUREMENT_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_MEASUREMENT_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_HEALTH_MEASUREMENT_TABLE);

        // Tạo bảng Reminder
        String CREATE_REMINDER_TABLE = "CREATE TABLE " + TABLE_REMINDER + "("
                + COLUMN_REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_REMINDER_TITLE + " TEXT,"
                + COLUMN_REMINDER_TIME + " TEXT,"
                + COLUMN_REMINDER_FREQUENCY + " TEXT,"
                + COLUMN_REMINDER_TYPE + " TEXT,"
                + COLUMN_REMINDER_PROGRESS + " REAL,"
                + COLUMN_REMINDER_TARGET + " REAL,"
                + COLUMN_REMINDER_CURRENT + " REAL,"
                + COLUMN_REMINDER_IS_ACTIVE + " INTEGER DEFAULT 1," // Mặc định là active (1)
                + COLUMN_REMINDER_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_REMINDER_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_REMINDER_TABLE);

        // Tạo bảng MedicalRecord
        String CREATE_MEDICAL_RECORD_TABLE = "CREATE TABLE " + TABLE_MEDICAL_RECORD + "("
                + COLUMN_MEDICAL_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MEDICAL_RECORD_TITLE + " TEXT,"
                + COLUMN_MEDICAL_RECORD_DESCRIPTION + " TEXT,"
                + COLUMN_MEDICAL_RECORD_DATE + " INTEGER,"
                + COLUMN_MEDICAL_RECORD_DOCTOR + " TEXT,"
                + COLUMN_MEDICAL_RECORD_HOSPITAL + " TEXT,"
                + COLUMN_MEDICAL_RECORD_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_MEDICAL_RECORD_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_MEDICAL_RECORD_TABLE);

        // Tạo bảng MenstrualCycle
        String CREATE_MENSTRUAL_CYCLE_TABLE = "CREATE TABLE " + TABLE_MENSTRUAL_CYCLE + "("
                + COLUMN_MENSTRUAL_CYCLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MENSTRUAL_CYCLE_START_DATE + " INTEGER,"
                + COLUMN_MENSTRUAL_CYCLE_END_DATE + " INTEGER,"
                + COLUMN_MENSTRUAL_CYCLE_SYMPTOMS + " TEXT,"
                + COLUMN_MENSTRUAL_CYCLE_NOTES + " TEXT,"
                + COLUMN_MENSTRUAL_CYCLE_USER_ID + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_MENSTRUAL_CYCLE_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_MENSTRUAL_CYCLE_TABLE);

        // Tạo bảng HealthSuggestionCategory (mới)
        String CREATE_HEALTH_SUGGESTION_CATEGORY_TABLE = "CREATE TABLE " + TABLE_HEALTH_SUGGESTION_CATEGORY + "("
                + COLUMN_HEALTH_SUGGESTION_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HEALTH_SUGGESTION_CATEGORY_NAME + " TEXT,"
                + COLUMN_HEALTH_SUGGESTION_CATEGORY_ICON + " TEXT,"
                + COLUMN_HEALTH_SUGGESTION_CATEGORY_DESCRIPTION + " TEXT,"
                + COLUMN_HEALTH_SUGGESTION_CATEGORY_COLOR + " INTEGER"
                + ")";
        db.execSQL(CREATE_HEALTH_SUGGESTION_CATEGORY_TABLE);

        // Tạo bảng HealthSuggestion (mới)
        String CREATE_HEALTH_SUGGESTION_TABLE = "CREATE TABLE " + TABLE_HEALTH_SUGGESTION + "("
                + COLUMN_HEALTH_SUGGESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HEALTH_SUGGESTION_TITLE + " TEXT,"
                + COLUMN_HEALTH_SUGGESTION_CONTENT + " TEXT,"
                + COLUMN_HEALTH_SUGGESTION_SOURCE + " TEXT,"
                + COLUMN_HEALTH_SUGGESTION_CATEGORY_FK + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_HEALTH_SUGGESTION_CATEGORY_FK + ") REFERENCES "
                + TABLE_HEALTH_SUGGESTION_CATEGORY + "(" + COLUMN_HEALTH_SUGGESTION_CATEGORY_ID + ")"
                + ")";
        db.execSQL(CREATE_HEALTH_SUGGESTION_TABLE);

        // Tạo bảng PregnancyData (mới)
        String CREATE_PREGNANCY_DATA_TABLE = "CREATE TABLE " + TABLE_PREGNANCY_DATA + "("
                + COLUMN_PREGNANCY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PREGNANCY_USER_ID + " INTEGER,"
                + COLUMN_PREGNANCY_START_DATE + " INTEGER,"
                + COLUMN_PREGNANCY_DUE_DATE + " INTEGER,"
                + COLUMN_PREGNANCY_BABY_NAME + " TEXT,"
                + COLUMN_PREGNANCY_IS_CONFIRMED + " INTEGER DEFAULT 0,"
                + COLUMN_PREGNANCY_LAST_UPDATED + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_PREGNANCY_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_PREGNANCY_DATA_TABLE);

        // Tạo bảng PregnancyDiary (mới)
        String CREATE_PREGNANCY_DIARY_TABLE = "CREATE TABLE " + TABLE_PREGNANCY_DIARY + "("
                + COLUMN_PREGNANCY_DIARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PREGNANCY_DIARY_PREGNANCY_ID + " INTEGER,"
                + COLUMN_PREGNANCY_DIARY_TITLE + " TEXT,"
                + COLUMN_PREGNANCY_DIARY_CONTENT + " TEXT,"
                + COLUMN_PREGNANCY_DIARY_ENTRY_DATE + " INTEGER,"
                + COLUMN_PREGNANCY_DIARY_WEEK + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_PREGNANCY_DIARY_PREGNANCY_ID + ") REFERENCES "
                + TABLE_PREGNANCY_DATA + "(" + COLUMN_PREGNANCY_ID + ")"
                + ")";
        db.execSQL(CREATE_PREGNANCY_DIARY_TABLE);

        // Tạo bảng UltrasoundImage (mới)
        String CREATE_ULTRASOUND_IMAGE_TABLE = "CREATE TABLE " + TABLE_ULTRASOUND_IMAGE + "("
                + COLUMN_ULTRASOUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ULTRASOUND_PREGNANCY_ID + " INTEGER,"
                + COLUMN_ULTRASOUND_IMAGE_URI + " TEXT,"
                + COLUMN_ULTRASOUND_DATE + " INTEGER,"
                + COLUMN_ULTRASOUND_WEEK + " INTEGER,"
                + COLUMN_ULTRASOUND_NOTES + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_ULTRASOUND_PREGNANCY_ID + ") REFERENCES "
                + TABLE_PREGNANCY_DATA + "(" + COLUMN_PREGNANCY_ID + ")"
                + ")";
        db.execSQL(CREATE_ULTRASOUND_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Tạo bảng PregnancyData (mới)
            String CREATE_PREGNANCY_DATA_TABLE = "CREATE TABLE " + TABLE_PREGNANCY_DATA + "("
                    + COLUMN_PREGNANCY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PREGNANCY_USER_ID + " INTEGER,"
                    + COLUMN_PREGNANCY_START_DATE + " INTEGER,"
                    + COLUMN_PREGNANCY_DUE_DATE + " INTEGER,"
                    + COLUMN_PREGNANCY_BABY_NAME + " TEXT,"
                    + COLUMN_PREGNANCY_IS_CONFIRMED + " INTEGER DEFAULT 0,"
                    + COLUMN_PREGNANCY_LAST_UPDATED + " INTEGER,"
                    + "FOREIGN KEY(" + COLUMN_PREGNANCY_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + ")"
                    + ")";
            db.execSQL(CREATE_PREGNANCY_DATA_TABLE);

            // Tạo bảng PregnancyDiary (mới)
            String CREATE_PREGNANCY_DIARY_TABLE = "CREATE TABLE " + TABLE_PREGNANCY_DIARY + "("
                    + COLUMN_PREGNANCY_DIARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PREGNANCY_DIARY_PREGNANCY_ID + " INTEGER,"
                    + COLUMN_PREGNANCY_DIARY_TITLE + " TEXT,"
                    + COLUMN_PREGNANCY_DIARY_CONTENT + " TEXT,"
                    + COLUMN_PREGNANCY_DIARY_ENTRY_DATE + " INTEGER,"
                    + COLUMN_PREGNANCY_DIARY_WEEK + " INTEGER,"
                    + "FOREIGN KEY(" + COLUMN_PREGNANCY_DIARY_PREGNANCY_ID + ") REFERENCES "
                    + TABLE_PREGNANCY_DATA + "(" + COLUMN_PREGNANCY_ID + ")"
                    + ")";
            db.execSQL(CREATE_PREGNANCY_DIARY_TABLE);

            // Tạo bảng UltrasoundImage (mới)
            String CREATE_ULTRASOUND_IMAGE_TABLE = "CREATE TABLE " + TABLE_ULTRASOUND_IMAGE + "("
                    + COLUMN_ULTRASOUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ULTRASOUND_PREGNANCY_ID + " INTEGER,"
                    + COLUMN_ULTRASOUND_IMAGE_URI + " TEXT,"
                    + COLUMN_ULTRASOUND_DATE + " INTEGER,"
                    + COLUMN_ULTRASOUND_WEEK + " INTEGER,"
                    + COLUMN_ULTRASOUND_NOTES + " TEXT,"
                    + "FOREIGN KEY(" + COLUMN_ULTRASOUND_PREGNANCY_ID + ") REFERENCES "
                    + TABLE_PREGNANCY_DATA + "(" + COLUMN_PREGNANCY_ID + ")"
                    + ")";
            db.execSQL(CREATE_ULTRASOUND_IMAGE_TABLE);
        }
    }
}
