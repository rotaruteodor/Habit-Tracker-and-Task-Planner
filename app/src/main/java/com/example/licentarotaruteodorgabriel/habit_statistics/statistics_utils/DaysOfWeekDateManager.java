package com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class DaysOfWeekDateManager {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getNextDate(Long currentDate, ArrayList<Integer> daysOfWeek) {
        Collections.sort(daysOfWeek);
        int currentDayOfWeekIndex = daysOfWeek.indexOf(LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue());

        if (currentDayOfWeekIndex != -1) {
            return getNextDateFromValidDate(currentDate, daysOfWeek);
        } else {
            return getNextDateFromRandomDate(currentDate, daysOfWeek);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getPreviousDate(Long currentDate, ArrayList<Integer> daysOfWeek) {
        Collections.sort(daysOfWeek);
        int currentDayOfWeekIndex = daysOfWeek.indexOf(LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue());

        if (currentDayOfWeekIndex != -1) {
            return getPreviousDateFromValidDate(currentDate, daysOfWeek);
        } else {
            return getPreviousDateFromRandomDate(currentDate, daysOfWeek);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getNextDateFromValidDate(Long currentDate, ArrayList<Integer> daysOfWeek) {
        Collections.sort(daysOfWeek);
        Integer currentDayOfWeekIndex = daysOfWeek.indexOf(LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue());

        if (currentDayOfWeekIndex < daysOfWeek.size() - 1) {
            return currentDate +
                    (daysOfWeek.get(currentDayOfWeekIndex + 1) - daysOfWeek.get(currentDayOfWeekIndex));
        }
        return currentDate +
                (7 - (daysOfWeek.get(currentDayOfWeekIndex) - daysOfWeek.get(0)));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getNextDateFromRandomDate(Long currentDate, ArrayList<Integer> daysOfWeek) {
        Collections.sort(daysOfWeek);

        if (LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue() > Collections.max(daysOfWeek)) {
            return currentDate +
                    (7 - (LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue() - Collections.min(daysOfWeek)));
        } else {
            Integer firstDayOfWeekBiggerThanCurrentDayOfWeek = daysOfWeek.stream()
                    .filter(d -> d > LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue())
                    .findFirst()
                    .orElse(null);

            return currentDate +
                    (firstDayOfWeekBiggerThanCurrentDayOfWeek -
                            LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getPreviousDateFromValidDate(Long currentDate, ArrayList<Integer> daysOfWeek) {
        Collections.sort(daysOfWeek);
        Integer currentDayOfWeekIndex = daysOfWeek.indexOf(LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue());

        if (currentDayOfWeekIndex > 0) {
            return currentDate - (daysOfWeek.get(currentDayOfWeekIndex) - daysOfWeek.get(currentDayOfWeekIndex - 1));
        }
        return currentDate - (7 - (daysOfWeek.get(daysOfWeek.size() - 1) /*fiindca e ordonata lista, asta e maximul*/
                - daysOfWeek.get(currentDayOfWeekIndex))); /*sau daysOfWeek.get(0)*/
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getPreviousDateFromRandomDate(Long currentDate, ArrayList<Integer> daysOfWeek) {
        Collections.sort(daysOfWeek);
        ArrayList<Integer> daysOfWeekReversed = new ArrayList<>(daysOfWeek);
        Collections.reverse(daysOfWeekReversed);

        if (LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue() <= Collections.min(daysOfWeek)) {
            return currentDate - (7 - (Collections.max(daysOfWeek) -
                    LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue()));
        } else {
            Integer firstDayOfWeekSmallerThanCurrentDayOfWeek = daysOfWeekReversed.stream()
                    .filter(d -> d < LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue())
                    .findFirst()
                    .orElse(null);

            return currentDate - (LocalDate.ofEpochDay(currentDate).getDayOfWeek().getValue() -
                    firstDayOfWeekSmallerThanCurrentDayOfWeek);
        }
    }
}
