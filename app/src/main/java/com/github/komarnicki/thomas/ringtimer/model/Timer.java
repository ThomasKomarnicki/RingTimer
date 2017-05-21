package com.github.komarnicki.thomas.ringtimer.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Timer{

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
}