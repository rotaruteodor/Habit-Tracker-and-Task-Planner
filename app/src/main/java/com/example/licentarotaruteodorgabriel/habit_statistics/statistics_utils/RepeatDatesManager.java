package com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class RepeatDatesManager {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getNextDate(Long date, Long startDate, Integer numberOfDaysToRepeatAt) {
        return startDate + numberOfDaysToRepeatAt * (
                ((date - startDate) / numberOfDaysToRepeatAt) + 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getPreviousDate(Long date, Long startDate, Integer numberOfDaysToRepeatAt) {
        if ((date - startDate) % numberOfDaysToRepeatAt != 0) {
            return startDate + numberOfDaysToRepeatAt * (
                    ((date - startDate) / numberOfDaysToRepeatAt));
        } else {
            return startDate + numberOfDaysToRepeatAt * (
                    ((date - startDate) / numberOfDaysToRepeatAt) - 1);
        }
    }
}
