package com.github.komarnicki.thomas.ringtimer.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate

class TimerProgressView : ProgressBar {

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
    private val animationHeight: Int

    private var borderArea = Rect()
    private var lastDraw = 0L

    private var timer: Timer? = null
    private val breakPaint: Paint
    private var breakPos: Float = 0f
    private var warningPos: Float = -1F

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    init {
        animationHeight = (resources.displayMetrics.density * 5).toInt()
        max = 100
        breakPaint = Paint()
        breakPaint.strokeWidth = resources.displayMetrics.density * 5
        breakPaint.color = resources.getColor(R.color.colorAccent)
    }

    override fun onDraw(canvas: Canvas) {

        if(timerNanoDuration == 0L){
            return // view essentially hasn't been initialized
        }

        if(state == State.RUNNING) {
            elapsedNano = (System.nanoTime() - nanoStart)
        }


        var startAnimPercent = 1f

        if(starting){
            if(System.nanoTime() > nanoStart + NANOS_IN_SECOND){
                starting = false
            }else{
                startAnimPercent = 1 - (((nanoStart + NANOS_IN_SECOND) - System.nanoTime()).toDouble()/NANOS_IN_SECOND.toDouble()).toFloat()
            }
        }

        borderArea.set(0,0, (startAnimPercent * width).toInt(), height)
        canvas.clipRect(borderArea, Region.Op.REPLACE)

        progress = ((elapsedNano.toDouble() / timerNanoDuration.toDouble()) * width).toInt()

        super.onDraw(canvas)

        if(timer != null){
            canvas.drawLine(breakPos * width, 0f, breakPos * width, height.toFloat(), breakPaint)

            if(timer!!.warning > 0){
                canvas.drawLine(warningPos * width, 0f, warningPos * width, height.toFloat(), breakPaint)
            }
        }

        if( state == State.RUNNING){
            invalidate()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        max = MeasureSpec.getSize(widthMeasureSpec)
    }

    fun start(progressUpdate: TimerProgressUpdate){
        nanoStart = System.nanoTime()
        timerNanoDuration = progressUpdate.timer.totalSecondsDuration().toLong() * NANOS_IN_SECOND
        state = State.RUNNING
        starting = true
        invalidate()
        translationY = -(animationHeight).toFloat()
        timer = progressUpdate.timer
        breakPos = 1f - timer!!.breakTime.toFloat()/timer!!.totalSecondsDuration().toFloat()
        if(timer!!.warning > 0){
            warningPos = 1f - (timer!!.breakTime + timer!!.warning.toFloat())/timer!!.totalSecondsDuration().toFloat()
        }
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
