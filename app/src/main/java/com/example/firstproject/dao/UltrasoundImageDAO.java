package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.UltrasoundImage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UltrasoundImageDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UltrasoundImageDAO(Context context) {
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

    public long insertUltrasoundImage(UltrasoundImage image) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ULTRASOUND_PREGNANCY_ID, image.getPregnancyId());
        values.put(DatabaseHelper.COLUMN_ULTRASOUND_IMAGE_URI, image.getImageUri());
        values.put(DatabaseHelper.COLUMN_ULTRASOUND_WEEK, image.getPregnancyWeek());
        values.put(DatabaseHelper.COLUMN_ULTRASOUND_NOTES, image.getNotes());

        if (image.getImageDate() != null) {
            values.put(DatabaseHelper.COLUMN_ULTRASOUND_DATE, image.getImageDate().getTime());
        } else {
            values.put(DatabaseHelper.COLUMN_ULTRASOUND_DATE, new Date().getTime());
        }

        return database.insert(DatabaseHelper.TABLE_ULTRASOUND_IMAGE, null, values);
    }

    public int updateUltrasoundImage(UltrasoundImage image) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ULTRASOUND_IMAGE_URI, image.getImageUri());
        values.put(DatabaseHelper.COLUMN_ULTRASOUND_WEEK, image.getPregnancyWeek());
        values.put(DatabaseHelper.COLUMN_ULTRASOUND_NOTES, image.getNotes());

        if (image.getImageDate() != null) {
            values.put(DatabaseHelper.COLUMN_ULTRASOUND_DATE, image.getImageDate().getTime());
        }

        return database.update(
                DatabaseHelper.TABLE_ULTRASOUND_IMAGE,
                values,
                DatabaseHelper.COLUMN_ULTRASOUND_ID + " = ?",
                new String[]{String.valueOf(image.getId())}
        );
    }

    public int deleteUltrasoundImage(long id) {
        return database.delete(
                DatabaseHelper.TABLE_ULTRASOUND_IMAGE,
                DatabaseHelper.COLUMN_ULTRASOUND_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public UltrasoundImage getUltrasoundImage(long id) {
        UltrasoundImage image = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ULTRASOUND_IMAGE,
                null,
                DatabaseHelper.COLUMN_ULTRASOUND_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            image = cursorToUltrasoundImage(cursor);
            cursor.close();
        }

        return image;
    }

    public List<UltrasoundImage> getAllUltrasoundImagesByPregnancyId(long pregnancyId) {
        List<UltrasoundImage> images = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_ULTRASOUND_IMAGE,
                null,
                DatabaseHelper.COLUMN_ULTRASOUND_PREGNANCY_ID + " = ?",
                new String[]{String.valueOf(pregnancyId)},
                null,
                null,
                DatabaseHelper.COLUMN_ULTRASOUND_DATE + " DESC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UltrasoundImage image = cursorToUltrasoundImage(cursor);
                images.add(image);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return images;
    }

    private UltrasoundImage cursorToUltrasoundImage(Cursor cursor) {
        UltrasoundImage image = new UltrasoundImage();

        image.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ULTRASOUND_ID)));
        image.setPregnancyId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ULTRASOUND_PREGNANCY_ID)));
        image.setImageUri(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ULTRASOUND_IMAGE_URI)));
        image.setPregnancyWeek(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ULTRASOUND_WEEK)));

        int notesIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ULTRASOUND_NOTES);
        if (notesIndex != -1 && !cursor.isNull(notesIndex)) {
            image.setNotes(cursor.getString(notesIndex));
        }

        int imageDateIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ULTRASOUND_DATE);
        if (imageDateIndex != -1 && !cursor.isNull(imageDateIndex)) {
            long imageDateMillis = cursor.getLong(imageDateIndex);
            image.setImageDate(new Date(imageDateMillis));
        }

        return image;
    }
}
