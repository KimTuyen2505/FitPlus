package com.example.firstproject.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.dao.DatabaseHelper;
import com.example.firstproject.models.User;

public class UserDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_AGE, user.getAge());
        values.put(DatabaseHelper.COLUMN_GENDER, user.getGender());
        values.put(DatabaseHelper.COLUMN_HEIGHT, user.getHeight());
        values.put(DatabaseHelper.COLUMN_WEIGHT, user.getWeight());

        return database.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_AGE, user.getAge());
        values.put(DatabaseHelper.COLUMN_GENDER, user.getGender());
        values.put(DatabaseHelper.COLUMN_HEIGHT, user.getHeight());
        values.put(DatabaseHelper.COLUMN_WEIGHT, user.getWeight());

        return database.update(DatabaseHelper.TABLE_USERS, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    public void deleteUser(long id) {
        database.delete(DatabaseHelper.TABLE_USERS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public User getUser(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    public User getCurrentUser() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_ID + " DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    @SuppressLint("Range")
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)));
        user.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
        user.setAge(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE)));
        user.setGender(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENDER)));
        user.setHeight(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_HEIGHT)));
        user.setWeight(cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT)));
        user.setCreatedAt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT)));
        return user;
    }
}

