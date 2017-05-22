package com.github.komarnicki.thomas.ringtimer.service.notification

import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.concurrent.TimeUnit


class TimerCountDown(var timer:Timer) {

    private var observableEmitter: ObservableEmitter<TimerProgressUpdate>? = null
    private var timerProgress: Long = 0

    var timerObservable: Observable<TimerProgressUpdate> = Observable.intervalRange(0L,(timer.duration + timer.breakTime).toLong(), 0L, 1L, TimeUnit.SECONDS).map {
        TimerProgressUpdate(it.toInt(), timer)
    }



    fun start(){
        
    }

    fun pause(){

    }

    fun restart(){

    }

    fun stop(){

    }
}
