package com.example.licentarotaruteodorgabriel.tasks_statistics.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Task;

public class Calculations {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static double getTaskDeviationScore(Task task) {
        if(task.getDueDate() >= task.getCompletionDate()){
            return 1;
        }
        double maxDeviation = (10.5 - task.getPriority()) * 86400;
        double deviation = task.getDueDate() - task.getCompletionDate();

        return Math.abs(deviation) > maxDeviation ? 0 : deviation / maxDeviation;
    }
}

