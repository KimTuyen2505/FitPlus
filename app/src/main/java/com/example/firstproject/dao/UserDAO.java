package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.User;

public class UserDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public UserDAO(Context context) {
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

    // Thêm người dùng mới
    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_USER_AGE, user.getAge());
        values.put(DatabaseHelper.COLUMN_USER_GENDER, user.getGender());
        values.put(DatabaseHelper.COLUMN_USER_HEIGHT, user.getHeight());
        values.put(DatabaseHelper.COLUMN_USER_WEIGHT, user.getWeight());
        values.put(DatabaseHelper.COLUMN_USER_BLOOD_TYPE, user.getBloodType());

        return database.insert(DatabaseHelper.TABLE_USER, null, values);
    }

    // Cập nhật thông tin người dùng
    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USER_NAME, user.getName());
        values.put(DatabaseHelper.COLUMN_USER_AGE, user.getAge());
        values.put(DatabaseHelper.COLUMN_USER_GENDER, user.getGender());
        values.put(DatabaseHelper.COLUMN_USER_HEIGHT, user.getHeight());
        values.put(DatabaseHelper.COLUMN_USER_WEIGHT, user.getWeight());
        values.put(DatabaseHelper.COLUMN_USER_BLOOD_TYPE, user.getBloodType());

        return database.update(
                DatabaseHelper.TABLE_USER,
                values,
                DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())}
        );
    }

    // Lấy thông tin người dùng
    public User getUser(long userId) {
        User user = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USER,
                null,
                DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    // Lấy người dùng đầu tiên (cho ứng dụng đơn người dùng)
    public User getFirstUser() {
        User user = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_USER,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_USER_ID + " ASC",
                "1"
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }

        return user;
    }

    public User getCurrentUser() {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USER,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_USER_ID + " DESC", "1");

        if (cursor != null && cursor.moveToFirst()) {
            User user = cursorToUser(cursor);
            cursor.close();
            return user;
        }
        return null;
    }

    // Chuyển đổi Cursor thành đối tượng User
    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)));
        user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_AGE)));
        user.setGender(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_GENDER)));
        user.setHeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_HEIGHT)));
        user.setWeight(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_WEIGHT)));

        // Kiểm tra xem cột có tồn tại không trước khi truy cập
        int bloodTypeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_BLOOD_TYPE);
        if (bloodTypeIndex != -1) {
            user.setBloodType(cursor.getString(bloodTypeIndex));
        }

        return user;
    }
}

