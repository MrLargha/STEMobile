package ru.mrlargha.stemobile.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import ru.mrlargha.stemobile.database.DateConverter;

@Entity(tableName = "substitutions_table")
public class Substitution implements Parcelable {

    public static final String STATUS_NOT_SYNCHRONIZED = "NOT_SYNCHRONIZED";
    public static final String STATUS_SYNCHRONIZED = "SYNCHRONIZED";
    public static final String STATUS_ERROR = "ERROR";


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int mID;

    public static final Creator<Substitution> CREATOR = new Creator<Substitution>() {
        @Override
        public Substitution createFromParcel(Parcel in) {
            return new Substitution(in);
        }

        @Override
        public Substitution[] newArray(int size) {
            return new Substitution[size];
        }
    };
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

    public Substitution(@NotNull String teacher, @NotNull String subject,
                        int group, int pair, @NotNull Date substitutionDate,
                        @NotNull String cabinet, @NotNull String status) {
        this.teacher = teacher;
        this.subject = subject;
        this.group = group;
        this.pair = pair;
        this.substitutionDate = substitutionDate;
        this.cabinet = cabinet;
        this.status = status;
    }

    @Expose
    @NotNull
    private String subject;

    protected Substitution(Parcel in) {
        mID = in.readInt();
        group = in.readInt();
        pair = in.readInt();
        status = in.readString();
        cabinet = in.readString();
        teacher = in.readString();
        subject = in.readString();
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

    public String getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return mID;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mID);
    }
}
