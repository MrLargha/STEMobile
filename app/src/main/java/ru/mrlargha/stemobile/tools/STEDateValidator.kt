package ru.mrlargha.stemobile.tools

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import java.util.*

class STEDateValidator(private val mStartDate: Long) : DateValidator {
    private val utc = Calendar.getInstance()
    override fun isValid(date: Long): Boolean {
        utc.timeInMillis = date
        val start = Calendar.getInstance()
        start.timeInMillis = mStartDate
        return utc[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY &&
                (start[Calendar.DAY_OF_YEAR] <= utc[Calendar.DAY_OF_YEAR]
                        && start[Calendar.YEAR] <= utc[Calendar.YEAR])
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(mStartDate)
    }

    companion object {
        val CREATOR: Parcelable.Creator<STEDateValidator?> = object : Parcelable.Creator<STEDateValidator?> {
            override fun createFromParcel(source: Parcel): STEDateValidator? {
                return STEDateValidator(source.readLong())
            }

            override fun newArray(size: Int): Array<STEDateValidator?> {
                return arrayOfNulls(size)
            }
        }
    }

}