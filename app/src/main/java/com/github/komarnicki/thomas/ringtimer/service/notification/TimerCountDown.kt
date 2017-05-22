package com.github.komarnicki.thomas.ringtimer.service.notification

import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit


class TimerCountDown(var timer:Timer) {

//    private var observableEmitter: ObservableEmitter<TimerProgressUpdate>? = null
    private var timerProgress: Long = 0
    var running: Boolean = false

    var timerObservable: Observable<TimerProgressUpdate> = Observable.interval(0L, 1L, TimeUnit.SECONDS).doOnNext {
        timerProgress += it
    }.skipWhile {
        !running
    }.map {
        TimerProgressUpdate(it.toInt(), timer)
    }


    fun start(){
        running = true
    }

    fun pause(){
        running = false;
    }

    fun restart(){
        timerProgress = 0;
        running = true
    }

    fun stop(){
        running = false

    }
}
