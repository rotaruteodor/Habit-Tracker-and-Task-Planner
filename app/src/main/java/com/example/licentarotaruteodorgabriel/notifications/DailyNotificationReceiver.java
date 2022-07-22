package com.example.licentarotaruteodorgabriel.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.licentarotaruteodorgabriel.interfaces.FirebaseDataCallback;
import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.activities.MainActivity;
import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.classes.Task;
import com.example.licentarotaruteodorgabriel.classes.User;
import com.example.licentarotaruteodorgabriel.fragments.fragments_utils.HabitsFilter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

public class DailyNotificationReceiver extends BroadcastReceiver {

    private ArrayList<Task> tasks;
    private ArrayList<Habit> habits;
    private User loggedInUser;

    private int nrOfUnarchivedHabits;
    private int nrOfOngoingHabits;
    private int nrOfPendingTasks;
    private int nrOfTasks;
    private DatabaseReference loggedInUserDbPath;

    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFirebaseUser != null && currentFirebaseUser.isEmailVerified()) {
            loggedInUserDbPath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            readAllData(new FirebaseDataCallback() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete() {
                    configureNotification(context, intent);
                }
            });
        }
    }

    private void readAllData(FirebaseDataCallback callback) {
        loggedInUserDbPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loggedInUser = snapshot.getValue(User.class);
                readHabitsData(callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void readHabitsData(FirebaseDataCallback callback) {
        loggedInUserDbPath.child("habits")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        habits = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Habit habit = dataSnapshot.getValue(Habit.class);
                            if (habit.getRealizations() == null) {
                                habit.setRealizations(new HashMap<>());
                            }
                            habits.add(habit);
                        }

                        readTasksData(callback);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    public void readTasksData(FirebaseDataCallback callback) {
        loggedInUserDbPath.child("tasks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        tasks = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Task task = dataSnapshot.getValue(Task.class);
                            tasks.add(task);
                        }
                        callback.onComplete();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureNotification(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 1);
        String channelId = "NOTIFICATION_CHANNEL_ID";
        nrOfUnarchivedHabits = (int) habits.stream().filter(h -> !h.getArchived()).count();
        nrOfOngoingHabits = HabitsFilter.getOngoingHabits(habits, LocalDate.now()).size();
        Long tomorrowAtWakeupHour;
        if (notificationId == 0) { // wakeup
            tomorrowAtWakeupHour = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .plusDays(1).toEpochSecond();
        } else { // sleep
            tomorrowAtWakeupHour = LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .plusHours(getHoursFromSleepToWakeupHour())
                    .toEpochSecond();
        }
        nrOfPendingTasks = (int) tasks.stream()
                .filter(t -> !t.getIsDone())
                .filter(d -> d.getDueDate() <= tomorrowAtWakeupHour)
                .count();

        nrOfTasks = tasks.size();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context,
                notificationId,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String title = getNotificationTitle(notificationId);
        String message = getNotificationMessage(notificationId);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_icon);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setContentIntent(mainActivityPendingIntent)
                .setSmallIcon(R.drawable.main_background_image)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, notification.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long getHoursFromSleepToWakeupHour() {
        long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        long todayDateAtWakeupHour = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .withHour(loggedInUser.getWakeupHour()).toEpochSecond();

        int hoursDiff = loggedInUser.getSleepHour() - loggedInUser.getWakeupHour();
        if (now > todayDateAtWakeupHour) {
            return 24 - Math.abs(hoursDiff);
        } else {
            return Math.abs(hoursDiff) - 1;
        }
    }

    private String getNotificationTitle(int notificationId) {
        if (notificationId == 0) { // wakeup
            return "Today will be a good day!";
        } else { // sleep
            return "Another happy day we hope!";
        }
    }

    @NonNull
    private String getNotificationMessage(int notificationId) {
        if (notificationId == 0) {// wakeup
            if (nrOfUnarchivedHabits == 0 && nrOfTasks == 0) {
                return "This is the day! Start your journey to become more productive";
            } else {
                if (nrOfOngoingHabits == 0 && nrOfPendingTasks == 0) {
                    return "Yay! You don't have anything scheduled for today";
                } else {
                    StringBuilder messageBuilder = new StringBuilder().append("You have ")
                            .append(nrOfOngoingHabits).append(" habit");
                    if (nrOfOngoingHabits != 1) {
                        messageBuilder.append("s");
                    }
                    messageBuilder.append(" and ").append(nrOfPendingTasks).append(" task");
                    if (nrOfPendingTasks != 1) {
                        messageBuilder.append("s");
                    }
                    messageBuilder.append(" scheduled for today");

                    return messageBuilder.toString();
                }
            }
        } else { // night
            if (nrOfUnarchivedHabits == 0 && nrOfTasks == 0) {
                return "Don't give up! Tomorrow you can try adding a new habit or task";
            } else {
                if (nrOfOngoingHabits == 0 && nrOfPendingTasks == 0) {
                    return "Congratulations! You completed everything for today.";
                } else {
                    StringBuilder messageBuilder = new StringBuilder().append("You still have ")
                            .append(nrOfOngoingHabits).append(" habit");
                    if (nrOfOngoingHabits != 1) {
                        messageBuilder.append("s");
                    }
                    messageBuilder.append(" and ").append(nrOfPendingTasks).append(" task");
                    if (nrOfPendingTasks != 1) {
                        messageBuilder.append("s");
                    }
                    messageBuilder.append(" due tomorrow.");

                    return messageBuilder.toString();
                }
            }
        }
    }
}
