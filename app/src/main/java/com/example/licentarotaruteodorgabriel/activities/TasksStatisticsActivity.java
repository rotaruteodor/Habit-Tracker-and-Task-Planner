package com.example.licentarotaruteodorgabriel.activities;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Task;
import com.example.licentarotaruteodorgabriel.classes.TasksStats;
import com.example.licentarotaruteodorgabriel.habit_charts.ScoreChartsManager;
import com.example.licentarotaruteodorgabriel.tasks_charts.TasksChartsManager;
import com.example.licentarotaruteodorgabriel.tasks_statistics.TasksScore;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;

public class TasksStatisticsActivity extends AppCompatActivity {

    private TasksStats tasksStats;
    private int nrOfPendingTasks;
    private double averagePriority;
    private ArrayList<Task> tasks;

    private TextView tvStatisticsTotalTasksInHistory;
    private TextView tvPendingTasks;
    private TextView tvAveragePriority;
    private TextView tvTotalCompleted;
    private TextView tvTasksCompletedBeforeDueDate;
    private TextView tvTasksCompletedAfterDueDate;
    private PieChart pieChartTasksScore;
    private BarChart barChartTaskCategories;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_statistics);

        initializeComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeComponents() {
        tasksStats = getIntent().getParcelableExtra("TASK_STATS_FROM_TASKS_FRAGMENT_TO_TASKS_STATISTICS_ACTIVITY");
        tasks = getIntent().getParcelableArrayListExtra("TASKS_FROM_TASKS_FRAGMENT_TO_TASKS_STATISTICS_ACTIVITY");
        nrOfPendingTasks = (int) tasks.stream().filter(t -> !t.getIsDone()).count();
        if (nrOfPendingTasks > 0) {
            averagePriority = tasks.stream()
                    .filter(t -> !t.getIsDone())
                    .mapToInt(Task::getPriority)
                    .average().getAsDouble();
        } else {
            averagePriority = 0;
        }

        tvStatisticsTotalTasksInHistory = findViewById(R.id.tvStatisticsTotalTasksInHistory);
        tvPendingTasks = findViewById(R.id.tvPendingTasks);
        tvAveragePriority = findViewById(R.id.tvAveragePriority);
        tvTotalCompleted = findViewById(R.id.tvTotalCompleted);
        tvTasksCompletedBeforeDueDate = findViewById(R.id.tvTasksCompletedBeforeDueDate);
        tvTasksCompletedAfterDueDate = findViewById(R.id.tvTasksCompletedAfterDueDate);
        pieChartTasksScore = findViewById(R.id.pieChartTasksScore);
        barChartTaskCategories = findViewById(R.id.barChartTaskCategories);

        configureComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureComponents() {
        tvStatisticsTotalTasksInHistory.setText(getSpannableStringForNumericalStats(0));
        tvPendingTasks.setText(getSpannableStringForNumericalStats(1));
        tvAveragePriority.setText(getSpannableStringForNumericalStats(2));
        tvTotalCompleted.setText(getSpannableStringForNumericalStats(3));
        tvTasksCompletedBeforeDueDate.setText(getSpannableStringForNumericalStats(4));
        tvTasksCompletedAfterDueDate.setText(getSpannableStringForNumericalStats(5));

        double tasksScore = TasksScore.getTasksScore(tasksStats, nrOfPendingTasks);
        ScoreChartsManager.configureScorePieChart(pieChartTasksScore,
                tasksScore * 100,
                TasksStatisticsActivity.this);

        TasksChartsManager.configureHorizontalBarChart(tasks,
                barChartTaskCategories,
                TasksStatisticsActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    private SpannableString getSpannableStringForNumericalStats(int type) {
        String title;
        String value;

        switch (type) {
            case 0:
                title = "Total tasks in history\n";
                value = String.valueOf(tasksStats.getNrOfAllTasks());
                break;
            case 1:
                title = "Pending tasks\n";
                value = String.valueOf(nrOfPendingTasks);
                break;
            case 2:
                title = "Average priority\n";
                value = String.format("%.1f", averagePriority);
                break;
            case 3:
                title = "Completed\n";
                value = String.valueOf(tasksStats.getNrOfAllTasksCompleted());
                break;
            case 4:
                title = "Before due date\n";
                value = String.valueOf(tasksStats.getNrOfAllTasksCompletedBeforeDue());
                break;
            case 5:
                title = "After due date\n";
                value = String.valueOf(tasksStats.getNrOfAllTasksCompletedAfterDue());
                break;
            default:
                title = "Woops";
                value = "Error";
                break;
        }
        String fullText = title.concat(value);
        SpannableString inputsSum = new SpannableString(title.concat(value));
        inputsSum.setSpan(new RelativeSizeSpan(1.5f),
                title.length(),
                fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return inputsSum;
    }
}