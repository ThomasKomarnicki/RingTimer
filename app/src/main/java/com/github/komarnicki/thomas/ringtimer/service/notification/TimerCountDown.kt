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

    var timerObservable: BehaviorSubject<TimerProgressUpdate> = BehaviorSubject.create<TimerProgressUpdate>()
    private var help: Observable<TimerProgressUpdate> = running1.switchMap {
        if (it) Observable.interval(0L, 1L, TimeUnit.SECONDS)
            .doOnNext {
                if(lastProgress != it){
                    timerProgress++
                }
                lastProgress = it
            } else
                Observable.never()
            }.doOnNext {
                if(timerProgress >= timer.duration + timer.breakTime){

                }
            }.map {
                TimerProgressUpdate(timerProgress.toInt(), timer)
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
