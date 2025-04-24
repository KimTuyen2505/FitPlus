package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.PregnancyDiary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PregnancyDiaryDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public PregnancyDiaryDAO(Context context) {
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

    public long insertDiaryEntry(PregnancyDiary diary) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_PREGNANCY_ID, diary.getPregnancyId());
        values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_TITLE, diary.getTitle());
        values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_CONTENT, diary.getContent());
        values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_WEEK, diary.getPregnancyWeek());

        if (diary.getEntryDate() != null) {
            values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_ENTRY_DATE, diary.getEntryDate().getTime());
        } else {
            values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_ENTRY_DATE, new Date().getTime());
        }

        return database.insert(DatabaseHelper.TABLE_PREGNANCY_DIARY, null, values);
    }

    public int updateDiaryEntry(PregnancyDiary diary) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_TITLE, diary.getTitle());
        values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_CONTENT, diary.getContent());
        values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_WEEK, diary.getPregnancyWeek());

        if (diary.getEntryDate() != null) {
            values.put(DatabaseHelper.COLUMN_PREGNANCY_DIARY_ENTRY_DATE, diary.getEntryDate().getTime());
        }

        return database.update(
                DatabaseHelper.TABLE_PREGNANCY_DIARY,
                values,
                DatabaseHelper.COLUMN_PREGNANCY_DIARY_ID + " = ?",
                new String[]{String.valueOf(diary.getId())}
        );
    }

    public int deleteDiaryEntry(long id) {
        return database.delete(
                DatabaseHelper.TABLE_PREGNANCY_DIARY,
                DatabaseHelper.COLUMN_PREGNANCY_DIARY_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public PregnancyDiary getDiaryEntry(long id) {
        PregnancyDiary diary = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_PREGNANCY_DIARY,
                null,
                DatabaseHelper.COLUMN_PREGNANCY_DIARY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            diary = cursorToDiary(cursor);
            cursor.close();
        }

        return diary;
    }

    public List<PregnancyDiary> getAllDiaryEntriesByPregnancyId(long pregnancyId) {
        List<PregnancyDiary> diaryEntries = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_PREGNANCY_DIARY,
                null,
                DatabaseHelper.COLUMN_PREGNANCY_DIARY_PREGNANCY_ID + " = ?",
                new String[]{String.valueOf(pregnancyId)},
                null,
                null,
                DatabaseHelper.COLUMN_PREGNANCY_DIARY_ENTRY_DATE + " DESC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PregnancyDiary diary = cursorToDiary(cursor);
                diaryEntries.add(diary);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return diaryEntries;
    }

    private PregnancyDiary cursorToDiary(Cursor cursor) {
        PregnancyDiary diary = new PregnancyDiary();

        diary.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_DIARY_ID)));
        diary.setPregnancyId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_DIARY_PREGNANCY_ID)));
        diary.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_DIARY_TITLE)));
        diary.setContent(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_DIARY_CONTENT)));
        diary.setPregnancyWeek(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREGNANCY_DIARY_WEEK)));

        int entryDateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PREGNANCY_DIARY_ENTRY_DATE);
        if (entryDateIndex != -1 && !cursor.isNull(entryDateIndex)) {
            long entryDateMillis = cursor.getLong(entryDateIndex);
            diary.setEntryDate(new Date(entryDateMillis));
        }

        return diary;
    }
}
