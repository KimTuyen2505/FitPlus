package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.MedicalRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicalRecordDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public MedicalRecordDAO(Context context) {
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

    // Thêm hồ sơ y tế mới
    public long insertMedicalRecord(MedicalRecord record) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_TITLE, record.getTitle());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_DESCRIPTION, record.getDescription());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_DATE, record.getDate().getTime());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_DOCTOR, record.getDoctor());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_HOSPITAL, record.getHospital());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_USER_ID, 1); // Mặc định user_id = 1 cho ứng dụng đơn người dùng

        return database.insert(DatabaseHelper.TABLE_MEDICAL_RECORD, null, values);
    }

    // Cập nhật hồ sơ y tế
    public int updateMedicalRecord(MedicalRecord record) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_TITLE, record.getTitle());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_DESCRIPTION, record.getDescription());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_DATE, record.getDate().getTime());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_DOCTOR, record.getDoctor());
        values.put(DatabaseHelper.COLUMN_MEDICAL_RECORD_HOSPITAL, record.getHospital());

        return database.update(
                DatabaseHelper.TABLE_MEDICAL_RECORD,
                values,
                DatabaseHelper.COLUMN_MEDICAL_RECORD_ID + " = ?",
                new String[]{String.valueOf(record.getId())}
        );
    }

    // Xóa hồ sơ y tế
    public int deleteMedicalRecord(long id) {
        return database.delete(
                DatabaseHelper.TABLE_MEDICAL_RECORD,
                DatabaseHelper.COLUMN_MEDICAL_RECORD_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Lấy tất cả hồ sơ y tế
    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> records = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_MEDICAL_RECORD,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_MEDICAL_RECORD_DATE + " DESC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MedicalRecord record = cursorToMedicalRecord(cursor);
                records.add(record);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return records;
    }

    // Tìm kiếm hồ sơ y tế theo từ khóa
    public List<MedicalRecord> searchMedicalRecords(String keyword) {
        List<MedicalRecord> records = new ArrayList<>();

        // Tạo câu truy vấn tìm kiếm
        String selection =
                DatabaseHelper.COLUMN_MEDICAL_RECORD_TITLE + " LIKE ? OR " +
                        DatabaseHelper.COLUMN_MEDICAL_RECORD_DESCRIPTION + " LIKE ? OR " +
                        DatabaseHelper.COLUMN_MEDICAL_RECORD_DOCTOR + " LIKE ? OR " +
                        DatabaseHelper.COLUMN_MEDICAL_RECORD_HOSPITAL + " LIKE ?";

        String[] selectionArgs = new String[] {
                "%" + keyword + "%",
                "%" + keyword + "%",
                "%" + keyword + "%",
                "%" + keyword + "%"
        };

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_MEDICAL_RECORD,
                null,
                selection,
                selectionArgs,
                null,
                null,
                DatabaseHelper.COLUMN_MEDICAL_RECORD_DATE + " DESC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MedicalRecord record = cursorToMedicalRecord(cursor);
                records.add(record);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return records;
    }

    // Lấy hồ sơ y tế theo ID
    public MedicalRecord getMedicalRecordById(long id) {
        MedicalRecord record = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_MEDICAL_RECORD,
                null,
                DatabaseHelper.COLUMN_MEDICAL_RECORD_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            record = cursorToMedicalRecord(cursor);
            cursor.close();
        }

        return record;
    }

    // Chuyển đổi Cursor thành đối tượng MedicalRecord
    private MedicalRecord cursorToMedicalRecord(Cursor cursor) {
        MedicalRecord record = new MedicalRecord();
        record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEDICAL_RECORD_ID)));
        record.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEDICAL_RECORD_TITLE)));
        record.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEDICAL_RECORD_DESCRIPTION)));

        long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEDICAL_RECORD_DATE));
        record.setDate(new Date(dateMillis));

        record.setDoctor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEDICAL_RECORD_DOCTOR)));
        record.setHospital(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEDICAL_RECORD_HOSPITAL)));

        return record;
    }
}
