package ru.mrlargha.stemobile.tools

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {
    fun dateToString(date: Date): String {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("dd.MM.yy")
        return dateFormat.format(date)
    }

    @Throws(ParseException::class)
    fun stringToDate(date: String): Date? {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("dd.MM.yy")
        return dateFormat.parse(date)
    }
}