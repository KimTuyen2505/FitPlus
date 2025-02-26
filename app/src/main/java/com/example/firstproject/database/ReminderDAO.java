package com.example.firstproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ReminderDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // Thêm nhắc nhở mới
    public long insertReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, reminder.getTitle());
        values.put(DatabaseHelper.COLUMN_TIME, reminder.getTime());
        values.put(DatabaseHelper.COLUMN_FREQUENCY, reminder.getFrequency());
        values.put(DatabaseHelper.COLUMN_IS_ACTIVE, reminder.isActive() ? 1 : 0);

        return database.insert(DatabaseHelper.TABLE_REMINDERS, null, values);
    }

    // Cập nhật nhắc nhở
    public int updateReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, reminder.getTitle());
        values.put(DatabaseHelper.COLUMN_TIME, reminder.getTime());
        values.put(DatabaseHelper.COLUMN_FREQUENCY, reminder.getFrequency());
        values.put(DatabaseHelper.COLUMN_IS_ACTIVE, reminder.isActive() ? 1 : 0);

        return database.update(
                DatabaseHelper.TABLE_REMINDERS,
                values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(reminder.getId())}
        );
    }

    // Xóa nhắc nhở
    public int deleteReminder(long id) {
        return database.delete(
                DatabaseHelper.TABLE_REMINDERS,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Lấy tất cả nhắc nhở
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_REMINDERS,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_CREATED_AT + " DESC"
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Reminder reminder = cursorToReminder(cursor);
                reminders.add(reminder);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return reminders;
    }

    // Lấy nhắc nhở theo ID
    public Reminder getReminderById(long id) {
        Reminder reminder = null;
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_REMINDERS,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            reminder = cursorToReminder(cursor);
            cursor.close();
        }

        return reminder;
    }

    // Chuyển đổi Cursor thành đối tượng Reminder
    private Reminder cursorToReminder(Cursor cursor) {
        Reminder reminder = new Reminder();
        reminder.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)));
        reminder.setTime(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)));
        reminder.setFrequency(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FREQUENCY)));
        reminder.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_ACTIVE)) == 1);
        return reminder;
    }
}


