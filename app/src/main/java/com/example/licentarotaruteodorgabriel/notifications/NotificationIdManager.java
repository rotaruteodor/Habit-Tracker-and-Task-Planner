package com.example.licentarotaruteodorgabriel.notifications;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class NotificationIdManager {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static int getId(ArrayList<Task> tasks) {
//        if (tasks.size() == 0) {
//            return 2;
//        }
//        tasks.sort(Comparator.comparing(Task::getNotificationId));
//        if(tasks.get(0).getNotificationId() > 2){
//            return 2;
//        }
//        if (tasks.size() == 1) {
//            return 3;
//        }
//
//        for (int i = 0; i < tasks.size() - 1; i++) {
//            if (tasks.get(i).getNotificationId() != tasks.get(i + 1).getNotificationId() - 1) {
//                return tasks.get(i).getNotificationId() + 1;
//            }
//        }
//
//        return tasks.get(tasks.size() - 1).getNotificationId() + 1;

        if (tasks.size() == 0) {
            return 2;
        }
        int[] notificationsIds = tasks.stream()
                .map(Task::getNotificationId)
                .mapToInt(Integer::intValue)
                .toArray();
        Arrays.sort(notificationsIds);
        if (notificationsIds[0] > 2) {
            return 2;
        }
        if (notificationsIds.length == 1) {
            return 3;
        }

        for (int i = 0; i < notificationsIds.length - 1; i++) {
            if (notificationsIds[i] != notificationsIds[i + 1] - 1) {
                return notificationsIds[i] + 1;
            }
        }
        return notificationsIds[notificationsIds.length - 1] + 1;
    }
}
