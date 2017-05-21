package com.github.komarnicki.thomas.ringtimer

import android.app.Application
import android.arch.persistence.room.Room
import com.github.komarnicki.thomas.ringtimer.model.TimerDatabase
import com.github.komarnicki.thomas.ringtimer.model.TimerDatabaseObject

class RingTimerApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        TimerDatabaseObject.timerDatabase = Room.databaseBuilder(this, TimerDatabase::class.java, "timer-database").build()
    }
}