package ru.mrlargha.stemobile.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import ru.mrlargha.stemobile.database.DateConverter;

@Entity(tableName = "substitutions_table")
public class Substitution {

    public static final String STATUS_NOT_SYNCHRONIZED = "NOT_SYNCHRONIZED";
    public static final String STATUS_SYNCHRONIZED = "SYNCHRONIZED";
    public static final String STATUS_ERROR = "ERROR";


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int mID;

    @NotNull
    @TypeConverters({DateConverter.class})
    @Expose
    private Date substitutionDate;

    @Expose
    private int group;

    @NotNull
    private String status;

    @Expose
    private int pair;

    @Expose
    @NotNull
    private String cabinet;

    @Expose
    @NotNull
    private String teacher;

    @NotNull
    private String author;

    public Substitution(@NotNull String teacher, @NotNull String subject,
                        int group, int pair, @NotNull Date substitutionDate,
                        @NotNull String cabinet, @NotNull String status, @NotNull String author) {
        this.teacher = teacher;
        this.subject = subject;
        this.group = group;
        this.pair = pair;
        this.substitutionDate = substitutionDate;
        this.cabinet = cabinet;
        this.status = status;
        this.author = author;
    }

    @Expose
    @NotNull
    private String subject;

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

    public boolean equals(Substitution comparable) {
        return comparable.getPair() == pair && comparable.getSubstitutionDate().equals(substitutionDate)
                && comparable.getGroup() == group;
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    public String getAuthor() {
        return author;
    }
}
