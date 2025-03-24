package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.MenstrualCycle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MenstrualCycleDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public MenstrualCycleDAO(Context context) {
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

    // Thêm chu kỳ kinh nguyệt mới
    public long insertMenstrualCycle(MenstrualCycle cycle) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_START_DATE, cycle.getStartDate().getTime());

        if (cycle.getEndDate() != null) {
            values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_END_DATE, cycle.getEndDate().getTime());
        }

        values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_SYMPTOMS, cycle.getSymptoms());
        values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_NOTES, cycle.getNotes());
        values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_USER_ID, 1); // Mặc định user_id = 1 cho ứng dụng đơn người dùng

        return database.insert(DatabaseHelper.TABLE_MENSTRUAL_CYCLE, null, values);
    }

    // Cập nhật chu kỳ kinh nguyệt
    public int updateMenstrualCycle(MenstrualCycle cycle) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_START_DATE, cycle.getStartDate().getTime());

        if (cycle.getEndDate() != null) {
            values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_END_DATE, cycle.getEndDate().getTime());
        }

        values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_SYMPTOMS, cycle.getSymptoms());
        values.put(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_NOTES, cycle.getNotes());

        return database.update(
                DatabaseHelper.TABLE_MENSTRUAL_CYCLE,
                values,
                DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_ID + " = ?",
                new String[]{String.valueOf(cycle.getId())}
        );
    }

    // Xóa chu kỳ kinh nguyệt
    public int deleteMenstrualCycle(long id) {
        return database.delete(
                DatabaseHelper.TABLE_MENSTRUAL_CYCLE,
                DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Lấy tất cả chu kỳ kinh nguyệt
    public List<MenstrualCycle> getAllMenstrualCycles() {
        List<MenstrualCycle> cycles = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_MENSTRUAL_CYCLE,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_START_DATE + " DESC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MenstrualCycle cycle = cursorToMenstrualCycle(cursor);
                cycles.add(cycle);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return cycles;
    }

    // Lấy chu kỳ kinh nguyệt gần nhất
    public MenstrualCycle getLatestMenstrualCycle() {
        MenstrualCycle cycle = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_MENSTRUAL_CYCLE,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_START_DATE + " DESC",
                "1"
        );

        if (cursor != null && cursor.moveToFirst()) {
            cycle = cursorToMenstrualCycle(cursor);
            cursor.close();
        }

        return cycle;
    }

    // Chuyển đổi Cursor thành đối tượng MenstrualCycle
    private MenstrualCycle cursorToMenstrualCycle(Cursor cursor) {
        MenstrualCycle cycle = new MenstrualCycle();
        cycle.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_ID)));

        long startDateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_START_DATE));
        cycle.setStartDate(new Date(startDateMillis));

        int endDateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_END_DATE);
        if (endDateColumnIndex != -1 && !cursor.isNull(endDateColumnIndex)) {
            long endDateMillis = cursor.getLong(endDateColumnIndex);
            cycle.setEndDate(new Date(endDateMillis));
        }

        cycle.setSymptoms(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_SYMPTOMS)));

        int notesColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_MENSTRUAL_CYCLE_NOTES);
        if (notesColumnIndex != -1 && !cursor.isNull(notesColumnIndex)) {
            cycle.setNotes(cursor.getString(notesColumnIndex));
        }

        return cycle;
    }
}



