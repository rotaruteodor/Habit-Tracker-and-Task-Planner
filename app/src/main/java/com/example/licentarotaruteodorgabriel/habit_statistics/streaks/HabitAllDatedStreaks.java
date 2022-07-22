package com.example.licentarotaruteodorgabriel.habit_statistics.streaks;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.DaysOfMonthDateManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.DaysOfWeekDateManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDatesManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HabitAllDatedStreaks {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LinkedHashMap<Long, Integer> getAllDatedStreaks(Habit habit) {
        HashMap<String , Double> realizations = habit.getRealizations();
        LinkedHashMap<Long, Integer> datedStreaks = new LinkedHashMap<>();

        if (realizations == null || realizations.size() == 0) {
            return datedStreaks;
        }

        int currentStreak = 0;

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
                ++currentStreak;
                datedStreaks.put(dateInEpochDays, currentStreak);
            } else {
                currentStreak = 0;
                datedStreaks.put(dateInEpochDays, 0);
            }
            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
        }
        return datedStreaks;
    }
}
