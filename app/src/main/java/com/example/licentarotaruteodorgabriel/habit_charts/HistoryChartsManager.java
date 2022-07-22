package com.example.licentarotaruteodorgabriel.habit_charts;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.MissedDayCalendarDecorator;
import com.example.licentarotaruteodorgabriel.habit_charts.charts_utils.RealizedDayCalendarDecorator;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.DaysOfMonthDateManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.DaysOfWeekDateManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDatesManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.temporal.TemporalAdjusters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


public class HistoryChartsManager {
    private static ArrayList<LocalDate> realizationDates = new ArrayList<>();
    private static ArrayList<LocalDate> missedDates = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void configureCalendar(MaterialCalendarView materialCalendarView,
                                         Habit habit,
                                         Context context) {

        Long habitStartDate = habit.getStartDate();
        materialCalendarView.setCurrentDate(org.threeten.bp.LocalDate.ofEpochDay(habitStartDate));
        materialCalendarView.state().edit()
                .setMinimumDate(org.threeten.bp.LocalDate.ofEpochDay(habitStartDate).with(TemporalAdjusters.firstDayOfMonth()))
                .setMaximumDate(org.threeten.bp.LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()))
                .commit();

        initializeDatesForCalendar(habit);

        boolean drawNumericalRealization = habit.getEvaluationType() == HabitEvaluationType.NUMERICAL;
        materialCalendarView.addDecorator(new RealizedDayCalendarDecorator(realizationDates,
                context, drawNumericalRealization));
        materialCalendarView.addDecorator(new MissedDayCalendarDecorator(missedDates,
                context, drawNumericalRealization));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void initializeDatesForCalendar(Habit habit) {
        realizationDates = new ArrayList<>();
        missedDates = new ArrayList<>();
        HashMap<String, Double> realizations = habit.getRealizations();

        if (realizations == null || realizations.size() == 0) {
            return;
        }

        Long dateInEpochDays = habit.getStartDate();
        if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFWEEK) {
            LocalDate habitStartDate = LocalDate.ofEpochDay(habit.getStartDate());
            if (!habit.getDaysOfWeek().contains(habitStartDate.getDayOfWeek().getValue())) {
                dateInEpochDays = DaysOfWeekDateManager.getNextDate(habitStartDate.toEpochDay(), habit.getDaysOfWeek());
            }
        } else if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFMONTH) {
            LocalDate habitStartDate = LocalDate.ofEpochDay(habit.getStartDate());
            if (!habit.getDaysOfMonth().contains(habitStartDate.getDayOfMonth())) {
                dateInEpochDays = DaysOfMonthDateManager.getNextDate(habitStartDate, habit.getDaysOfMonth());
            }
        }


        Long maxDate = LocalDate.now().toEpochDay();
        if (!habit.getRealizations().containsKey(String.valueOf(LocalDate.now().toEpochDay()))) {
            --maxDate;
        }

        while (dateInEpochDays <= maxDate) {
            if (HabitRealizationValidator.isValid(dateInEpochDays, habit)) {
                realizationDates.add(LocalDate.ofEpochDay(dateInEpochDays));
            } else {
                missedDates.add(LocalDate.ofEpochDay(dateInEpochDays));
            }
            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
        }
    }
}
