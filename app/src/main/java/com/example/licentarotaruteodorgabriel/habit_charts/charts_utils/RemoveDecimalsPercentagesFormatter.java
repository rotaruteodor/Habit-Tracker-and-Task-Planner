package com.example.licentarotaruteodorgabriel.habit_charts.charts_utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.NumberFormat;

public class RemoveDecimalsPercentagesFormatter extends ValueFormatter {
    private NumberFormat formatter;
    private Long totalNumberOfDays;

    public RemoveDecimalsPercentagesFormatter(Long totalNumberOfDays) {
        this.totalNumberOfDays = totalNumberOfDays;
        formatter = NumberFormat.getInstance();
    }

    @Override
    public String getFormattedValue(float value) {
        StringBuilder sb = new StringBuilder();

        if (value == 0) {
            return "";
        }

        sb.append(formatter.format(value))
                .append(" (")
                .append(Math.round(((value / totalNumberOfDays)*100)))
                .append("%)");
        return sb.toString();
    }
}
