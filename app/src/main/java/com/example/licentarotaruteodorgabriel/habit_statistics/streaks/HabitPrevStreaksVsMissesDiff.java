package com.example.licentarotaruteodorgabriel.habit_statistics.streaks;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDatesManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;

import java.time.LocalDate;
import java.util.HashMap;

public class HabitPrevStreaksVsMissesDiff {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static double get(Habit habit) {
        HashMap<String, Double> realizations = habit.getRealizations();
        if (realizations == null || realizations.size() == 0) {
            return 0;
        }
        double difference = 0;
        int currentStreak = 0;
        Long dateInEpochDays = habit.getStartDate();
        int foundStreak = 0;
        int foundMisses = 0;
        Long maxDate = LocalDate.now().toEpochDay();
        if (!habit.getRealizations().containsKey(String.valueOf(LocalDate.now().toEpochDay()))) {
            --maxDate;
        }

        while (dateInEpochDays <= maxDate) {
            if (HabitRealizationValidator.isValid(dateInEpochDays, habit)) {
                ++currentStreak;
                if (currentStreak == 1 && foundStreak > 0) {
                    difference += foundStreak - 3 * foundMisses;
                    foundMisses = 0;
                    foundStreak = 0;
                }
            } else {
                if (foundStreak > 0) {
                    ++foundMisses;
                } else if (currentStreak > 0) {
                    foundStreak = currentStreak;
                    foundMisses = 1;
                }
                currentStreak = 0;
            }
            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
        }

        difference += foundStreak > 0 ? foundStreak - 3 * foundMisses : 0;
        return difference < 0 ? 0 : difference;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static double get(Habit habit, Long maxDate, HashMap<String, Double> realizations) {
        if (realizations == null || realizations.size() == 0) {
            return 0;
        }
        double difference = 0;
        int currentStreak = 0;
        Long dateInEpochDays = habit.getStartDate();
        int foundStreak = 0;
        int foundMisses = 0;

        while (dateInEpochDays <= maxDate) {
            if (HabitRealizationValidator.isValid(dateInEpochDays, habit)) {
                ++currentStreak;
                if (currentStreak == 1 && foundStreak > 0) {
                    difference += foundStreak - 3 * foundMisses;
                    foundMisses = 0;
                    foundStreak = 0;
                }
            } else {
                if (foundStreak > 0) {
                    ++foundMisses;
                } else if (currentStreak > 0) {
                    foundStreak = currentStreak;
                    foundMisses = 1;
                }
                currentStreak = 0;
            }
            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
        }

        difference += foundStreak > 0 ? foundStreak - 3 * foundMisses : 0;
        return difference < 0 ? 0 : difference;
    }
}
