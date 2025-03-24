package com.example.firstproject.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.firstproject.MainActivity;
import com.example.firstproject.R;
import com.example.firstproject.dao.ReminderDAO;
import com.example.firstproject.models.Reminder;

public class ReminderNotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "reminder_notification_channel";
    private static final String CHANNEL_NAME = "Nhắc nhở";
    private static final String CHANNEL_DESCRIPTION = "Thông báo nhắc nhở từ ứng dụng Quản lý sức khỏe";

    @Override
    public void onReceive(Context context, Intent intent) {
        long reminderId = intent.getLongExtra("reminder_id", -1);
        String title = intent.getStringExtra("reminder_title");

        if (reminderId == -1 || title == null) {
            return;
        }

        // Tạo thông báo
        createNotification(context, reminderId, title);

        // Lên lịch lại thông báo tiếp theo nếu cần
        rescheduleNotification(context, reminderId);
    }

    private void createNotification(Context context, long reminderId, String title) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo channel cho Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

        // Intent khi nhấn vào thông báo
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("open_reminders", true);
        intent.putExtra("reminder_id", reminderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) reminderId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        // Xây dựng thông báo
        String content = "Đã đến giờ";
        int icon = R.drawable.ic_reminders;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Hiển thị thông báo
        notificationManager.notify((int) reminderId, builder.build());
    }

    private void rescheduleNotification(Context context, long reminderId) {
        ReminderDAO reminderDAO = new ReminderDAO(context);
        reminderDAO.open();

        Reminder reminder = reminderDAO.getReminder(reminderId);
        if (reminder != null && reminder.isActive()) {
            reminder.scheduleNotification(context);
        }

        reminderDAO.close();
    }
}

