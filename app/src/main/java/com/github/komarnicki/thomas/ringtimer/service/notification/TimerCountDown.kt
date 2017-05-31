package com.github.komarnicki.thomas.ringtimer.service.notification

import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import com.github.komarnicki.thomas.ringtimer.model.TimerUpdateType
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


class TimerCountDown(var timer:Timer) {

    private var timerProgress: Long = 0
    private var lastProgress: Long = 0

    private val running1 = BehaviorSubject.create<Boolean>()

    private var playing = true

    private val breakTimeMillis = timer.duration * 1000
    private val loopTimeMillis = (timer.duration + timer.breakTime) * 1000
    private val almostDoneMillis: Int

    private var almostDone = false
    private var breakStarted = false

    init {
        if(timer.almostDone > 0) {
            almostDoneMillis = (timer.duration - timer.almostDone) * 1000
        }else{
            almostDoneMillis = Int.MAX_VALUE
        }
    }

    var timerObservable: BehaviorSubject<TimerProgressUpdate> = BehaviorSubject.create<TimerProgressUpdate>()
    private var help: Observable<TimerProgressUpdate> = running1.switchMap {
        if (it) Observable.interval(0L, 50L, TimeUnit.MILLISECONDS)
            .doOnNext {
                if(lastProgress != it){
                    timerProgress += 50
                }
                lastProgress = it
            } else
                Observable.never()
            }.doOnNext {
                if(!almostDone && timerProgress >= almostDoneMillis){
                    almostDone = true
                    timerObservable.onNext(TimerProgressUpdate(0, timer, TimerUpdateType.ALMOST_DONE))
                }
                if(!breakStarted && timerProgress >= breakTimeMillis){
                    breakStarted = true
                    timerObservable.onNext(TimerProgressUpdate(0, timer, TimerUpdateType.BREAK))
                }
                if(timerProgress > loopTimeMillis){
                    timerProgress = 0
                    almostDone = false
                    breakStarted = false
                    timerObservable.onNext(TimerProgressUpdate(0, timer, TimerUpdateType.LOOP))
                }
            }.map {
                var updateType = TimerUpdateType.PROGRESS

                TimerProgressUpdate(timerProgress.toInt()/1000, timer, updateType)
            }
            .takeUntil(running1.materialize().filter { it.isOnComplete })
            .doOnComplete {
                timerObservable.onComplete()
            }.observeOn(AndroidSchedulers.mainThread())

    init {
        help.subscribe {
            timerObservable.onNext(it)
        }
        timerObservable.subscribe {
            if(it.updateType == TimerUpdateType.PLAY){
                playing = true
                running1.onNext(playing)
            }else if(it.updateType == TimerUpdateType.PAUSE){
                playing = false
                running1.onNext(playing)
            }
        }

        timerObservable.onNext(TimerProgressUpdate(0,timer,TimerUpdateType.PLAY))
    }

    fun restart() {
        timerProgress = 0;
        running1.onNext(true)
        playing = true
    }

    fun stop() {
        timerObservable.onNext(timerObservable.value.copy(updateType = TimerUpdateType.DONE))
        running1.onComplete()
        playing = false
    }

    fun toggle() {
        playing = !playing
        if(playing){
            timerObservable.onNext(timerObservable.value.copy(updateType = TimerUpdateType.PLAY))
        }else{
            timerObservable.onNext(timerObservable.value.copy(updateType = TimerUpdateType.PAUSE))
        }
    }
}
