package com.example.licentarotaruteodorgabriel.habit_charts;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.licentarotaruteodorgabriel.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ScoreChartsManager {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configureScorePieChart(PieChart pieChart,
                                              double score,
                                              Context context) {
        ArrayList<PieEntry> pieChartEntries = new ArrayList<>();
        ArrayList<Integer> pieChartColors = new ArrayList<>();

        pieChartEntries.add(new PieEntry((float) score, "score"));
        pieChartColors.add(ContextCompat.getColor(context, R.color.main_green));
        pieChartEntries.add(new PieEntry((float) (100 - score), "rest"));
        pieChartColors.add(ContextCompat.getColor(context, R.color.main_transparent_white));

        PieDataSet pieChartDataSet = new PieDataSet(pieChartEntries, "");
        pieChartDataSet.setDrawValues(false);
        pieChartDataSet.setColors(pieChartColors);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(14);
        pieChart.setCenterText(String.format("%.0f", score));
        pieChart.setCenterTextTypeface(ResourcesCompat.getFont(context, R.font.russo_one));
        pieChart.animateY(2000, Easing.EaseInOutBack);
        PieData pieData = new PieData(pieChartDataSet);
        pieChart.setHoleRadius(70);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

}
