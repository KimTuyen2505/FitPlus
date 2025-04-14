package com.example.firstproject.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.firstproject.models.HealthSuggestion;

import java.util.ArrayList;
import java.util.List;

public class HealthSuggestionDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public HealthSuggestionDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insert(HealthSuggestion suggestion) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_TITLE, suggestion.getTitle());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CONTENT, suggestion.getContent());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_SOURCE, suggestion.getSource());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_FK, suggestion.getCategoryId());

        return database.insert(DatabaseHelper.TABLE_HEALTH_SUGGESTION, null, values);
    }

    public int update(HealthSuggestion suggestion) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_TITLE, suggestion.getTitle());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CONTENT, suggestion.getContent());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_SOURCE, suggestion.getSource());
        values.put(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_FK, suggestion.getCategoryId());

        return database.update(DatabaseHelper.TABLE_HEALTH_SUGGESTION, values,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_ID + " = ?",
                new String[]{String.valueOf(suggestion.getId())});
    }

    public void delete(int id) {
        database.delete(DatabaseHelper.TABLE_HEALTH_SUGGESTION,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public HealthSuggestion getById(int id) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_HEALTH_SUGGESTION,
                null,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            HealthSuggestion suggestion = cursorToSuggestion(cursor);
            cursor.close();
            return suggestion;
        }
        return null;
    }

    public List<HealthSuggestion> getAll() {
        List<HealthSuggestion> suggestions = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_HEALTH_SUGGESTION,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_TITLE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                suggestions.add(cursorToSuggestion(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Nếu danh sách trống, khởi tạo dữ liệu mẫu
        if (suggestions.isEmpty()) {
            initializeSampleData();
            return getAll(); // Gọi lại để lấy dữ liệu mẫu
        }

        return suggestions;
    }

    public List<HealthSuggestion> getByCategoryId(int categoryId) {
        List<HealthSuggestion> suggestions = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_HEALTH_SUGGESTION,
                null,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_FK + " = ?",
                new String[]{String.valueOf(categoryId)},
                null, null,
                DatabaseHelper.COLUMN_HEALTH_SUGGESTION_TITLE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                suggestions.add(cursorToSuggestion(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return suggestions;
    }

    private HealthSuggestion cursorToSuggestion(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_ID);
        int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_TITLE);
        int contentIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CONTENT);
        int sourceIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_SOURCE);
        int categoryIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_HEALTH_SUGGESTION_CATEGORY_FK);

        HealthSuggestion suggestion = new HealthSuggestion();
        suggestion.setId(cursor.getInt(idIndex));
        suggestion.setTitle(cursor.getString(titleIndex));
        suggestion.setContent(cursor.getString(contentIndex));
        suggestion.setSource(cursor.getString(sourceIndex));
        suggestion.setCategoryId(cursor.getInt(categoryIdIndex));

        return suggestion;
    }

    // Khởi tạo dữ liệu mẫu cho các lời khuyên
    public void initializeSampleData() {
        // Xóa dữ liệu cũ nếu có
        database.delete(DatabaseHelper.TABLE_HEALTH_SUGGESTION, null, null);

        // Thêm các lời khuyên mẫu cho tim mạch (categoryId = 1)
        insert(new HealthSuggestion(1, "Giảm lượng muối",
                "Hạn chế lượng muối trong chế độ ăn uống giúp kiểm soát huyết áp và giảm nguy cơ bệnh tim mạch. Nên ăn dưới 5g muối mỗi ngày.",
                "Hiệp hội Tim mạch Việt Nam", 1));
        insert(new HealthSuggestion(2, "Tập thể dục đều đặn",
                "Tập thể dục ít nhất 150 phút mỗi tuần giúp tăng cường sức khỏe tim mạch và giảm nguy cơ đột quỵ.",
                "Tổ chức Y tế Thế giới (WHO)", 1));
        insert(new HealthSuggestion(3, "Bỏ thuốc lá",
                "Hút thuốc lá làm tăng nguy cơ mắc bệnh tim mạch lên gấp 2-4 lần. Bỏ thuốc lá giúp giảm nguy cơ này đáng kể sau 1 năm.",
                "Bộ Y tế", 1));

        // Thêm các lời khuyên mẫu cho gan (categoryId = 2)
        insert(new HealthSuggestion(4, "Hạn chế rượu bia",
                "Uống quá nhiều rượu bia là nguyên nhân hàng đầu gây bệnh gan. Nên giới hạn lượng rượu bia tiêu thụ và có những ngày không uống trong tuần.",
                "Bệnh viện Bạch Mai", 2));
        insert(new HealthSuggestion(5, "Duy trì cân nặng hợp lý",
                "Béo phì làm tăng nguy cơ mắc bệnh gan nhiễm mỡ không do rượu. Giảm cân có thể giúp cải thiện tình trạng gan nhiễm mỡ.",
                "Hiệp hội Gan mật Việt Nam", 2));

        // Thêm các lời khuyên mẫu cho thận (categoryId = 3)
        insert(new HealthSuggestion(6, "Uống đủ nước",
                "Uống đủ nước (khoảng 1.5-2 lít mỗi ngày) giúp thận hoạt động tốt và ngăn ngừa sỏi thận.",
                "Hội Thận học Việt Nam", 3));
        insert(new HealthSuggestion(7, "Kiểm soát đường huyết",
                "Đái tháo đường là nguyên nhân hàng đầu gây bệnh thận mạn. Kiểm soát đường huyết tốt giúp bảo vệ thận.",
                "Bệnh viện Nội tiết Trung ương", 3));

        // Thêm các lời khuyên mẫu cho tiểu đường (categoryId = 4)
        insert(new HealthSuggestion(8, "Ăn nhiều rau xanh",
                "Rau xanh giàu chất xơ, ít carbohydrate, giúp kiểm soát đường huyết hiệu quả. Nên ăn ít nhất 3 khẩu phần rau mỗi ngày.",
                "Hiệp hội Đái tháo đường Việt Nam", 4));
        insert(new HealthSuggestion(9, "Kiểm tra đường huyết thường xuyên",
                "Theo dõi đường huyết thường xuyên giúp điều chỉnh chế độ ăn uống và thuốc men kịp thời, tránh biến chứng.",
                "Bệnh viện Nội tiết Trung ương", 4));

        // Thêm các lời khuyên mẫu cho dinh dưỡng (categoryId = 5)
        insert(new HealthSuggestion(10, "Ăn đa dạng thực phẩm",
                "Chế độ ăn đa dạng giúp cung cấp đầy đủ các chất dinh dưỡng cần thiết cho cơ thể. Nên kết hợp nhiều loại thực phẩm khác nhau.",
                "Viện Dinh dưỡng Quốc gia", 5));
        insert(new HealthSuggestion(11, "Hạn chế đường tinh luyện",
                "Đường tinh luyện làm tăng nguy cơ béo phì, tiểu đường và bệnh tim mạch. Nên hạn chế bánh kẹo, nước ngọt và thực phẩm chế biến sẵn.",
                "Tổ chức Y tế Thế giới (WHO)", 5));

        // Thêm các lời khuyên mẫu cho tập thể dục (categoryId = 6)
        insert(new HealthSuggestion(12, "Kết hợp nhiều loại hình tập luyện",
                "Kết hợp tập luyện sức bền (đi bộ, chạy bộ) và tập luyện sức mạnh (tập tạ) giúp tăng cường sức khỏe toàn diện.",
                "Hiệp hội Y học Thể thao Việt Nam", 6));
        insert(new HealthSuggestion(13, "Khởi động và giãn cơ",
                "Khởi động trước khi tập và giãn cơ sau khi tập giúp giảm nguy cơ chấn thương và đau nhức cơ.",
                "Trung tâm Y học Thể thao", 6));
    }
}
