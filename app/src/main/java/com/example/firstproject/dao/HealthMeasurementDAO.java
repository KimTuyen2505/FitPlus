package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.firstproject.models.HealthMeasurement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HealthMeasurementDAO {
    private static final String TAG = "HealthMeasurementDAO";
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public HealthMeasurementDAO(Context context) {
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
            dbHelper.close();
        } catch (Exception e) {
            Log.e(TAG, "Error closing database: " + e.getMessage());
        }
    }

    // Add new health measurement
    public long insertMeasurement(HealthMeasurement measurement) {
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_MEASUREMENT_TYPE, measurement.getType());
            values.put(DatabaseHelper.COLUMN_MEASUREMENT_VALUE, measurement.getValue());
            values.put(DatabaseHelper.COLUMN_MEASUREMENT_DATE, measurement.getDate().getTime());
            values.put(DatabaseHelper.COLUMN_MEASUREMENT_USER_ID, 1); // Default user_id = 1 for single-user app

            return database.insert(DatabaseHelper.TABLE_HEALTH_MEASUREMENT, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting measurement: " + e.getMessage());
            return -1;
        }
    }

    // Update health measurement
    public int updateMeasurement(HealthMeasurement measurement) {
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_MEASUREMENT_TYPE, measurement.getType());
            values.put(DatabaseHelper.COLUMN_MEASUREMENT_VALUE, measurement.getValue());
            values.put(DatabaseHelper.COLUMN_MEASUREMENT_DATE, measurement.getDate().getTime());

            return database.update(
                    DatabaseHelper.TABLE_HEALTH_MEASUREMENT,
                    values,
                    DatabaseHelper.COLUMN_MEASUREMENT_ID + " = ?",
                    new String[]{String.valueOf(measurement.getId())}
            );
        } catch (Exception e) {
            Log.e(TAG, "Error updating measurement: " + e.getMessage());
            return 0;
        }
    }

    // Delete health measurement
    public int deleteMeasurement(long id) {
        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            return database.delete(
                    DatabaseHelper.TABLE_HEALTH_MEASUREMENT,
                    DatabaseHelper.COLUMN_MEASUREMENT_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
        } catch (Exception e) {
            Log.e(TAG, "Error deleting measurement: " + e.getMessage());
            return 0;
        }
    }

    // Get measurements by type
    public List<HealthMeasurement> getMeasurementsByType(String type) {
        List<HealthMeasurement> measurements = new ArrayList<>();

        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            Cursor cursor = database.query(
                    DatabaseHelper.TABLE_HEALTH_MEASUREMENT,
                    null,
                    DatabaseHelper.COLUMN_MEASUREMENT_TYPE + " = ?",
                    new String[]{type},
                    null,
                    null,
                    DatabaseHelper.COLUMN_MEASUREMENT_DATE + " DESC"
            );

            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    HealthMeasurement measurement = cursorToMeasurement(cursor);
                    measurements.add(measurement);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting measurements by type: " + e.getMessage());
        }

        return measurements;
    }

    // Get latest measurement by type
    public HealthMeasurement getLatestMeasurementByType(String type) {
        HealthMeasurement measurement = null;

        try {
            if (database == null || !database.isOpen()) {
                open();
            }

            Cursor cursor = database.query(
                    DatabaseHelper.TABLE_HEALTH_MEASUREMENT,
                    null,
                    DatabaseHelper.COLUMN_MEASUREMENT_TYPE + " = ?",
                    new String[]{type},
                    null,
                    null,
                    DatabaseHelper.COLUMN_MEASUREMENT_DATE + " DESC",
                    "1"
            );

            if (cursor != null && cursor.moveToFirst()) {
                measurement = cursorToMeasurement(cursor);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting latest measurement: " + e.getMessage());
        }

        return measurement;
    }

    // Convert cursor to HealthMeasurement object
    private HealthMeasurement cursorToMeasurement(Cursor cursor) {
        try {
            HealthMeasurement measurement = new HealthMeasurement();
            measurement.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_ID)));
            measurement.setType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_TYPE)));
            measurement.setValue(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_VALUE)));

            long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_DATE));
            measurement.setDate(new Date(dateMillis));

            return measurement;
        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to measurement: " + e.getMessage());
            return null;
        }
    }
}
