package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.PregnancyData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PregnancyDataDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public PregnancyDataDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (database != null && database.isOpen()) {
            dbHelper.close();
            database = null;
        }
    }

    public long insertPregnancyData(PregnancyData pregnancyData) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PREGNANCY_USER_ID, pregnancyData.getUserId());

        if (pregnancyData.getStartDate() != null) {
            values.put(DatabaseHelper.COLUMN_PREGNANCY_START_DATE, pregnancyData.getStartDate().getTime());
        }

        if (pregnancyData.getDueDate() != null) {
            values.put(DatabaseHelper.COLUMN_PREGNANCY_DUE_DATE, pregnancyData.getDueDate().getTime());
        }

        values.put(DatabaseHelper.COLUMN_PREGNANCY_BABY_NAME, pregnancyData.getBabyName());
        values.put(DatabaseHelper.COLUMN_PREGNANCY_IS_CONFIRMED, pregnancyData.isConfirmed() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_PREGNANCY_LAST_UPDATED, new Date().getTime());

        return database.insert(DatabaseHelper.TABLE_PREGNANCY_DATA, null, values);
    }

    public int updatePregnancyData(PregnancyData pregnancyData) {
        ContentValues values = new ContentValues();

        if (pregnancyData.getStartDate() != null) {
            values.put(DatabaseHelper.COLUMN_PREGNANCY_START_DATE, pregnancyData.getStartDate().getTime());
        }

        if (pregnancyData.getDueDate() != null) {
            values.put(DatabaseHelper.COLUMN_PREGNANCY_DUE_DATE, pregnancyData.getDueDate().getTime());
        }

        values.put(DatabaseHelper.COLUMN_PREGNANCY_BABY_NAME, pregnancyData.getBabyName());
        values.put(DatabaseHelper.COLUMN_PREGNANCY_IS_CONFIRMED, pregnancyData.isConfirmed() ? 1 : 0);
        values.put(DatabaseHelper.COLUMN_PREGNANCY_LAST_UPDATED, new Date().getTime());

        return database.update(
                DatabaseHelper.TABLE_PREGNANCY_DATA,
                values,
                DatabaseHelper.COLUMN_PREGNANCY_ID + " = ?",
                new String[]{String.valueOf(pregnancyData.getId())}
        );
    }

    public int deletePregnancyData(long id) {
        return database.delete(
                DatabaseHelper.TABLE_PREGNANCY_DATA,
                DatabaseHelper.COLUMN_PREGNANCY_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public PregnancyData getPregnancyData(long id) {
        PregnancyData pregnancyData = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_PREGNANCY_DATA,
                null,
                DatabaseHelper.COLUMN_PREGNANCY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            pregnancyData = cursorToPregnancyData(cursor);
            cursor.close();
        }

        return pregnancyData;
    }

    public PregnancyData getLatestPregnancyData() {
        PregnancyData pregnancyData = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_PREGNANCY_DATA,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_PREGNANCY_LAST_UPDATED + " DESC",
                "1"
        );

        if (cursor != null && cursor.moveToFirst()) {
            pregnancyData = cursorToPregnancyData(cursor);
            cursor.close();
        }

        return pregnancyData;
    }

    public List<PregnancyData> getAllPregnancyData() {
        List<PregnancyData> pregnancyDataList = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_PREGNANCY_DATA,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_PREGNANCY_LAST_UPDATED + " DESC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PregnancyData pregnancyData = cursorToPregnancyData(cursor);
                pregnancyDataList.add(pregnancyData);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return pregnancyDataList;
    }

    private PregnancyData cursorToPregnancyData(Cursor cursor) {
        PregnancyData pregnancyData = new PregnancyData();

        pregnancyData.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_ID)));
        pregnancyData.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_USER_ID)));

        int startDateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PREGNANCY_START_DATE);
        if (startDateIndex != -1 && !cursor.isNull(startDateIndex)) {
            long startDateMillis = cursor.getLong(startDateIndex);
            pregnancyData.setStartDate(new Date(startDateMillis));
        }

        int dueDateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PREGNANCY_DUE_DATE);
        if (dueDateIndex != -1 && !cursor.isNull(dueDateIndex)) {
            long dueDateMillis = cursor.getLong(dueDateIndex);
            pregnancyData.setDueDate(new Date(dueDateMillis));
        }

        pregnancyData.setBabyName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_BABY_NAME)));
        pregnancyData.setConfirmed(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_IS_CONFIRMED)) == 1);

        int lastUpdatedIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PREGNANCY_LAST_UPDATED);
        if (lastUpdatedIndex != -1 && !cursor.isNull(lastUpdatedIndex)) {
            long lastUpdatedMillis = cursor.getLong(lastUpdatedIndex);
            pregnancyData.setLastUpdated(new Date(lastUpdatedMillis));
        }

        return pregnancyData;
    }
}
