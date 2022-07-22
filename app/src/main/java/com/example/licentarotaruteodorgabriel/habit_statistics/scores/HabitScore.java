package com.example.licentarotaruteodorgabriel.habit_statistics.scores;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.Calculations;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.DaysOfMonthDateManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.DaysOfWeekDateManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitDatesManager;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class HabitScore {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static double get(Habit habit,
                             double streaksVsMisses,
                             Integer currentStreak,
                             Double averageStreak,
                             Integer bestStreak) {

        ArrayList<Double> realizationsValues = getAllBinaryRealizationValues(habit);
        if (habit.getEvaluationType() == HabitEvaluationType.YESNO) {
            return 100 * (Calculations.getForecast(realizationsValues, 0.1) * 0.7 +
                    Calculations.getHabitStrength(streaksVsMisses,
                            currentStreak,
                            averageStreak,
                            bestStreak,
                            habit.getDaysToFormHabit()) * 0.3);
        } else if (habit.getEvaluationType() == HabitEvaluationType.NUMERICAL) {
            return 100 * (Calculations.getForecast(realizationsValues, 0.1) * 0.5 +
                    Calculations.getHabitStrength(streaksVsMisses,
                            currentStreak,
                            averageStreak,
                            bestStreak,
                            habit.getDaysToFormHabit()) * 0.3 +
                    Calculations.getNumericalHabitDeviationScore(habit, habit.getNumericalGoal()) * 0.2);
        }
        return -1;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static ArrayList<Double> getAllBinaryRealizationValues(Habit habit) {
        HashMap<String, Double> realizations = habit.getRealizations();
        ArrayList<Double> realizationValues = new ArrayList<>();

        if (realizations == null || realizations.size() == 0) {
            return realizationValues;
        }

        realizationValues.add(0.);

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
        if (!realizations.containsKey(String.valueOf(LocalDate.now().toEpochDay()))) {
            --maxDate;
        }

        while (dateInEpochDays <= maxDate) {
            if (HabitRealizationValidator.isValid(dateInEpochDays, habit)) {
                realizationValues.add(1.0);
            } else {
                realizationValues.add(0.0);
            }
            dateInEpochDays = HabitDatesManager.getNextDate(dateInEpochDays, habit);
        }

        return realizationValues;
    }
}
