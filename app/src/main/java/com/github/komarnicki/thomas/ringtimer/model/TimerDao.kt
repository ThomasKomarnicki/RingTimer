package com.github.komarnicki.thomas.ringtimer.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

@Dao
interface TimerDao {
    @Query("SELECT * FROM Timer")
    fun getTimers(): LiveData<List<Timer>>

    @Insert(onConflict = REPLACE)
    fun insert(vararg timer: Timer)

    @Update
    fun update(vararg timer: Timer)

    @Delete
    fun delete(timer: Timer)
}