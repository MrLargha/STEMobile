package ru.mrlargha.stemobile.tools;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public static String dateToString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd.MM.yy");
        return dateFormat.format(date);
    }

    public static Date stringToDate(String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd.MM.yy");
        return dateFormat.parse(date);
    }
}
