package com.github.komarnicki.thomas.ringtimer.service.notification


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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

//        observable.subscribe {
//
//            onUiUpdate.invoke()
//        }
    }

    fun setProgress(update: TimerProgressUpdate) {
        remoteViews.setTextViewText(R.id.notification_time, update.progress.toString())
        remoteViews.setInt(R.id.notification_progress, "setProgress", update.progress)
    }

    fun destroyNotification(){

    }

}