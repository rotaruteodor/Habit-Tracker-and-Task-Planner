package com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class HabitDaysManager {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getTotalDaysPossible(Habit habit) {
        Long totalDaysPossible = 0L;
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
            ++totalDaysPossible;
            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
        }
        return totalDaysPossible;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Integer getDaysPossibleOfMonth(Habit habit, LocalDate date) {
        int month = date.getMonthValue();
        int daysPossible = 0;

        LocalDate habitStartDate = LocalDate.ofEpochDay(habit.getStartDate());
        if (habitStartDate.getMonthValue() == date.getMonthValue() && habitStartDate.getYear() == date.getYear()) {
            date = habitStartDate;
        } else {
            date = date.with(TemporalAdjusters.firstDayOfMonth());
        }

        if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFWEEK) {
            if (!habit.getDaysOfWeek().contains(date.getDayOfWeek().getValue())) {
                date = LocalDate.ofEpochDay(DaysOfWeekDateManager.getNextDate(date.toEpochDay(), habit.getDaysOfWeek()));
            }
        } else if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFMONTH) {
            if (!habit.getDaysOfMonth().contains(date.getDayOfMonth())) {
                date = LocalDate.ofEpochDay(DaysOfMonthDateManager.getNextDate(date, habit.getDaysOfMonth()));
            }
        } else if (habit.getFrequency() == HabitFrequency.REPEAT) {
            if ((date.toEpochDay() - habitStartDate.toEpochDay()) % habit.getNumberOfDaysForRepeat() != 0) {
                date = LocalDate.ofEpochDay(RepeatDatesManager.getNextDate(date.toEpochDay(),
                        habitStartDate.toEpochDay(),
                        habit.getNumberOfDaysForRepeat()));
            }
        }

        if (LocalDate.now().getMonthValue() == date.getMonthValue() && LocalDate.now().getYear() == date.getYear()) {
            int maxDayOfMonth = LocalDate.now().getDayOfMonth();
            if (!habit.getRealizations().containsKey(String.valueOf(LocalDate.now().toEpochDay()))) {
                --maxDayOfMonth;
            }
            while (date.getMonthValue() == month && date.getDayOfMonth() <= maxDayOfMonth) {
                ++daysPossible;
                date = LocalDate.ofEpochDay(HabitDatesManager.getNextDate(date.toEpochDay(), habit));
            }
        } else {
            while (date.getMonthValue() == month) {
                ++daysPossible;
                date = LocalDate.ofEpochDay(HabitDatesManager.getNextDate(date.toEpochDay(), habit));
            }
        }
        return daysPossible;
    }
}