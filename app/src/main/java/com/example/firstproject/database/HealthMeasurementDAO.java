package com.example.firstproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.HealthMeasurement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HealthMeasurementDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private SimpleDateFormat dateFormat;

    public HealthMeasurementDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm chỉ số sức khỏe mới
    public long insertMeasurement(HealthMeasurement measurement) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_TYPE, measurement.getType());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_VALUE, measurement.getValue());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_DATE, dateFormat.format(measurement.getDate()));

        return database.insert(DatabaseHelper.TABLE_HEALTH_MEASUREMENTS, null, values);
    }

    // Cập nhật chỉ số sức khỏe
    public int updateMeasurement(HealthMeasurement measurement) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_TYPE, measurement.getType());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_VALUE, measurement.getValue());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_DATE, dateFormat.format(measurement.getDate()));

        return database.update(
                DatabaseHelper.TABLE_HEALTH_MEASUREMENTS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(measurement.getId())}
        );
    }

    // Xóa chỉ số sức khỏe
    public int deleteMeasurement(long id) {
        return database.delete(
                DatabaseHelper.TABLE_HEALTH_MEASUREMENTS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Lấy tất cả chỉ số sức khỏe theo loại
    public List<HealthMeasurement> getMeasurementsByType(String type) {
        List<HealthMeasurement> measurements = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_HEALTH_MEASUREMENTS,
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

        return measurements;
    }

    // Lấy chỉ số sức khỏe mới nhất theo loại
    public HealthMeasurement getLatestMeasurementByType(String type) {
        HealthMeasurement measurement = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_HEALTH_MEASUREMENTS,
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

        return measurement;
    }

    // Chuyển đổi Cursor thành đối tượng HealthMeasurement
    private HealthMeasurement cursorToMeasurement(Cursor cursor) {
        HealthMeasurement measurement = new HealthMeasurement();
        measurement.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        measurement.setType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_TYPE)));
        measurement.setValue(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_VALUE)));

        String dateString = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_DATE));
        try {
            Date date = dateFormat.parse(dateString);
            measurement.setDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
            measurement.setDate(new Date());
        }

        return measurement;
    }
}
