package com.example.firstproject.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.firstproject.receivers.ReminderNotificationReceiver;

import java.util.Calendar;

public class Reminder {
    private long id;
    private String title;
    private String time; // Format: HH:mm
    private String frequency;
    private boolean isActive;

    // Các loại tần suất
    public static final String FREQUENCY_DAILY = "Hàng ngày";
    public static final String FREQUENCY_MINUTE = "Mỗi phút";

    public Reminder() {
        this.isActive = true;
    }

    public Reminder(String title, String time, String frequency) {
        this.title = title;
        this.time = time;
        this.frequency = frequency;
        this.isActive = true;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Lên lịch thông báo
    public void scheduleNotification(Context context) {
        if (!isActive) {
            cancelNotification(context);
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderNotificationReceiver.class);
        intent.putExtra("reminder_id", id);
        intent.putExtra("reminder_title", title);
        intent.putExtra("reminder_frequency", frequency);

        // Tạo PendingIntent duy nhất cho mỗi nhắc nhở
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // Tính toán thời gian nhắc nhở
        Calendar calendar = Calendar.getInstance();

        // Nếu thời gian được chỉ định (HH:mm)
        if (time != null && !time.isEmpty() && time.contains(":")) {
            String[] timeParts = time.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Nếu thời gian đã qua trong ngày, đặt cho ngày mai
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                // Nếu tần suất là hàng ngày, đặt cho ngày mai
                if (FREQUENCY_DAILY.equals(frequency)) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            }
        }

        // Đặt lịch dựa trên tần suất
        if (FREQUENCY_DAILY.equals(frequency)) {
            // Hàng ngày vào giờ đã chỉ định
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            }
        } else if (FREQUENCY_MINUTE.equals(frequency)) {
            long time = calendar.getTimeInMillis();
            if (time <= System.currentTimeMillis()) {
                time = System.currentTimeMillis() + (60 * 1000);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        time,
                        pendingIntent
                );
            }
        }
    }

    // Hủy thông báo
    public void cancelNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}

