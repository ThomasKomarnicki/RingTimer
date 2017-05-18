package com.github.komarnicki.thomas.ringtimer.model

data class Timer(var duration: Int = 120, var breakTime: Int = 30) {


    fun getTotalDuration() : Int = duration + breakTime
}