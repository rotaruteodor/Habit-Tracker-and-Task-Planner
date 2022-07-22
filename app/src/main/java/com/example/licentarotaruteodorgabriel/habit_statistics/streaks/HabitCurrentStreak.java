package com.example.licentarotaruteodorgabriel.habit_statistics.streaks;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDatesManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;

import java.time.LocalDate;

public class HabitCurrentStreak {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Integer getCurrentStreak(Habit habit) {
        Integer currentStreak = 0;
        if(habit.getStartDate() > LocalDate.now().toEpochDay()){
            return 0;
        }
        if (HabitRealizationValidator.isValid(LocalDate.now().toEpochDay(), habit)) {
            ++currentStreak;
        }
        Long dateInEpochDays = HabitDatesManager.getPreviousDate(LocalDate.now().toEpochDay(), habit);
        while (HabitRealizationValidator.isValid(dateInEpochDays, habit)) {
            ++currentStreak;
            dateInEpochDays = HabitDatesManager.getPreviousDate(dateInEpochDays, habit);
        }
        return currentStreak;
    }
}
