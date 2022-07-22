package com.example.licentarotaruteodorgabriel.tasks_statistics;

import com.example.licentarotaruteodorgabriel.classes.TasksStats;

public class TasksScore {

    public static double getTasksScore(TasksStats tasksStats, int nrOfPendingTasks) {
        int allTasks = tasksStats.getNrOfAllTasks();
        int allTasksCompleted = tasksStats.getNrOfAllTasksCompleted();
        int allTasksCompletedBeforeDue = tasksStats.getNrOfAllTasksCompletedBeforeDue();
        double totalDeviation = tasksStats.getTotalDeviation();

        if (allTasks == 0 || allTasksCompleted == 0) {
            return 0;
        }
        double completionRate = (double) allTasksCompleted / (allTasks - nrOfPendingTasks);
        double completionBeforeDueRate = (double) allTasksCompletedBeforeDue / allTasksCompleted;
        double deviationScore = totalDeviation / allTasksCompleted;

        return completionRate * 0.6 + completionBeforeDueRate * 0.2 + deviationScore * 0.2;
    }
}
