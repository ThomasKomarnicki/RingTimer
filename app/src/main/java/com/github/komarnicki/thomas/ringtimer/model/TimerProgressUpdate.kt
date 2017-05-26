package com.github.komarnicki.thomas.ringtimer.model

enum class TimerUpdateType {
    PROGRESS, PLAY, PAUSE, DONE
}

data class TimerProgressUpdate(val progress: Int, val timer: Timer, val updateType: TimerUpdateType = TimerUpdateType.PROGRESS){



    var elapsedTime = "${progress/60}:${progress%60}"

}

