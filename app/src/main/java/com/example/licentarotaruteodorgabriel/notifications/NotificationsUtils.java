package com.example.licentarotaruteodorgabriel.notifications;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class NotificationsUtils {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setUpDailyNotification(Calendar calendarDate,
                                              int pendingIntentRequestCode,
                                              Context context) {

         if (Calendar.getInstance().after(calendarDate)) {
            calendarDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        intent.putExtra("NOTIFICATION_ID", pendingIntentRequestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                pendingIntentRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendarDate.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                calendarDate.getTimeInMillis(),
                pendingIntent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setUpTaskNotification(Calendar calendarDate,
                                             String title,
                                             String message,
                                             int pendingIntentRequestCode,
                                             Context context) {

        Calendar calendar = Calendar.getInstance();
        if (!calendar.after(calendarDate)) {
            Intent intent = new Intent(context, TaskNotificationReceiver.class);
            intent.putExtra("NOTIFICATION_TITLE", title);
            intent.putExtra("NOTIFICATION_MESSAGE", message);
            intent.putExtra("NOTIFICATION_ID", pendingIntentRequestCode);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    pendingIntentRequestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendarDate.getTimeInMillis(),
                    pendingIntent);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendarDate.getTimeInMillis(),
                    pendingIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void removeNotification(int pendingIntentRequestCode, Context context) {
        Intent intent = new Intent(context, TaskNotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                pendingIntentRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void setUpNotificationChannel(Context context) {
        String channelName = "Notification Channel";
        String description = "Notification Channel Description";
        String channelId = "NOTIFICATION_CHANNEL_ID";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel notificationChannel = new NotificationChannel(channelId,
                channelName,
                importance);
        notificationChannel.setDescription(description);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);
    }
}
