package com.github.komarnicki.thomas.ringtimer.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(Timer::class), version = 1)
abstract class TimerDatabase : RoomDatabase(){
    abstract fun timerDao(): TimerDao
}