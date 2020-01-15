package ru.wsr.stemobile.database;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class Substitution {

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "uid")
    private int mID;

    @NotNull
    @ColumnInfo(name = "date")
    private Date mDate;

    @NotNull
    @ColumnInfo(name = "group")
    private int mGroup;

    @NotNull
    @ColumnInfo(name = "pair")
    private int mPair;

    @NotNull
    @ColumnInfo(name = "cabinet")
    private String mCabinet;

    @NotNull
    @ColumnInfo(name = "teacher")
    private String mTeacher;

    @NotNull
    @ColumnInfo(name = "subject")
    private String mSubject;

    public Substitution(String teacher, String subject, int group, int pair, Date date) {
        mTeacher = teacher;
        mSubject = subject;
        mGroup = group;
        mPair = pair;
        mDate = date;
    }

    public void setTeacher(String teacher) {
        mTeacher = teacher;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public void setGroup(int group) {
        mGroup = group;
    }

    public void setPair(int pair) {
        mPair = pair;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTeacher() {
        return mTeacher;
    }

    public String getSubject() {
        return mSubject;
    }

    public int getGroup() {
        return mGroup;
    }

    public int getPair() {
        return mPair;
    }

    public Date getDate() {
        return mDate;
    }
}
