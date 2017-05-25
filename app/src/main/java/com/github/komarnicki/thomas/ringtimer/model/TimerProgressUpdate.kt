package com.github.komarnicki.thomas.ringtimer.model


data class TimerProgressUpdate(val progress: Int, val timer: Timer){

    var elapsedTime = "${progress/60}:${progress%60}"

}

