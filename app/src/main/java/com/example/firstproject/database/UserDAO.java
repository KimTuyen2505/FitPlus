package com.example.firstproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    // Thêm người dùng mới
    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_AGE, user.getAge());
        values.put(DatabaseHelper.COLUMN_GENDER, user.getGender());
        values.put(DatabaseHelper.COLUMN_HEIGHT, user.getHeight());
        values.put(DatabaseHelper.COLUMN_WEIGHT, user.getWeight());

        return database.insert(DatabaseHelper.TABLE_USERS, null, values);
    }

    // Cập nhật thông tin người dùng
    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_AGE, user.getAge());
        values.put(DatabaseHelper.COLUMN_GENDER, user.getGender());
        values.put(DatabaseHelper.COLUMN_HEIGHT, user.getHeight());
        values.put(DatabaseHelper.COLUMN_WEIGHT, user.getWeight());

        return database.update(
                DatabaseHelper.TABLE_USERS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );
    }

    // Lấy thông tin người dùng
    public User getUser() {
        User user = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USERS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_ID + " DESC",
                "1"
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    // Chuyển đổi Cursor thành đối tượng User
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
        user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AGE)));
        user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GENDER)));
        user.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT)));
        user.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT)));
        return user;
    }
}
