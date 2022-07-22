package com.example.licentarotaruteodorgabriel.tasks_charts;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Task;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RemoveDecimalsFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TasksChartsManager {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configureHorizontalBarChart(ArrayList<Task> tasks,
                                                   BarChart barChart,
                                                   Context context) {

        HashMap<String, Integer> taskCategoriesFrequencies = new HashMap<>();
        tasks.stream()
                .filter(t -> t.getCategory() != null)
                .forEach(f -> incrementCategory(f.getCategory(), taskCategoriesFrequencies));

        if (taskCategoriesFrequencies.size() > 0) {
            ArrayList<String> allColors = new ArrayList<>(
                    Arrays.asList(context.getResources().getStringArray(R.array.tasks_bar_chart_colors)));
            int colorIndex = 0;
            ArrayList<String> labels = new ArrayList<>();
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<Integer> barChartColors = new ArrayList<>();

            int barEntryIndex = 0;
            for (Map.Entry<String, Integer> taskCategoryFrequency : taskCategoriesFrequencies.entrySet()) {
                entries.add(new BarEntry(barEntryIndex, taskCategoryFrequency.getValue()));
                labels.add(taskCategoryFrequency.getKey());
                if (colorIndex == allColors.size()) {
                    colorIndex = 0;
                }
                barChartColors.add(Color.parseColor(allColors.get(colorIndex)));
                ++colorIndex;
                ++barEntryIndex;
            }

            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(barChartColors);
            barDataSet.setValueTextSize(12);
            BarData data = new BarData(barDataSet);
            data.setValueFormatter(new RemoveDecimalsFormatter());
            data.setValueTextColor(Color.WHITE);
            data.setValueTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
            barChart.getDescription().setEnabled(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.getAxisLeft().setEnabled(false);
            barChart.getLegend().setEnabled(false);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.setDrawValueAboveBar(false);
            barChart.getXAxis().setGranularity(1f);
            barChart.setScaleEnabled(false);
            barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            barChart.getXAxis().setTextColor(Color.WHITE);
            barChart.getXAxis().setTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
            barChart.animateY(400);
            barChart.animateX(400);

            barChart.setData(data);
            barChart.invalidate();
            barChart.setVisibleXRangeMaximum(5);
            barChart.getAxisLeft().setAxisMinimum(0);
            barChart.getAxisLeft().setAxisMaximum(barChart.getYMax());
        } else {
            barChart.setNoDataText("No tasks with categories!");
            barChart.setNoDataTextColor(Color.WHITE);
            barChart.setNoDataTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
        }
    }

    private static void incrementCategory(String category, HashMap<String, Integer> taskCategoriesFrequency) {
        Integer value = taskCategoriesFrequency.get(category);
        taskCategoriesFrequency.put(category, value == null ? 1 : ++value);
    }
}
