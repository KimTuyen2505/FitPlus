package com.example.firstproject.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HealthManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_REMINDERS = "reminders";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_HEALTH_MEASUREMENTS = "health_measurements";
    public static final String TABLE_MEDICAL_RECORDS = "medical_records";
    public static final String TABLE_MENSTRUAL_CYCLES = "menstrual_cycles";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";

    // REMINDERS table columns
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_FREQUENCY = "frequency";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TARGET = "target";
    public static final String COLUMN_CURRENT = "current";
    public static final String COLUMN_PROGRESS = "progress";

    // Cột bảng users
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";

    // Cột bảng health_measurements
    public static final String COLUMN_MEASUREMENT_TYPE = "measurement_type";
    public static final String COLUMN_MEASUREMENT_VALUE = "measurement_value";
    public static final String COLUMN_MEASUREMENT_DATE = "measurement_date";

    // Cột bảng medical_records
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DOCTOR = "doctor";
    public static final String COLUMN_NOTES = "notes";

    // Cột bảng menstrual_cycles
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_SYMPTOMS = "symptoms";

    // Create table statements
    private static final String CREATE_TABLE_REMINDERS = "CREATE TABLE " + TABLE_REMINDERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_TIME + " TEXT NOT NULL,"
            + COLUMN_FREQUENCY + " TEXT NOT NULL,"
            + COLUMN_TYPE + " TEXT NOT NULL,"
            + COLUMN_TARGET + " REAL,"
            + COLUMN_CURRENT + " REAL,"
            + COLUMN_PROGRESS + " REAL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_UPDATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    // SQL tạo bảng users
    private static final String SQL_CREATE_USERS_TABLE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_AGE + " INTEGER, " +
                    COLUMN_GENDER + " TEXT, " +
                    COLUMN_HEIGHT + " REAL, " +
                    COLUMN_WEIGHT + " REAL, " +
                    COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    // SQL tạo bảng health_measurements
    private static final String SQL_CREATE_HEALTH_MEASUREMENTS_TABLE =
            "CREATE TABLE " + TABLE_HEALTH_MEASUREMENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEASUREMENT_TYPE + " TEXT, " +
                    COLUMN_MEASUREMENT_VALUE + " REAL, " +
                    COLUMN_MEASUREMENT_DATE + " TIMESTAMP, " +
                    COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    // SQL tạo bảng medical_records
    private static final String SQL_CREATE_MEDICAL_RECORDS_TABLE =
            "CREATE TABLE " + TABLE_MEDICAL_RECORDS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_DOCTOR + " TEXT, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    // SQL tạo bảng menstrual_cycles
    private static final String SQL_CREATE_MENSTRUAL_CYCLES_TABLE =
            "CREATE TABLE " + TABLE_MENSTRUAL_CYCLES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_START_DATE + " TIMESTAMP, " +
                    COLUMN_END_DATE + " TIMESTAMP, " +
                    COLUMN_SYMPTOMS + " TEXT, " +
                    COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    // Instance duy nhất của DatabaseHelper (Singleton pattern)
    private static DatabaseHelper instance;

    // Phương thức để lấy instance duy nhất
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_REMINDERS);
        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_HEALTH_MEASUREMENTS_TABLE);
        db.execSQL(SQL_CREATE_MEDICAL_RECORDS_TABLE);
        db.execSQL(SQL_CREATE_MENSTRUAL_CYCLES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_MEASUREMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICAL_RECORDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENSTRUAL_CYCLES);

        // Create tables again
        onCreate(db);
    }
}

