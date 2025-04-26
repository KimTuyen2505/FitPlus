package com.example.firstproject.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.firstproject.dao.ReminderDAO;
import com.example.firstproject.models.Reminder;

import java.util.List;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Restart all reminders after device boot
            ReminderDAO reminderDAO = new ReminderDAO(context);
            reminderDAO.open();

            List<Reminder> reminders = reminderDAO.getAllReminders();
            for (Reminder reminder : reminders) {
                if (reminder.isActive()) {
                    reminder.scheduleNotification(context);
                }
            }

            reminderDAO.close();
        }
    }
}
