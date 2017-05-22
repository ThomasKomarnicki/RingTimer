package com.github.komarnicki.thomas.ringtimer.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Timer implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int duration;

    private int breakTime;

    @Ignore
    public Timer(int duration, int breakTime) {
        this.duration = duration;
        this.breakTime = breakTime;
    }

    public Timer() {

    }

    protected Timer(Parcel in) {
        id = in.readInt();
        duration = in.readInt();
        breakTime = in.readInt();
    }

    public static final Creator<Timer> CREATOR = new Creator<Timer>() {
        @Override
        public Timer createFromParcel(Parcel in) {
            return new Timer(in);
        }

        @Override
        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(int breakTime) {
        this.breakTime = breakTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(duration);
        dest.writeInt(breakTime);
    }
}