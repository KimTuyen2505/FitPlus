package com.example.firstproject.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "health_management.db";
    private static final int DATABASE_VERSION = 2; // Tăng version để trigger onUpgrade

    // Bảng User
    public static final String TABLE_USER = "user";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_AGE = "age";
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_HEIGHT = "height";
    public static final String COLUMN_USER_WEIGHT = "weight";
    public static final String COLUMN_USER_BLOOD_TYPE = "blood_type";

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
    public static final String COLUMN_REMINDER_USER_ID = "user_id";
    public static final String COLUMN_REMINDER_IS_ACTIVE = "is_active"; // Thêm cột mới

    // Bảng MedicalRecord
    public static final String TABLE_MEDICAL_RECORD = "medical_record";
    public static final String COLUMN_MEDICAL_RECORD_ID = "id";
    public static final String COLUMN_MEDICAL_RECORD_TITLE = "title";
    public static final String COLUMN_MEDICAL_RECORD_DESCRIPTION = "description";
    public static final String COLUMN_MEDICAL_RECORD_DATE = "date";
    public static final String COLUMN_MEDICAL_RECORD_DOCTOR = "doctor";
    public static final String COLUMN_MEDICAL_RECORD_USER_ID = "user_id";

    // Bảng MenstrualCycle
    public static final String TABLE_MENSTRUAL_CYCLE = "menstrual_cycle";
    public static final String COLUMN_MENSTRUAL_CYCLE_ID = "id";
    public static final String COLUMN_MENSTRUAL_CYCLE_START_DATE = "start_date";
    public static final String COLUMN_MENSTRUAL_CYCLE_END_DATE = "end_date";
    public static final String COLUMN_MENSTRUAL_CYCLE_SYMPTOMS = "symptoms";
    public static final String COLUMN_MENSTRUAL_CYCLE_NOTES = "notes";
    public static final String COLUMN_MENSTRUAL_CYCLE_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng User
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_AGE + " INTEGER,"
                + COLUMN_USER_GENDER + " TEXT,"
                + COLUMN_USER_HEIGHT + " REAL,"
                + COLUMN_USER_WEIGHT + " REAL,"
                + COLUMN_USER_BLOOD_TYPE + " TEXT"
                + ")";
        db.execSQL(CREATE_USER_TABLE);

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
    }

    // Instance duy nhất của DatabaseHelper (Singleton pattern)
    private static DatabaseHelper instance;

    // Phương thức để lấy instance duy nhất
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_MEASUREMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICAL_RECORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENSTRUAL_CYCLE);

        // Create tables again
        onCreate(db);
    }
}

