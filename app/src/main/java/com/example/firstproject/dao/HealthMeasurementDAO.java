package com.example.firstproject.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.dao.DatabaseHelper;
import com.example.firstproject.models.HealthMeasurement;

import java.util.ArrayList;
import java.util.List;

public class HealthMeasurementDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public HealthMeasurementDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertMeasurement(HealthMeasurement measurement) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_TYPE, measurement.getMeasurementType());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_VALUE, measurement.getMeasurementValue());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_DATE, measurement.getMeasurementDate());

        return database.insert(DatabaseHelper.TABLE_HEALTH_MEASUREMENTS, null, values);
    }

    public int updateMeasurement(HealthMeasurement measurement) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_TYPE, measurement.getMeasurementType());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_VALUE, measurement.getMeasurementValue());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_DATE, measurement.getMeasurementDate());

        return database.update(DatabaseHelper.TABLE_HEALTH_MEASUREMENTS, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(measurement.getId())});
    }

    public void deleteMeasurement(long id) {
        database.delete(DatabaseHelper.TABLE_HEALTH_MEASUREMENTS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<HealthMeasurement> getAllMeasurements() {
        List<HealthMeasurement> measurements = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_HEALTH_MEASUREMENTS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_MEASUREMENT_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                HealthMeasurement measurement = cursorToMeasurement(cursor);
                measurements.add(measurement);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return measurements;
    }

    public List<HealthMeasurement> getMeasurementsByType(String type) {
        List<HealthMeasurement> measurements = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_HEALTH_MEASUREMENTS,
                null,
                DatabaseHelper.COLUMN_MEASUREMENT_TYPE + " = ?",
                new String[]{type},
                null, null,
                DatabaseHelper.COLUMN_MEASUREMENT_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                HealthMeasurement measurement = cursorToMeasurement(cursor);
                measurements.add(measurement);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return measurements;
    }

    public HealthMeasurement getLatestMeasurementByType(String type) {
        HealthMeasurement measurement = null;

        String[] columns = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_MEASUREMENT_TYPE,
                DatabaseHelper.COLUMN_MEASUREMENT_VALUE,
                DatabaseHelper.COLUMN_MEASUREMENT_DATE,
                DatabaseHelper.COLUMN_CREATED_AT
        };

        String selection = DatabaseHelper.COLUMN_MEASUREMENT_TYPE + " = ?";
        String[] selectionArgs = {type};
        String orderBy = DatabaseHelper.COLUMN_MEASUREMENT_DATE + " DESC";
        String limit = "1";

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_HEALTH_MEASUREMENTS,
                columns,
                selection,
                selectionArgs,
                null,  // groupBy
                null,  // having
                orderBy,
                limit
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    measurement = new HealthMeasurement();
                    measurement.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
                    measurement.setMeasurementType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_TYPE)));
                    measurement.setMeasurementValue(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_VALUE)));
                    measurement.setMeasurementDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_DATE)));
                    measurement.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }

        return measurement;
    }



    @SuppressLint("Range")
    private HealthMeasurement cursorToMeasurement(Cursor cursor) {
        HealthMeasurement measurement = new HealthMeasurement();
        measurement.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        measurement.setMeasurementType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MEASUREMENT_TYPE)));
        measurement.setMeasurementValue(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_MEASUREMENT_VALUE)));
        measurement.setMeasurementDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MEASUREMENT_DATE)));
        measurement.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
        return measurement;
    }
}

