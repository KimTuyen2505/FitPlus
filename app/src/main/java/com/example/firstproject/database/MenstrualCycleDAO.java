package com.example.firstproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.MenstrualCycle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenstrualCycleDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private SimpleDateFormat dateFormat;

    public MenstrualCycleDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm chu kỳ kinh nguyệt mới
    public long insertMenstrualCycle(MenstrualCycle cycle) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_START_DATE, dateFormat.format(cycle.getStartDate()));

        if (cycle.getEndDate() != null) {
            values.put(DatabaseHelper.COLUMN_END_DATE, dateFormat.format(cycle.getEndDate()));
        }

        values.put(DatabaseHelper.COLUMN_SYMPTOMS, cycle.getSymptoms());

        return database.insert(DatabaseHelper.TABLE_MENSTRUAL_CYCLES, null, values);
    }

    // Cập nhật chu kỳ kinh nguyệt
    public int updateMenstrualCycle(MenstrualCycle cycle) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_START_DATE, dateFormat.format(cycle.getStartDate()));

        if (cycle.getEndDate() != null) {
            values.put(DatabaseHelper.COLUMN_END_DATE, dateFormat.format(cycle.getEndDate()));
        }

        values.put(DatabaseHelper.COLUMN_SYMPTOMS, cycle.getSymptoms());

        return database.update(
                DatabaseHelper.TABLE_MENSTRUAL_CYCLES,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(cycle.getId())}
        );
    }

    // Xóa chu kỳ kinh nguyệt
    public int deleteMenstrualCycle(long id) {
        return database.delete(
                DatabaseHelper.TABLE_MENSTRUAL_CYCLES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Lấy tất cả chu kỳ kinh nguyệt
    public List<MenstrualCycle> getAllMenstrualCycles() {
        List<MenstrualCycle> cycles = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_MENSTRUAL_CYCLES,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_START_DATE + " DESC"
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
                DatabaseHelper.TABLE_MENSTRUAL_CYCLES,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_START_DATE + " DESC",
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
        cycle.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));

        String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_DATE));
        try {
            cycle.setStartDate(dateFormat.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
            cycle.setStartDate(new Date());
        }

        int endDateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_END_DATE);
        if (endDateColumnIndex != -1 && !cursor.isNull(endDateColumnIndex)) {
            String endDate = cursor.getString(endDateColumnIndex);
            try {
                cycle.setEndDate(dateFormat.parse(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        cycle.setSymptoms(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SYMPTOMS)));
        return cycle;
    }
}

