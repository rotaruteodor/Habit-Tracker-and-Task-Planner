package com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitNumericalComparisonType;

public class HabitRealizationValidator {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isValid(Long realizationDate, Habit habit) {
        if (habit.getRealizations().containsKey(String.valueOf(realizationDate))) {
            if (habit.getEvaluationType() == HabitEvaluationType.YESNO) {
                return true;
            } else if (habit.getEvaluationType() == HabitEvaluationType.NUMERICAL) {
                Double realizationValue = habit.getRealizations().get(String.valueOf(realizationDate));
                return isNumericalValid(realizationValue, habit.getNumericalGoal(), habit.getNumericalComparisonType());
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isValidForExistingDate(Long realizationDate, Habit habit) {
        if (habit.getEvaluationType() == HabitEvaluationType.YESNO) {
            return true;
        } else if (habit.getEvaluationType() == HabitEvaluationType.NUMERICAL) {
            Double realizationValue = habit.getRealizations().get(String.valueOf(realizationDate));
            return isNumericalValid(realizationValue, habit.getNumericalGoal(), habit.getNumericalComparisonType());
        }
        return false;
    }

    public static boolean isNumericalValid(Double realization,
                                           Double numericalGoal,
                                           HabitNumericalComparisonType comparisonType) {

        if (comparisonType == HabitNumericalComparisonType.ATLEAST && realization >= numericalGoal) {
            return true;
        } else if (comparisonType == HabitNumericalComparisonType.EXACTLY && realization.equals(numericalGoal)) {
            return true;
        } else if (comparisonType == HabitNumericalComparisonType.LESSTHAN) {
            return realization < numericalGoal;
        }
        return false;
    }
}
