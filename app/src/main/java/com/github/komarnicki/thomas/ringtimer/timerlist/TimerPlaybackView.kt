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
import com.github.komarnicki.thomas.ringtimer.model.TimerUpdateType
import com.github.komarnicki.thomas.ringtimer.view.TimerProgressView
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class TimerPlaybackView : FrameLayout {

    private val tag = "TimerPlaybackView"

    private var playPauseButton: ImageButton
    private var timeText: TextView
    private var progressView: TimerProgressView
    private var playing = true
    private val playIcon = R.drawable.ic_play_arrow_black_24dp
    private val pauseIcon = R.drawable.ic_pause_black_24dp

    private var disposable: Disposable? = null

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
        playPauseButton.setImageResource(pauseIcon)
        progressView = view.findViewById(R.id.view_timer_playback_view) as TimerProgressView
    }

    fun setTimerProgressObservable(timerProgressObservable: BehaviorSubject<TimerProgressUpdate>){
        disposable?.dispose()
        disposable = timerProgressObservable.subscribe{
            if(it.updateType == TimerUpdateType.PLAY){
                progressView.resume(it)
                playing = true
                configPlayPauseButton()
            }else if(it.updateType == TimerUpdateType.PAUSE){
                progressView.pause()
                playing = false
                configPlayPauseButton()
            }
            else if(it.updateType == TimerUpdateType.PROGRESS){
                timeText.text = it.elapsedTime
//                progressView?.progress = it.percent
//                progressView?.invalidate()
            }else if(it.updateType == TimerUpdateType.DONE){
                playing = false
                progressView.reset()
            }
            else if(it.updateType == TimerUpdateType.LOOP){
                progressView?.start(timerProgressObservable.value!!)
            }
        }

        val update = timerProgressObservable.value!!
        progressView.start(update)

        playPauseButton.setOnClickListener {
            playing = !playing
            configPlayPauseButton()
            val update = timerProgressObservable.value.copy(updateType = if(playing) TimerUpdateType.PLAY else TimerUpdateType.PAUSE)
            timerProgressObservable.onNext(update)
        }

        playing = true
        configPlayPauseButton()
    }

    private fun configPlayPauseButton(){
        playPauseButton.setImageResource(if(playing) pauseIcon else playIcon)
    }

}