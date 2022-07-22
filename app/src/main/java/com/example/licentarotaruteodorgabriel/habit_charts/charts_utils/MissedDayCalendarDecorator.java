package com.example.licentarotaruteodorgabriel.habit_charts.charts_utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.licentarotaruteodorgabriel.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.time.LocalDate;
import java.util.ArrayList;

public class MissedDayCalendarDecorator implements DayViewDecorator {
    private Context context;
    private ArrayList<LocalDate> dates;
    private boolean drawNumericalRealization;

    public MissedDayCalendarDecorator(ArrayList<LocalDate> dates, Context context, boolean drawNumericalRealization) {
        this.dates = new ArrayList<>(dates);
        this.context = context;
        this.drawNumericalRealization = drawNumericalRealization;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(LocalDate.of(day.getYear(), day.getMonth(), day.getDay()));
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rectangle_error_red));
    }
}
