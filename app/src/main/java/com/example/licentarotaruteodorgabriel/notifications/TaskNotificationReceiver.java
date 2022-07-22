package com.example.licentarotaruteodorgabriel.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.activities.MainActivity;

public class TaskNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("NOTIFICATION_MESSAGE");
        String title = intent.getStringExtra("NOTIFICATION_TITLE");
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 3);
        String channelId = "NOTIFICATION_CHANNEL_ID";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mainActivityIntent = new Intent(context, MainActivity.class);

        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context,
                notificationId,
                mainActivityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Intent snoozeTaskNotificationIntent = new Intent(context, SnoozeTaskNotificationReceiver.class);
        snoozeTaskNotificationIntent.putExtra("TASK_NOTIFICATION_ID_FROM_TASK_RECEIVER_TO_SNOOZE_ACTIVITY", notificationId);
        snoozeTaskNotificationIntent.putExtra("TASK_MESSAGE_FROM_TASK_RECEIVER_TO_SNOOZE_ACTIVITY", message);
        snoozeTaskNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent snoozeTaskNotificationPendingIntent = PendingIntent.getBroadcast(context,
                notificationId,
                snoozeTaskNotificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setContentIntent(mainActivityPendingIntent)
                .setSmallIcon(R.drawable.main_background_image)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.ic_trophy_svg, "Snooze 1h", snoozeTaskNotificationPendingIntent)
                .setAutoCancel(true)
                .setOngoing(false);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, notification.build());
    }
}
