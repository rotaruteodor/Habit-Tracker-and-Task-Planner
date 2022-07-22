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

public class HabitBestStreak {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Integer getBestStreak(Habit habit) {
        HashMap<String , Double> realizations = habit.getRealizations();
        if (realizations == null || realizations.size() == 0) {
            return 0;
        }
        Integer bestStreak = 0;
        Integer currentStreak = 0;
        Long dateInEpochDays = habit.getStartDate();
        Long maxDate = Collections.max(
                realizations.keySet()
                        .stream()
                        .map(Long::parseLong)
                        .collect(Collectors.toList()));

        if(maxDate > LocalDate.now().toEpochDay()){
            maxDate = LocalDate.now().toEpochDay();
        }

        while (dateInEpochDays <= maxDate) {
            if (HabitRealizationValidator.isValid(dateInEpochDays, habit)) {
                ++currentStreak;
            } else {
                if (currentStreak > bestStreak) {
                    bestStreak = currentStreak;
                }
                currentStreak = 0;
            }
            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
        }

        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
        return bestStreak;
    }
}
