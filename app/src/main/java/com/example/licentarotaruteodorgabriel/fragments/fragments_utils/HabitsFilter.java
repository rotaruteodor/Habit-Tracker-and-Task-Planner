package com.example.licentarotaruteodorgabriel.fragments.fragments_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils.HabitRealizationValidator;

import java.time.LocalDate;
import java.util.ArrayList;

public class HabitsFilter {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Habit> getOngoingHabits(ArrayList<Habit> habits, LocalDate selectedDate) {
        ArrayList<Habit> filteredHabits = new ArrayList<>();
        for (Habit habit : habits) {
            if (isValidForOngoingHabitsList(habit, selectedDate)) {
                filteredHabits.add(habit);
            }
        }
        return filteredHabits;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean isValidForOngoingHabitsList(Habit habit, LocalDate selectedDate) {
        boolean isHabitStarted = habit.getStartDate() < selectedDate.toEpochDay();
        boolean isHabitRealizationValid = HabitRealizationValidator.isValid(
                selectedDate.toEpochDay(), habit);

        if (!isHabitStarted || habit.getArchived() || isHabitRealizationValid) {
            return false;
        }

        if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFWEEK) {
            return habit.getDaysOfWeek().contains(selectedDate.getDayOfWeek().getValue());
        } else if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFMONTH) {
            return habit.getDaysOfMonth().contains(selectedDate.getDayOfMonth());
        } else if (habit.getFrequency() == HabitFrequency.REPEAT) {
            return (selectedDate.toEpochDay() - habit.getStartDate()) % habit.getNumberOfDaysForRepeat() == 0;
        }
        return true;
    }

}
