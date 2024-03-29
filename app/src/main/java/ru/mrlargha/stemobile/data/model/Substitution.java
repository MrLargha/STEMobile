package ru.mrlargha.stemobile.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import ru.mrlargha.stemobile.database.DateConverter;

/**
 * Сущность замещения
 */
@Entity(tableName = "substitutions_table", indices = {@Index(value = {"group",
        "substitutionDate", "pair"}, unique = true)})
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
    private final Date substitutionDate;

    @Expose
    private final int group;

    @NotNull
    private String status;

    @Expose
    private final int pair;

    @Expose
    @NotNull
    private final String cabinet;

    @Expose
    @NotNull
    private final String teacher;

    @NotNull
    private final String author;
    @Expose
    @NotNull
    private final String subject;

    /**
     * Конструктор по-умолчанию
     *
     * @param teacher          преподаватель
     * @param subject          предмет
     * @param group            группа
     * @param pair             пара
     * @param substitutionDate дата замещения
     * @param cabinet          кабинет
     * @param status           статус
     * @param author           автор
     */
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

    public boolean fullEquals(Substitution comparable) {
        return comparable.getPair() == pair && comparable.getSubstitutionDate().equals(substitutionDate)
                && comparable.getGroup() == group && teacher.equals(comparable.teacher) &&
                subject.equals(comparable.subject) && cabinet.equals(comparable.cabinet)
                && author.equals(comparable.getAuthor());
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NotNull String status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }
}
