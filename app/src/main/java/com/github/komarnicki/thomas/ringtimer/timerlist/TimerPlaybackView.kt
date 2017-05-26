package com.github.komarnicki.thomas.ringtimer.timerlist

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class TimerPlaybackView : FrameLayout {

    private val tag = "TimerPlaybackView"

    private var playPauseButton: ImageButton? = null
    private var timeText: TextView? = null
    private var playing = true
    private val playIcon = R.drawable.ic_play_arrow_black_24dp
    private val pauseIcon = R.drawable.ic_pause_black_24dp


    var playPauseObservable: Observable<Boolean> = Observable.create<Boolean> { o ->
        Log.d(tag, "playPauseObservable subscribed to")
        playPauseButton?.setOnClickListener {
            playing = !playing
            playPauseButton?.setImageResource(if(playing) pauseIcon else playIcon)
            o.onNext(playing)
        }
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {

    }

    constructor(context: Context) : super(context) {

    }

    init {
        val view = View.inflate(context, R.layout.view_timer_playback, this)
        playPauseButton = view.findViewById(R.id.view_timer_playback_play_pause) as ImageButton
        timeText = view.findViewById(R.id.view_timer_playback_time) as TextView
        playPauseButton?.setImageResource(pauseIcon)
    }

    fun setTimerProgressObservable(timerProgressObservable: Observable<TimerProgressUpdate>?){
        timerProgressObservable?.subscribe(timerProgressObserver)
        Log.d(tag, "timer progress subscribed to")
    }

    private val timerProgressObserver = object : Observer<TimerProgressUpdate>{
        override fun onSubscribe(d: Disposable?) {
            Log.d(tag, "onSubscribe")
        }

        override fun onComplete() {
            Log.d(tag, "onComplete()")

        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
        }


        override fun onNext(t: TimerProgressUpdate) {
            Log.d(tag, "onNext()")
            timeText?.text = t.elapsedTime

        }
    }

}