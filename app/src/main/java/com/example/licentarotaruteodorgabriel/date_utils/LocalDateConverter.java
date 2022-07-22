package com.example.licentarotaruteodorgabriel.date_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter {

    private static final String STANDARD_DATE_FORMAT = "dd/MM/yyyy";
    private static final String WEEK_DAY_DATE_FORMAT = "dd MMM yyyy\nEEEE";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate getStandardFormatDateFromString(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(STANDARD_DATE_FORMAT));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getStandardFormatStringFromDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return  DateTimeFormatter.ofPattern(STANDARD_DATE_FORMAT).format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getWeekDayFormatStringFromDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return  DateTimeFormatter.ofPattern(WEEK_DAY_DATE_FORMAT).format(date);
    }

}
