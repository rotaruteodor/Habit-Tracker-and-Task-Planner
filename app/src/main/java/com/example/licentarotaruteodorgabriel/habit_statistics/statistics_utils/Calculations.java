package com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitNumericalComparisonType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calculations {

    public static double getForecast(List<Double> data, double alpha) {
        if (data == null || data.size() == 0) {
            return 0;
        }

        double[] y = new double[data.size()];
        y[0] = data.get(0);
        int i;
        for (i = 1; i < data.size(); i++) {
            y[i] = alpha * data.get(i - 1) + (1 - alpha) * y[i - 1];
        }

        return alpha * data.get(data.size() - 1) + (1 - alpha) * y[i - 1];
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Double getNumericalHabitDeviationScore(Habit habit, Double maxDeviation) {
        HashMap<String, Double> realizations = habit.getRealizations();
        if (realizations == null || realizations.size() == 0) {
            return 0d;
        }

        HashMap<String, Double> realizationsTillToday = new HashMap<>();
        for (Map.Entry<String, Double> realization : realizations.entrySet()) {
            if (Long.parseLong(realization.getKey()) <= LocalDate.now().toEpochDay()) {
                realizationsTillToday.put(realization.getKey(), realization.getValue());
            }
        }

        if (realizationsTillToday.size() == 0) {
            return 0d;
        }

        Double numericalGoal = habit.getNumericalGoal();
        double averageDeviation;
        if (habit.getNumericalComparisonType() == HabitNumericalComparisonType.EXACTLY) {
            averageDeviation = realizationsTillToday.values().stream()
                    .map(o -> Math.abs(o - numericalGoal))
                    .mapToDouble(d -> d).average().getAsDouble();
        } else {
            averageDeviation = Math.abs(realizationsTillToday.values().stream()
                    .map(o -> o - numericalGoal)
                    .mapToDouble(d -> d).average().getAsDouble());
        }

        return averageDeviation > maxDeviation ? 0 : 1 - averageDeviation / maxDeviation;
    }


    public static Double getHabitStrength(double previousStreaksVsMissesScore,
                                          Integer currentStreak,
                                          Double averageStreak,
                                          Integer bestStreak,
                                          int nrOfDaysForHabitToForm) {
        double sum = 0;
        double currentStreakWeight = currentStreak + previousStreaksVsMissesScore;
        if (currentStreakWeight > 0) {
            sum += currentStreakWeight >= nrOfDaysForHabitToForm ? 9.0 / 10 : (double) 9 * currentStreakWeight / nrOfDaysForHabitToForm / 10;
        }
        sum += averageStreak >= nrOfDaysForHabitToForm ? 0.5 / 10 : 0.5 * averageStreak / nrOfDaysForHabitToForm / 10;
        sum += bestStreak >= nrOfDaysForHabitToForm ? 0.5 / 10 : (double) 0.5 * bestStreak / nrOfDaysForHabitToForm / 10;
        return sum;
    }

}
