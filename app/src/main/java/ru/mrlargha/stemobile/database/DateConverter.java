package ru.mrlargha.stemobile.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    long fromSubstitutionDate(Date substitutionDate) {
        return substitutionDate.getTime();
    }

    @TypeConverter
    Date toSubstitutionDate(long substitutionDate) {
        return new Date(substitutionDate);
    }
}
