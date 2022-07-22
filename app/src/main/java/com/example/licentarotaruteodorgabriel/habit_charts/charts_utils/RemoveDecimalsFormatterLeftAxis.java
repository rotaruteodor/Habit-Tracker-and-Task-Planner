package com.example.licentarotaruteodorgabriel.habit_charts.charts_utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;

public class RemoveDecimalsFormatterLeftAxis extends ValueFormatter {
    private NumberFormat formatter;

    public RemoveDecimalsFormatterLeftAxis() {
        formatter = NumberFormat.getInstance();
    }

    @Override
    public String getFormattedValue(float value) {
        if (value % 1 == 0) {
            return formatter.format(value);
        }
        return "";
    }
}
