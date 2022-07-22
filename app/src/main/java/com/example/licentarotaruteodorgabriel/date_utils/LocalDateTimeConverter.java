package com.example.licentarotaruteodorgabriel.date_utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime getDateFromString(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } catch (Exception e) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getStringFromDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(DATE_FORMAT).format(date);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getStringFromDate(Long date) {
        try {
            return getStringFromDate(getLocalDateTimeFromLong(date));
        } catch (Exception e) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static LocalDateTime getLocalDateTimeFromLong(Long date) {
        try {
            return Instant.ofEpochSecond(date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }


}
