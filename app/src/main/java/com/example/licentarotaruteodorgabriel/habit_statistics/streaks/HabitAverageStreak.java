package com.example.licentarotaruteodorgabriel.habit_statistics.streaks;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDatesManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HabitAverageStreak {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Double getAverageStreak(Habit habit) {
        HashMap<String, Double> realizations = habit.getRealizations();
        if (realizations == null || realizations.size() == 0) {
            return 0d;
        }
        int currentStreak = 0;
        int sumOfStreaks = 0;
        int numberOfStreaks = 0;
        Long dateInEpochDays = habit.getStartDate();
        Long maxDate = Collections.max(
                realizations.keySet()
                        .stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList()));

        if (maxDate > LocalDate.now().toEpochDay()) {
            maxDate = LocalDate.now().toEpochDay();
        }

        while (dateInEpochDays <= maxDate) {
            if (HabitRealizationValidator.isValid(dateInEpochDays, habit)) {
                ++currentStreak;
            } else {
                if (currentStreak > 0) {
                    sumOfStreaks += currentStreak;
                    ++numberOfStreaks;
                }
                currentStreak = 0;
            }
            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
        }

        if (currentStreak > 0) {
            sumOfStreaks += currentStreak;
            ++numberOfStreaks;
            return (double) sumOfStreaks / numberOfStreaks;
        }
        if (numberOfStreaks > 0) {
            return (double) sumOfStreaks / numberOfStreaks;
        }

        return 0d;
    }
}
