package ru.mrlargha.stemobile.database

import androidx.room.TypeConverter
import java.util.*

// Type converters must be public
class DateConverter {
    @TypeConverter
    fun fromSubstitutionDate(substitutionDate: Date): Long {
        return substitutionDate.time
    }

    @TypeConverter
    fun toSubstitutionDate(substitutionDate: Long): Date {
        return Date(substitutionDate)
    }
}