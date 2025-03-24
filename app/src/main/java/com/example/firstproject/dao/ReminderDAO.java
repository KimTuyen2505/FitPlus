package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.dao.DatabaseHelper;
import com.example.firstproject.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ReminderDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REMINDER_TITLE, reminder.getTitle());
        values.put(DatabaseHelper.COLUMN_REMINDER_TIME, reminder.getTime());
        values.put(DatabaseHelper.COLUMN_REMINDER_FREQUENCY, reminder.getFrequency());
        values.put(DatabaseHelper.COLUMN_REMINDER_IS_ACTIVE, reminder.isActive() ? 1 : 0); // Lưu trạng thái active
        // Trong ứng dụng thực tế, bạn sẽ lưu user_id từ người dùng đăng nhập
        values.put(DatabaseHelper.COLUMN_REMINDER_USER_ID, 1);

        return database.insert(DatabaseHelper.TABLE_REMINDER, null, values);
    }

    public int updateReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_REMINDER_TITLE, reminder.getTitle());
        values.put(DatabaseHelper.COLUMN_REMINDER_TIME, reminder.getTime());
        values.put(DatabaseHelper.COLUMN_REMINDER_FREQUENCY, reminder.getFrequency());
        values.put(DatabaseHelper.COLUMN_REMINDER_IS_ACTIVE, reminder.isActive() ? 1 : 0); // Cập nhật trạng thái active

        return database.update(DatabaseHelper.TABLE_REMINDER, values,
                DatabaseHelper.COLUMN_REMINDER_ID + " = ?",
                new String[]{String.valueOf(reminder.getId())});
    }

    public void deleteReminder(long id) {
        database.delete(DatabaseHelper.TABLE_REMINDER,
                DatabaseHelper.COLUMN_REMINDER_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public Reminder getReminder(long id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_REMINDER,
                null,
                DatabaseHelper.COLUMN_REMINDER_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Reminder reminder = cursorToReminder(cursor);
            cursor.close();
            return reminder;
        }
        return null;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_REMINDER,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_REMINDER_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Reminder reminder = cursorToReminder(cursor);
                reminders.add(reminder);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return reminders;
    }

    private Reminder cursorToReminder(Cursor cursor) {
        Reminder reminder = new Reminder();
        reminder.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REMINDER_ID)));
        reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REMINDER_TITLE)));
        reminder.setTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REMINDER_TIME)));
        reminder.setFrequency(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REMINDER_FREQUENCY)));
        // Đọc trạng thái active từ cơ sở dữ liệu
        int isActiveInt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_REMINDER_IS_ACTIVE));
        reminder.setActive(isActiveInt == 1);

        return reminder;
    }
}



