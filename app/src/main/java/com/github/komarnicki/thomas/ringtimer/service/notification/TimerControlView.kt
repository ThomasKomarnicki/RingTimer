package com.github.komarnicki.thomas.ringtimer.service.notification


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.RemoteViews
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import com.github.komarnicki.thomas.ringtimer.service.TimerService


class TimerControlView(val remoteViews: RemoteViews, val onUiUpdate:() -> Unit) {

    private val playIcon = R.drawable.ic_play_arrow_black_24dp
    private val pauseIcon = R.drawable.ic_pause_black_24dp

//    private val observable = Observable.interval(0L, 20L, TimeUnit.MILLISECONDS)

    fun setPlaying(playing: Boolean) {
        remoteViews.setImageViewResource(R.id.notification_play_pause, if(playing) pauseIcon else playIcon)
    }

    fun configPlayPause(context: Context, timer: Timer){
        val intent = Intent(context, TimerService::class.java)
        intent.putExtra("pause", true)
        val pausePendingIntent = PendingIntent.getService(context, 4, intent, 0)
        remoteViews.setOnClickPendingIntent(R.id.notification_play_pause, pausePendingIntent)
        remoteViews.setImageViewResource(R.id.notification_play_pause, pauseIcon)
        remoteViews.setInt(R.id.notification_progress, "setMax", timer.totalSecondsDuration())

        setTickBitmap(context, timer)

    }

    fun setProgress(update: TimerProgressUpdate) {
        remoteViews.setTextViewText(R.id.notification_time, update.progress.toString())
        remoteViews.setInt(R.id.notification_progress, "setProgress", update.progress)
    }

    fun destroyNotification(){

    }

    private fun setTickBitmap(context: Context, timer: Timer){
        val b = Bitmap.createBitmap(context.resources.displayMetrics.widthPixels, (context.resources.displayMetrics.density * 10).toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(b)
        val breakPaint = Paint()
        breakPaint.strokeWidth = context.resources.displayMetrics.density * 5
        breakPaint.color = context.resources.getColor(R.color.colorAccent)

        val breakPos = 1f - timer.breakTime.toFloat()/timer.totalSecondsDuration().toFloat()
        var warningPos = 0f
        if(timer.warning > 0){
            warningPos = 1f - (timer.breakTime + timer.warning.toFloat())/timer.totalSecondsDuration().toFloat()
        }

        canvas.drawLine(breakPos * b.width.toFloat(), 0f, breakPos * b.width.toFloat(), b.width.toFloat(), breakPaint)

        if(timer.warning > 0){
            canvas.drawLine(warningPos * b.width.toFloat(), 0f, warningPos * b.width.toFloat(), b.height.toFloat(), breakPaint)
        }

        remoteViews.setImageViewBitmap(R.id.notification_tick_image, b)
    }

}