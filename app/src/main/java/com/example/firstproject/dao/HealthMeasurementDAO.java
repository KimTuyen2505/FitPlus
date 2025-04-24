package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.HealthMeasurement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HealthMeasurementDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public HealthMeasurementDAO(Context context) {
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

    // Thêm chỉ số sức khỏe mới
    public long insertMeasurement(HealthMeasurement measurement) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_TYPE, measurement.getType());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_VALUE, measurement.getValue());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_DATE, measurement.getDate().getTime());
        values.put(DatabaseHelper.COLUMN_MEASUREMENT_USER_ID, 1); // Mặc định user_id = 1 cho ứng dụng đơn người dùng

        return database.insert(DatabaseHelper.TABLE_HEALTH_MEASUREMENT, null, values);
    }

    // Cập nhật chỉ số sức khỏe
    public int updateMeasurement(HealthMeasurement measurement) {
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
    }

    // Xóa chỉ số sức khỏe
    public int deleteMeasurement(long id) {
        return database.delete(
                DatabaseHelper.TABLE_HEALTH_MEASUREMENT,
                DatabaseHelper.COLUMN_MEASUREMENT_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Thêm kiểm tra null trong các phương thức truy vấn
    public List<HealthMeasurement> getMeasurementsByType(String type) {
        List<HealthMeasurement> measurements = new ArrayList<>();

        if (database == null) {
            open();
        }

        if (database != null) {
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
        }

        return measurements;
    }

    // Lấy chỉ số sức khỏe mới nhất theo loại
    public HealthMeasurement getLatestMeasurementByType(String type) {
        HealthMeasurement measurement = null;
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

        return measurement;
    }

    // Chuyển đổi Cursor thành đối tượng HealthMeasurement
    private HealthMeasurement cursorToMeasurement(Cursor cursor) {
        HealthMeasurement measurement = new HealthMeasurement();
        measurement.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_ID)));
        measurement.setType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_TYPE)));
        measurement.setValue(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_VALUE)));

        long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEASUREMENT_DATE));
        measurement.setDate(new Date(dateMillis));

        return measurement;
    }
}
