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
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RemoveDecimalsFormatterLeftAxis;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RemoveDecimalsOnlyPercentagesFormatter;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.Calculations;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDatesManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;
import com.example.licentarotaruteodorgabriel.habit_statistics.streaks.HabitPrevStreaksVsMissesDiff;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class StrengthChartsManager {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configureScorePieChart(PieChart pieChart,
                                              Context context,
                                              double habitStrength) {

        ArrayList<PieEntry> pieChartEntries = new ArrayList<>();
        ArrayList<Integer> pieChartColors = new ArrayList<>();

        pieChartEntries.add(new PieEntry((float) habitStrength * 100, "Strength"));
        pieChartColors.add(ContextCompat.getColor(context, R.color.strong_orange));
        pieChartEntries.add(new PieEntry((float) (100 - habitStrength * 100), "Rest"));
        pieChartColors.add(ContextCompat.getColor(context, R.color.main_transparent_white));

        PieDataSet pieChartDataSet = new PieDataSet(pieChartEntries, "");
        pieChartDataSet.setDrawValues(false);
        pieChartDataSet.setColors(pieChartColors);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(14);
        pieChart.setCenterText(String.format("%.0f", habitStrength * 100).concat("%"));
        pieChart.setCenterTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
        pieChart.animateY(1200, Easing.EaseOutCirc);
        PieData pieData = new PieData(pieChartDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configureStrengthEvolutionLineChart(Habit habit,
                                                           LineChart lineChart,
                                                           Context context) {
        if (habit.getRealizations().size() > 0) {
            ArrayList<Entry> streaks = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            String labelDatePattern;
            if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFWEEK) {
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

            double currentStrength;
            int index = 0;
            int currentStreak = 0;
            int bestStreak = 0;
            double averageStreak = 0;
            int nrOfStreaks = 0;
            double sumOfAllStreaks = 0;
            HashMap<String, Double> realizations = habit.getRealizations();
            HashMap<String, Double> realizationsSoFar = new HashMap<>();
            Long dateInEpochDays = habit.getStartDate();
            double streaksVsMissesScore;
            int daysToFormHabit = habit.getDaysToFormHabit();

            Long maxDate = LocalDate.now().toEpochDay();
            if (!habit.getRealizations().containsKey(String.valueOf(LocalDate.now().toEpochDay()))) {
                --maxDate;
            }

            while (dateInEpochDays <= maxDate) {
                if (HabitRealizationValidator.isValid(dateInEpochDays, habit)) {
                    realizationsSoFar.put(String.valueOf(dateInEpochDays),
                            realizations.get(String.valueOf(dateInEpochDays)));
                    if (currentStreak == 0) {
                        ++nrOfStreaks;
                    }
                    ++currentStreak;
                    ++sumOfAllStreaks;
                    if (nrOfStreaks == 1) {
                        averageStreak = bestStreak = currentStreak;
                    } else {
                        if (currentStreak > bestStreak) {
                            bestStreak = currentStreak;
                        }
                        averageStreak = sumOfAllStreaks / nrOfStreaks;
                    }
                } else if (currentStreak > 0) {
                    if (currentStreak > bestStreak) {
                        bestStreak = currentStreak;
                    }
                    averageStreak = sumOfAllStreaks / nrOfStreaks;
                    currentStreak = 0;
                }
                streaksVsMissesScore = HabitPrevStreaksVsMissesDiff.get(habit, dateInEpochDays, realizationsSoFar);
                currentStrength = Calculations.getHabitStrength(streaksVsMissesScore,
                        currentStreak,
                        averageStreak,
                        bestStreak,
                        daysToFormHabit);

                streaks.add(new Entry(index, (float) currentStrength * 100));
                String formattedDate = DateTimeFormatter.ofPattern(labelDatePattern)
                        .format(LocalDate.ofEpochDay(dateInEpochDays));
                labels.add(formattedDate);

                ++index;
                dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
            }

            LineDataSet lineDataSet = new LineDataSet(streaks, "");
            lineDataSet.setValueTextSize(11);
            lineDataSet.setColor(ContextCompat.getColor(context, R.color.strong_orange));
            lineDataSet.setCircleColor(Color.WHITE);
            LineData lineData = new LineData(lineDataSet);
            lineData.setValueFormatter(new RemoveDecimalsOnlyPercentagesFormatter());
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
//            lineChart.setXAxisRenderer(new LabelNewlineFormatter(lineChart.getViewPortHandler(),
//                    lineChart.getXAxis(),
//                    lineChart.getTransformer(YAxis.AxisDependency.LEFT)));
            lineChart.invalidate();
        } else {
            lineChart.setNoDataText("This graph requires at least one completion");
            lineChart.setNoDataTextColor(Color.WHITE);
            lineChart.setNoDataTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
        }
    }
}
