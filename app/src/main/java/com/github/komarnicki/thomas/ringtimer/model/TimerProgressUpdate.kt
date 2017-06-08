package com.github.komarnicki.thomas.ringtimer.model

enum class TimerUpdateType {
    PROGRESS, PLAY, PAUSE, DONE, LOOP, ALMOST_DONE, BREAK, SWITCH_TIMER
}

data class TimerProgressUpdate(val progress: Int, val timer: Timer, val updateType: TimerUpdateType = TimerUpdateType.PROGRESS){

    var elapsedTime = "${progress/60}:${if(progress%60 < 10) "0" else ""}${progress%60}"

    var percent = (progress.toFloat() / (timer.duration + timer.breakTime).toFloat())

}

