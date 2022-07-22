package com.example.licentarotaruteodorgabriel.habit_charts.charts_utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;

public class RemoveDecimalsFormatter extends ValueFormatter {
    private NumberFormat formatter;

    public RemoveDecimalsFormatter() {
        formatter = NumberFormat.getInstance();
    }

    @Override
    public String getFormattedValue(float value) {
        if (value == 0) {
            return "";
        } else if (value % 1 == 0) {
            return formatter.format(value);
        }
        return String.format("%.2f", value);
    }
}

