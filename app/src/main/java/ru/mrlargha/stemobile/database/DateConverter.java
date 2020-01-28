package ru.mrlargha.stemobile.database;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public long fromSubstitutionDate(Date substitutionDate) {
        return substitutionDate.getTime();
    }

    @TypeConverter
    public Date toSubstitutionDate(long substitutionDate) {
        return new Date(substitutionDate);
    }
}
