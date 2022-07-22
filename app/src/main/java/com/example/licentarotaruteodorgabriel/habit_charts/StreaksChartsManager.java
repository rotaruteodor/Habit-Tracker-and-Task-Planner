package com.example.licentarotaruteodorgabriel.habit_charts;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RemoveDecimalsFormatter;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RemoveDecimalsFormatterLeftAxis;
import com.example.licentarotaruteodorgabriel.habit_statistics.streaks.HabitAllDatedStreaks;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StreaksChartsManager {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configureHorizontalBarChart(Habit receivedHabit,
                                                   HorizontalBarChart horizontalBarChart,
                                                   Context context,
                                                   Integer currentStreak,
                                                   Double averageStreak,
                                                   Integer bestStreak) {

        if (receivedHabit.getRealizations().size() > 0) {
            ArrayList<Double> valueList = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            ArrayList<BarEntry> entries = new ArrayList<>();
            ArrayList<Integer> barChartColors = new ArrayList<>();

            valueList.add(averageStreak);
            valueList.add(Double.valueOf(bestStreak));
            valueList.add(Double.valueOf(currentStreak));

            labels.add("Average streak");
            labels.add("Best streak");
            labels.add("Current streak");

            barChartColors.add(ContextCompat.getColor(context, R.color.strong_orange));
            barChartColors.add(ContextCompat.getColor(context, R.color.main_green));
            barChartColors.add(ContextCompat.getColor(context, R.color.modern_blue));

            for (int i = 0; i < valueList.size(); i++) {
                BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
                entries.add(barEntry);
            }

            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(barChartColors);
            barDataSet.setValueTextSize(12);

            BarData data = new BarData(barDataSet);
            data.setValueFormatter(new RemoveDecimalsFormatter());
            data.setValueTextColor(Color.WHITE);
            horizontalBarChart.getAxisLeft().setAxisMinimum(0);
            horizontalBarChart.getDescription().setEnabled(false);
            horizontalBarChart.getAxisRight().setEnabled(false);
            horizontalBarChart.getAxisLeft().setEnabled(false);
            horizontalBarChart.getLegend().setEnabled(false);
            horizontalBarChart.getXAxis().setDrawGridLines(false);
            horizontalBarChart.setDrawValueAboveBar(false);
            horizontalBarChart.getXAxis().setGranularity(1f);
            horizontalBarChart.setScaleEnabled(false);
            horizontalBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            horizontalBarChart.getXAxis().setTextColor(Color.WHITE);
            horizontalBarChart.getXAxis().setTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
            horizontalBarChart.animateY(400);
            horizontalBarChart.animateX(400);
            horizontalBarChart.setData(data);
            horizontalBarChart.invalidate();
        } else {
            horizontalBarChart.setNoDataText("No streaks available for this habit yet");
            horizontalBarChart.setNoDataTextColor(Color.WHITE);
            horizontalBarChart.setNoDataTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configureStreaksEvolutionLineChart(Habit receivedHabit,
                                                          LineChart lineChart,
                                                          Context context) {

        if (receivedHabit.getRealizations().size() > 0) {
            ArrayList<Entry> streaks = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            LinkedHashMap<Long, Integer> datedStreaks = HabitAllDatedStreaks.getAllDatedStreaks(receivedHabit);
            String labelDatePattern;
            if (receivedHabit.getFrequency() == HabitFrequency.SPECIFICDAYSOFWEEK) {
//                labelDatePattern = "dd/MM EEE\nYYYY";
                labelDatePattern = "dd/MM EEE";
                lineChart.getXAxis().setLabelRotationAngle(-40f);
                lineChart.getXAxis().setTextSize(7);
            } else {
//                labelDatePattern = "dd-MMM\nYYYY";
                labelDatePattern = "dd-MMM";
                lineChart.getXAxis().setLabelRotationAngle(-30f);
                lineChart.getXAxis().setTextSize(8);
            }

            int index = 0;
            for (Map.Entry<Long, Integer> datedStreak : datedStreaks.entrySet()) {
                streaks.add(new Entry(index, datedStreak.getValue()));
                ++index;
                String formattedDate = DateTimeFormatter.ofPattern(labelDatePattern)
                        .format(LocalDate.ofEpochDay(
                                datedStreak.getKey()
                        ));
                labels.add(formattedDate);
            }

            LineDataSet lineDataSet = new LineDataSet(streaks, "");
            lineDataSet.setValueTextSize(11);
            lineDataSet.setColor(ContextCompat.getColor(context, R.color.main_green));
            lineDataSet.setCircleColor(Color.WHITE);
            LineData lineData = new LineData(lineDataSet);
            lineData.setValueFormatter(new RemoveDecimalsFormatter());
            lineData.setValueTextColor(Color.WHITE);
            lineChart.getAxisRight().setEnabled(false);
            lineChart.getAxisLeft().setValueFormatter(new RemoveDecimalsFormatterLeftAxis());
            lineChart.getAxisLeft().setTextColor(Color.WHITE);
            lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            lineChart.getXAxis().setDrawGridLines(true);
            lineChart.getAxisLeft().setDrawGridLines(true);
            lineChart.getAxisRight().setDrawGridLines(false);
            lineChart.getXAxis().setDrawLabels(true);
            lineChart.animateX(1200, Easing.EaseInSine);
            lineChart.getDescription().setEnabled(false);
            lineChart.setExtraBottomOffset(10);
            lineChart.getLegend().setEnabled(false);
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(false);
            lineChart.getXAxis().setGranularity(1f);
            lineChart.getXAxis().setTextColor(Color.WHITE);
            lineChart.setData(lineData);
            lineChart.setVisibleXRangeMaximum(6);
            lineChart.invalidate();
//            lineChart.setXAxisRenderer(new LabelNewlineFormatter(lineChart.getViewPortHandler(),
//                    lineChart.getXAxis(),
//                    lineChart.getTransformer(YAxis.AxisDependency.LEFT)));
        } else {
            lineChart.setNoDataText("This graph requires at least one completion");
            lineChart.setNoDataTextColor(Color.WHITE);
            lineChart.setNoDataTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
        }
    }
}