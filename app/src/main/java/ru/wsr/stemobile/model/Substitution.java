package ru.wsr.stemobile.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import ru.wsr.stemobile.database.DateConverter;

@Entity(tableName = "substitutions_table")
public class Substitution {

    @PrimaryKey
    @ColumnInfo(name = "uid")
    private int mID;

    @NotNull
    @TypeConverters({DateConverter.class})
    private Date substitutionDate;

    private int group;

    private int pair;

    @NotNull
    private String cabinet;

    @NotNull
    private String teacher;

    @NotNull
    private String subject;

    public Substitution(@NotNull String teacher, @NotNull String subject,
                        int group, int pair, @NotNull Date substitutionDate, @NotNull String cabinet) {
        this.teacher = teacher;
        this.subject = subject;
        this.group = group;
        this.pair = pair;
        this.substitutionDate = substitutionDate;
        this.cabinet = cabinet;
    }

    @NotNull
    public String getTeacher() {
        return teacher;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }

    public int getGroup() {
        return group;
    }

    public int getPair() {
        return pair;
    }

    @NotNull
    public Date getSubstitutionDate() {
        return substitutionDate;
    }

    @NotNull
    public String getCabinet() {
        return cabinet;
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }
}