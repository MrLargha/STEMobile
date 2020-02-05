package ru.mrlargha.stemobile.tools;

import android.os.Parcel;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.Calendar;
import java.util.TimeZone;

public class STEDateValidator implements CalendarConstraints.DateValidator {

    private Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
    private long mStartDate;

    public static final Creator<STEDateValidator> CREATOR =
            new Creator<STEDateValidator>() {
                @Override
                public STEDateValidator createFromParcel(Parcel source) {
                    return new STEDateValidator(source.readLong());
                }

                @Override
                public STEDateValidator[] newArray(int size) {
                    return new STEDateValidator[size];
                }
            };

    public STEDateValidator(long startDate) {
        mStartDate = startDate;
    }

    @Override
    public boolean isValid(long date) {
        utc.setTimeInMillis(date);
        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Europe/Moscow"));
        start.setTimeInMillis(mStartDate);
        return utc.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY &&
                (start.get(Calendar.DAY_OF_YEAR) <= utc.get(Calendar.DAY_OF_YEAR)
                        && start.get(Calendar.YEAR) <= utc.get(Calendar.YEAR));

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mStartDate);
    }
}
