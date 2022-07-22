package com.example.licentarotaruteodorgabriel.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class SnoozeTaskNotificationReceiver extends BroadcastReceiver {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("TASK_NOTIFICATION_ID_FROM_TASK_RECEIVER_TO_SNOOZE_ACTIVITY", 3);
        String message = intent.getStringExtra("TASK_MESSAGE_FROM_TASK_RECEIVER_TO_SNOOZE_ACTIVITY");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        NotificationsUtils.setUpTaskNotification(calendar,
                "Snoozed task reminder",
                message,
                id,
                context);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(id);
    }
}
