package com.example.firstproject.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.dao.DatabaseHelper;
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

    public long insertRecord(MedicalRecord record) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, record.getTitle());
        values.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        values.put(DatabaseHelper.COLUMN_DOCTOR, record.getDoctor());
        values.put(DatabaseHelper.COLUMN_NOTES, record.getNotes());

        return database.insert(DatabaseHelper.TABLE_MEDICAL_RECORDS, null, values);
    }

    public int updateRecord(MedicalRecord record) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, record.getTitle());
        values.put(DatabaseHelper.COLUMN_DATE, record.getDate());
        values.put(DatabaseHelper.COLUMN_DOCTOR, record.getDoctor());
        values.put(DatabaseHelper.COLUMN_NOTES, record.getNotes());

        return database.update(DatabaseHelper.TABLE_MEDICAL_RECORDS, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(record.getId())});
    }

    public void deleteRecord(long id) {
        database.delete(DatabaseHelper.TABLE_MEDICAL_RECORDS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<MedicalRecord> getAllRecords() {
        List<MedicalRecord> records = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_MEDICAL_RECORDS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                MedicalRecord record = cursorToRecord(cursor);
                records.add(record);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return records;
    }

    public MedicalRecord getRecord(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_MEDICAL_RECORDS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            MedicalRecord record = cursorToRecord(cursor);
            cursor.close();
            return record;
        }
        return null;
    }
    @SuppressLint("Range")
    private MedicalRecord cursorToRecord(Cursor cursor) {
        MedicalRecord record = new MedicalRecord();
        record.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        record.setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE)));
        record.setDate(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)));
        record.setDoctor(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DOCTOR)));
        record.setNotes(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOTES)));
        record.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
        return record;
    }
}

