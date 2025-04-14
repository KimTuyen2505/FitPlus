package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import com.example.firstproject.models.HealthSuggestionCategory;

import java.util.ArrayList;
import java.util.List;

public class HealthSuggestionCategoryDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public HealthSuggestionCategoryDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insert(HealthSuggestionCategory category) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_ICON, category.getIconName());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_DESCRIPTION, category.getDescription());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_COLOR, category.getColor());

        return database.insert(DatabaseHelper.TABLE_HEALTH_SUGGESTION_CATEGORY, null, values);
    }

    public int update(HealthSuggestionCategory category) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_NAME, category.getName());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_ICON, category.getIconName());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_DESCRIPTION, category.getDescription());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_COLOR, category.getColor());

        return database.update(DatabaseHelper.TABLE_HEALTH_SUGGESTION_CATEGORY, values,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(category.getId())});
    }

    public void delete(int id) {
        database.delete(DatabaseHelper.TABLE_HEALTH_SUGGESTION_CATEGORY,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public HealthSuggestionCategory getById(int id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_HEALTH_SUGGESTION_CATEGORY,
                null,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            HealthSuggestionCategory category = cursorToCategory(cursor);
            cursor.close();
            return category;
        }
        return null;
    }

    public List<HealthSuggestionCategory> getAll() {
        List<HealthSuggestionCategory> categories = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_HEALTH_SUGGESTION_CATEGORY,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                categories.add(cursorToCategory(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Nếu danh sách trống, khởi tạo dữ liệu mẫu
        if (categories.isEmpty()) {
            initializeSampleData();
            return getAll(); // Gọi lại để lấy dữ liệu mẫu
        }

        return categories;
    }

    private HealthSuggestionCategory cursorToCategory(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_NAME);
        int iconIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_ICON);
        int descIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_DESCRIPTION);
        int colorIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_COLOR);

        HealthSuggestionCategory category = new HealthSuggestionCategory();
        category.setId(cursor.getInt(idIndex));
        category.setName(cursor.getString(nameIndex));
        category.setIconName(cursor.getString(iconIndex));
        category.setDescription(cursor.getString(descIndex));
        category.setColor(cursor.getInt(colorIndex));

        return category;
    }

    // Khởi tạo dữ liệu mẫu cho các danh mục
    public void initializeSampleData() {
        // Xóa dữ liệu cũ nếu có
        database.delete(DatabaseHelper.TABLE_HEALTH_SUGGESTION_CATEGORY, null, null);

        // Thêm các danh mục mẫu
        insert(new HealthSuggestionCategory(1, "Tim mạch", "ic_heart", "Lời khuyên về bệnh tim mạch", Color.parseColor("#E53935")));
        insert(new HealthSuggestionCategory(2, "Gan", "ic_liver", "Lời khuyên về bệnh gan", Color.parseColor("#8E24AA")));
        insert(new HealthSuggestionCategory(3, "Thận", "ic_kidney", "Lời khuyên về bệnh thận", Color.parseColor("#43A047")));
        insert(new HealthSuggestionCategory(4, "Tiểu đường", "ic_diabetes", "Lời khuyên về bệnh tiểu đường", Color.parseColor("#FB8C00")));
        insert(new HealthSuggestionCategory(5, "Dinh dưỡng", "ic_nutrition", "Lời khuyên về dinh dưỡng", Color.parseColor("#1E88E5")));
        insert(new HealthSuggestionCategory(6, "Tập thể dục", "ic_exercise", "Lời khuyên về tập thể dục", Color.parseColor("#00ACC1")));
    }
}
