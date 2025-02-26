package com.example.firstproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.MedicalRecord;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public MedicalRecordDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm hồ sơ y tế mới
    public long insertMedicalRecord(MedicalRecord record) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, record.getTitle());
        values.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        values.put(DatabaseHelper.COLUMN_DOCTOR, record.getDoctor());
        values.put(DatabaseHelper.COLUMN_NOTES, record.getNotes());

        return database.insert(DatabaseHelper.TABLE_MEDICAL_RECORDS, null, values);
    }

    // Cập nhật hồ sơ y tế
    public int updateMedicalRecord(MedicalRecord record) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, record.getTitle());
        values.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        values.put(DatabaseHelper.COLUMN_DOCTOR, record.getDoctor());
        values.put(DatabaseHelper.COLUMN_NOTES, record.getNotes());

        return database.update(
                DatabaseHelper.TABLE_MEDICAL_RECORDS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(record.getId())}
        );
    }

    // Xóa hồ sơ y tế
    public int deleteMedicalRecord(long id) {
        return database.delete(
                DatabaseHelper.TABLE_MEDICAL_RECORDS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Lấy tất cả hồ sơ y tế
    public List<MedicalRecord> getAllMedicalRecords() {
        List<MedicalRecord> records = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_MEDICAL_RECORDS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_DATE + " DESC"
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
                DatabaseHelper.TABLE_MEDICAL_RECORDS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
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
        record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        record.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)));
        record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)));
        record.setDoctor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DOCTOR)));
        record.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTES)));
        return record;
    }
}

