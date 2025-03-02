package com.example.firstproject.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.dao.DatabaseHelper;
import com.example.firstproject.models.MenstrualCycle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenstrualCycleDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private SimpleDateFormat dateFormat;

    public MenstrualCycleDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertCycle(MenstrualCycle cycle) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_START_DATE, dateFormat.format(cycle.getStartDate()));
        if (cycle.getEndDate() != null) {
            values.put(DatabaseHelper.COLUMN_END_DATE, dateFormat.format(cycle.getEndDate()));
        }
        values.put(DatabaseHelper.COLUMN_SYMPTOMS, cycle.getSymptoms());

        return database.insert(DatabaseHelper.TABLE_MENSTRUAL_CYCLES, null, values);
    }

    public int updateCycle(MenstrualCycle cycle) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_START_DATE, dateFormat.format(cycle.getStartDate()));
        if (cycle.getEndDate() != null) {
            values.put(DatabaseHelper.COLUMN_END_DATE, dateFormat.format(cycle.getEndDate()));
        }
        values.put(DatabaseHelper.COLUMN_SYMPTOMS, cycle.getSymptoms());

        return database.update(DatabaseHelper.TABLE_MENSTRUAL_CYCLES, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(cycle.getId())});
    }

    public void deleteCycle(long id) {
        database.delete(DatabaseHelper.TABLE_MENSTRUAL_CYCLES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<MenstrualCycle> getAllCycles() {
        List<MenstrualCycle> cycles = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_MENSTRUAL_CYCLES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_START_DATE + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    MenstrualCycle cycle = cursorToCycle(cursor);
                    cycles.add(cycle);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return cycles;
    }

    public MenstrualCycle getLatestCycle() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_MENSTRUAL_CYCLES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_START_DATE + " DESC",
                "1");

        if (cursor != null && cursor.moveToFirst()) {
            try {
                MenstrualCycle cycle = cursorToCycle(cursor);
                cursor.close();
                return cycle;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cursor.close();
        }
        return null;
    }

    @SuppressLint("Range")
    private MenstrualCycle cursorToCycle(Cursor cursor) throws ParseException {
        MenstrualCycle cycle = new MenstrualCycle();
        cycle.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        cycle.setStartDate(dateFormat.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_START_DATE))));

        String endDateStr = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_END_DATE));
        if (endDateStr != null) {
            cycle.setEndDate(dateFormat.parse(endDateStr));
        }

        cycle.setSymptoms(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SYMPTOMS)));
        cycle.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
        return cycle;
    }
}

