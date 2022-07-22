package com.example.licentarotaruteodorgabriel.habit_statistics.statistics_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class DaysOfMonthDateManager {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getNextDate(LocalDate currentDate, ArrayList<Integer> daysOfMonth) {
        Collections.sort(daysOfMonth);
        Integer currentDayOfMonthIndex = daysOfMonth.indexOf(currentDate.getDayOfMonth());

        if (currentDayOfMonthIndex != -1) {
            return getNextDateFromValidDate(currentDate, daysOfMonth);
        } else {
            return getNextDateFromRandomDate(currentDate, daysOfMonth);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Long getPreviousDate(LocalDate currentDate, ArrayList<Integer> daysOfMonth) {
        Collections.sort(daysOfMonth);
        Integer currentDayOfMonthIndex = daysOfMonth.indexOf(currentDate.getDayOfMonth());

        if (currentDayOfMonthIndex != -1) {
            return getPreviousDateFromValidDate(currentDate, daysOfMonth);
        } else {
            return getPreviousDateFromRandomDate(currentDate, daysOfMonth);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getNextDateFromValidDate(LocalDate currentDate, ArrayList<Integer> daysOfMonth) {
        Integer currentDayOfMonthIndex = daysOfMonth.indexOf(currentDate.getDayOfMonth());

        if (currentDayOfMonthIndex < daysOfMonth.size() - 1) {
            ++currentDayOfMonthIndex;
            while (true) {
                try {
                    return LocalDate.of(currentDate.getYear(),
                            currentDate.getMonthValue(),
                            daysOfMonth.get(currentDayOfMonthIndex))
                            .toEpochDay();
                } catch (Exception exception) {
                    currentDayOfMonthIndex = 0;
                    currentDate = currentDate.plusMonths(1);
                    exception.printStackTrace();
                }
            }
        } else {
            while (true) {
                currentDate = currentDate.plusMonths(1);
                try {
                    return LocalDate.of(currentDate.getYear(),
                            currentDate.getMonthValue(),
                            daysOfMonth.get(0))
                            .toEpochDay();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getNextDateFromRandomDate(LocalDate currentDate, ArrayList<Integer> daysOfMonth) {
        Collections.sort(daysOfMonth);
        if (currentDate.getDayOfMonth() > Collections.max(daysOfMonth)) {
            while (true) {
                currentDate = currentDate.plusMonths(1);
                try {
                    return LocalDate.of(currentDate.getYear(),
                            currentDate.getMonthValue(),
                            daysOfMonth.get(0))
                            .toEpochDay();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } else {
            int currentDayOfMonth = currentDate.getDayOfMonth();
            Integer firstDayOfMonthBiggerThanCurrentDayOfMonth = daysOfMonth.stream()
                    .filter(d -> d > currentDayOfMonth)
                    .findFirst()
                    .orElse(null);

            while (true) {
                try {
                    return LocalDate.of(currentDate.getYear(),
                            currentDate.getMonthValue(),
                            firstDayOfMonthBiggerThanCurrentDayOfMonth)
                            .toEpochDay();
                } catch (Exception exception) {
                    currentDate = currentDate.plusMonths(1);
                    exception.printStackTrace();
                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getPreviousDateFromValidDate(LocalDate currentDate, ArrayList<Integer> daysOfMonth) {
        Collections.sort(daysOfMonth);
        ArrayList<Integer> daysOfMonthReversed = new ArrayList<>(daysOfMonth);
        Collections.reverse(daysOfMonthReversed);

        Integer currentDayOfMonthIndex = daysOfMonth.indexOf(currentDate.getDayOfMonth());

        if (currentDayOfMonthIndex > 0) {
            return LocalDate.of(currentDate.getYear(),
                    currentDate.getMonthValue(),
                    daysOfMonth.get(currentDayOfMonthIndex - 1))
                    .toEpochDay();
        } else {
            while (true) {
                currentDate = currentDate.minusMonths(1);
                for (Integer dayOfMonth : daysOfMonthReversed) {
                    try {
                        return LocalDate.of(currentDate.getYear(),
                                currentDate.getMonthValue(),
                                dayOfMonth)
                                .toEpochDay();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Long getPreviousDateFromRandomDate(LocalDate currentDate, ArrayList<Integer> daysOfMonth) {
        Collections.sort(daysOfMonth);
        ArrayList<Integer> daysOfMonthReversed = new ArrayList<>(daysOfMonth);
        Collections.reverse(daysOfMonthReversed);

        if (currentDate.getDayOfMonth() <= Collections.min(daysOfMonth)) {
            while (true) {
                currentDate = currentDate.minusMonths(1);
                for (Integer dayOfMonth : daysOfMonthReversed) {
                    try {
                        return LocalDate.of(currentDate.getYear(),
                                currentDate.getMonthValue(),
                                dayOfMonth)
                                .toEpochDay();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        } else {
            int currentDayOfMonth = currentDate.getDayOfMonth();
            Integer firstDayOfMonthSmallerThanCurrentDayOfMonth = daysOfMonthReversed.stream()
                    .filter(d -> d < currentDayOfMonth)
                    .findFirst()
                    .orElse(null);

            return LocalDate.of(currentDate.getYear(),
                    currentDate.getMonthValue(),
                    firstDayOfMonthSmallerThanCurrentDayOfMonth)
                    .toEpochDay();
        }
    }
}


