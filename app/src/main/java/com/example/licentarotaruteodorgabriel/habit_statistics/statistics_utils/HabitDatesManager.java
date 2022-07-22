package com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;

import java.time.LocalDate;

public class HabitDatesManager {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getNextDate(Long date, Habit habit){
        HabitFrequency frequency = habit.getFrequency();

        if (frequency == HabitFrequency.DAILY) {
            return ++date;
        } else if (frequency == HabitFrequency.SPECIFICDAYSOFWEEK) {
            return DaysOfWeekDateManager.getNextDate(date, habit.getDaysOfWeek());
        } else if (frequency == HabitFrequency.SPECIFICDAYSOFMONTH) {
            return DaysOfMonthDateManager.getNextDate(LocalDate.ofEpochDay(date), habit.getDaysOfMonth());
        } else if (frequency == HabitFrequency.REPEAT) {
            return RepeatDatesManager.getNextDate(date, habit.getStartDate(), habit.getNumberOfDaysForRepeat());
        }

        return -1L;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getPreviousDate(Long date, Habit habit){
        HabitFrequency frequency = habit.getFrequency();

        if (frequency == HabitFrequency.DAILY) {
            return --date;
        } else if (frequency == HabitFrequency.SPECIFICDAYSOFWEEK) {
            return DaysOfWeekDateManager.getPreviousDate(date, habit.getDaysOfWeek());
        } else if (frequency == HabitFrequency.SPECIFICDAYSOFMONTH) {
            return DaysOfMonthDateManager.getPreviousDate(LocalDate.ofEpochDay(date), habit.getDaysOfMonth());
        } else if (frequency == HabitFrequency.REPEAT) {
            return RepeatDatesManager.getPreviousDate(date, habit.getStartDate(), habit.getNumberOfDaysForRepeat());
        }

        return -1L;
    }
}
