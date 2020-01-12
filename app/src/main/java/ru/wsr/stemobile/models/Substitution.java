package ru.wsr.stemobile.models;

import java.util.Date;

public class Substitution {
    private String mTeacher;
    private String mSubject;
    private int mGroup;
    private int mPair;
    private Date mDate;

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
