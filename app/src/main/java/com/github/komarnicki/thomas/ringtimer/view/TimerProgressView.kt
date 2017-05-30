package com.github.komarnicki.thomas.ringtimer.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate

class TimerProgressView : View {

    private val NANOS_IN_SECOND = 1_000_000_000L

    private enum class State{
        RUNNING, PAUSED
    }

    private var state = State.PAUSED
    private var nanoStart = 0L
    private var timerNanoDuration = 0L
    private var elapsedNano = 0L
    private var starting = false
    private var pauseNano = 0L

    private var paint: Paint = Paint()
    private var drawArea = Rect()

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    init {
        paint.color = Color.RED
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(timerNanoDuration == 0L){
            return // view essentially hasn't been initialized
        }

        if(state == State.RUNNING) {
            elapsedNano = (System.nanoTime() - nanoStart)
        }

        drawArea.set(0, 0, ((elapsedNano.toDouble() / timerNanoDuration.toDouble()) * width.toDouble()).toInt(), height)
        canvas.clipRect(drawArea, Region.Op.REPLACE)
        canvas.drawRect(drawArea, paint)

        if( state == State.RUNNING){
            invalidate()
        }
    }

    fun start(progressUpdate: TimerProgressUpdate){
        nanoStart = System.nanoTime()
        timerNanoDuration = progressUpdate.timer.totalSecondsDuration().toLong() * NANOS_IN_SECOND
        state = State.RUNNING
        starting = true
        invalidate()
    }

    fun resume(progressUpdate: TimerProgressUpdate){
        state = State.RUNNING
        nanoStart = nanoStart + System.nanoTime() - pauseNano
        invalidate()
    }

    fun pause(){
        pauseNano = System.nanoTime()
        state = State.PAUSED
    }

    fun reset(){
        elapsedNano = 0L
        pauseNano = 0L
        state = State.PAUSED
    }
}
