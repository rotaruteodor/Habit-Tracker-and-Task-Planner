package com.example.licentarotaruteodorgabriel.habit_charts.charts_utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class RemoveDecimalsOnlyPercentagesFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return String.format("%.0f", value).concat("%");
    }
}
