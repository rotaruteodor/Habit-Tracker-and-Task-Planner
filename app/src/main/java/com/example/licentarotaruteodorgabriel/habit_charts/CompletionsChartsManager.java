package com.example.licentarotaruteodorgabriel.habit_charts;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitNumericalComparisonType;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RemoveDecimalsFormatter;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RemoveDecimalsFormatterLeftAxis;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RemoveDecimalsPercentagesFormatter;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDaysManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CompletionsChartsManager {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configurePieChart(Habit habit,
                                         PieChart pieChart,
                                         Context context) {

        if (habit.getStartDate() <= LocalDate.now().toEpochDay()) {
            Long totalNumberOfDays = HabitDaysManager.getTotalDaysPossible(habit);
            Long numberOfCompletions = getTotalNumberOfCompletions(habit);
            long numberOfMisses = totalNumberOfDays - numberOfCompletions;

            ArrayList<PieEntry> pieChartEntries = new ArrayList<>();
            ArrayList<Integer> pieChartColors = new ArrayList<>();
            pieChartEntries.add(new PieEntry(numberOfCompletions, "Completed"));
            pieChartColors.add(ContextCompat.getColor(context, R.color.main_green));
            pieChartEntries.add(new PieEntry(numberOfMisses, "Missed"));
            pieChartColors.add(ContextCompat.getColor(context, R.color.error_red));

            PieDataSet pieChartDataSet = new PieDataSet(pieChartEntries, "");
            pieChartDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieChartDataSet.setColors(pieChartColors);
            pieChartDataSet.setValueTextColor(Color.WHITE);
            pieChartDataSet.setValueTextSize(10);
            pieChartDataSet.setValueLineColor(Color.WHITE);
            pieChartDataSet.setValueTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
            pieChart.setExtraOffsets(20, 15, 20, 15);
            pieChart.getDescription().setEnabled(false);
            pieChart.setDrawEntryLabels(false);
            pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
            pieChart.getLegend().setTextColor(Color.WHITE);
            pieChart.setHoleColor(Color.TRANSPARENT);
            pieChart.setCenterTextColor(Color.WHITE);
            pieChart.setCenterText("Total " + totalNumberOfDays);
            pieChart.setCenterTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
            pieChart.getLegend().setTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
            pieChart.setEntryLabelColor(Color.WHITE);
            pieChart.animateY(1200, Easing.EaseInOutQuad);

            PieData pieData = new PieData(pieChartDataSet);
            pieData.setValueFormatter(new RemoveDecimalsPercentagesFormatter(totalNumberOfDays));
            pieChart.setData(pieData);
            pieChart.invalidate();

        } else {
            pieChart.setNoDataText("Habit hasn't started yet");
            pieChart.setNoDataTextColor(Color.WHITE);
            pieChart.setNoDataTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getTotalNumberOfCompletions(Habit habit) {
        if (habit.getEvaluationType() == HabitEvaluationType.YESNO) {
            return habit.getRealizations().keySet().stream()
                    .map(Long::parseLong)
                    .filter(m -> m <= LocalDate.now().toEpochDay())
                    .count();
        } else {
            Long numberOfCompletions = 0L;
            Double numericalGoal = habit.getNumericalGoal();
            HabitNumericalComparisonType comparisonType = habit.getNumericalComparisonType();

            for (Map.Entry<String, Double> realization : habit.getRealizations().entrySet()) {
                if (Long.parseLong(realization.getKey()) <= LocalDate.now().toEpochDay() &&
                        HabitRealizationValidator.isNumericalValid(
                                realization.getValue(),
                                numericalGoal,
                                comparisonType)) {
                    ++numberOfCompletions;
                }
            }
            return numberOfCompletions;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configureBarChart(Habit habit,
                                         BarChart barChart,
                                         TextView tvCurrMthCompPerc,
                                         TextView tvCurrMthMissPerc,
                                         TextView tvAvgCompPerc,
                                         TextView tvAvgMissPerc,
                                         Context context) {

        if (habit.getStartDate() <= LocalDate.now().toEpochDay()) {
            ArrayList<String> labels = new ArrayList<>();
            ArrayList<BarEntry> entries = new ArrayList<>();
            HashMap<String, Double> realizations = habit.getRealizations();
            LocalDate date = LocalDate.ofEpochDay(habit.getStartDate());

            int index = 0;
            int completionsSum = 0;
            int missedSum = 0;
            while (date.getMonthValue() <= LocalDate.now().getMonthValue()) {
                int numberOfCompletions = getNumberOfCompletionsForSpecificMonth(habit,
                        realizations,
                        date);
                Integer daysPossibleOfMonth = HabitDaysManager.getDaysPossibleOfMonth(habit, date);

                if (date.getMonthValue() == LocalDate.now().getMonthValue()) {
                    int completionPercent = Math.round(((float) numberOfCompletions / daysPossibleOfMonth) * 100);
                    int missPercent = Math.round(((float) daysPossibleOfMonth - numberOfCompletions) / daysPossibleOfMonth * 100);
                    tvCurrMthCompPerc.setText(String.valueOf(completionPercent).concat("%"));
                    tvCurrMthMissPerc.setText(String.valueOf(missPercent).concat("%"));
                }

                completionsSum += numberOfCompletions;
                missedSum += daysPossibleOfMonth - numberOfCompletions;

                entries.add(new BarEntry(index, new float[]{numberOfCompletions, daysPossibleOfMonth - numberOfCompletions}));
                labels.add(DateTimeFormatter.ofPattern("MMM/yy").format(date));

                ++index;
                date = date.plusMonths(1);
            }

            int avgCompletion = 0;
            int avgMiss = 0;
            if (completionsSum + missedSum != 0) {
                avgCompletion = Math.round(((float) completionsSum / (completionsSum + missedSum)) * 100);
                avgMiss = Math.round(((float) missedSum / (completionsSum + missedSum)) * 100);
            }
            tvAvgCompPerc.setText(String.valueOf(avgCompletion).concat("%"));
            tvAvgMissPerc.setText(String.valueOf(avgMiss).concat("%"));

            int[] stackColors = new int[]{ContextCompat.getColor(context, R.color.main_green),
                    ContextCompat.getColor(context, R.color.error_red)};
            BarDataSet barDataSet = new BarDataSet(entries, "");
            barDataSet.setColors(stackColors);
            barDataSet.setValueTextSize(12);

            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new RemoveDecimalsFormatter());
            barData.setValueTextColor(Color.WHITE);
            barData.setValueTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
            barChart.getAxisLeft().setValueFormatter(new RemoveDecimalsFormatterLeftAxis());
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getDescription().setEnabled(false);
            barChart.getAxisLeft().setTextColor(Color.WHITE);
            barChart.getAxisRight().setEnabled(false);
            barChart.getLegend().setEnabled(false);
            barChart.setDrawValueAboveBar(false);
            barChart.getXAxis().setGranularity(1f);
            barChart.setScaleEnabled(false);
            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            barChart.getXAxis().setTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
            barChart.getXAxis().setTextColor(Color.WHITE);
            barChart.animateY(400);
            barChart.animateX(400);
            barChart.setMaxVisibleValueCount(6);
            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    float[] values = entries.get((int) e.getX()).getYVals();
                    int completionPercent = Math.round(values[0] / (values[0] + values[1]) * 100);
                    int missPercent = Math.round(values[1] / (values[0] + values[1]) * 100);
                    tvCurrMthCompPerc.setText(String.valueOf(completionPercent).concat("%"));
                    tvCurrMthMissPerc.setText(String.valueOf(missPercent).concat("%"));
                }

                @Override
                public void onNothingSelected() {
                }
            });
            barChart.setData(barData);
            barChart.invalidate();
        } else {
            barChart.setNoDataText("Habit hasn't started yet");
            barChart.setNoDataTextColor(Color.WHITE);
            barChart.setNoDataTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static int getNumberOfCompletionsForSpecificMonth(Habit habit, HashMap<String, Double> realizations, LocalDate date) {
        int numberOfCompletions = 0;
        boolean isCurrentMonth = date.getMonthValue() == LocalDate.now().getMonthValue() &&
                date.getYear() == LocalDate.now().getYear();
        int maxDayOfMonthForCurrentMonth = LocalDate.now().getDayOfMonth();
        if (!realizations.containsKey(String.valueOf(LocalDate.now().toEpochDay()))) {
            --maxDayOfMonthForCurrentMonth;
        }
        for (Map.Entry<String, Double> realization : realizations.entrySet()) {
            LocalDate realizationDate = LocalDate.ofEpochDay(Long.parseLong(realization.getKey()));
            boolean isDayOfMonthSmallerThanCurrentDayOfMonth = true;

            if (isCurrentMonth) {
                isDayOfMonthSmallerThanCurrentDayOfMonth = realizationDate.getDayOfMonth() <= maxDayOfMonthForCurrentMonth;
            }
            if (realizationDate.getMonthValue() == date.getMonthValue() &&
                    isDayOfMonthSmallerThanCurrentDayOfMonth &&
                    HabitRealizationValidator.isValidForExistingDate(realizationDate.toEpochDay(), habit)) {
                ++numberOfCompletions;
            }
        }
        return numberOfCompletions;
    }
}