package com.github.komarnicki.thomas.ringtimer.service.notification

import android.util.Log
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


class TimerCountDown(var timer:Timer) {

    private var timerProgress: Long = 0

    val running1 = BehaviorSubject.create<Boolean>()

    var timerObservable: Observable<TimerProgressUpdate> = running1.switchMap { if (it) Observable.interval(0L, 1L, TimeUnit.SECONDS) else Observable.never()
        }.doOnNext {
            timerProgress++
        }.map {
            TimerProgressUpdate(timerProgress.toInt(), timer)
        }
        .takeUntil(running1.materialize().filter { it.isOnComplete })
        .share()

    fun restart() {
        Log.d("TimerService","Restarted timer")
        timerProgress = 0;
        running1.onNext(true)
    }

    fun stop() {
        Log.d("TimerService","stopped timer")
        running1.onComplete()
    }

    fun toggle() {
        running1.onNext(!running1.value)
    }
}
