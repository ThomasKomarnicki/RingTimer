package com.github.komarnicki.thomas.ringtimer.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.widget.RemoteViews
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerUpdateType
import com.github.komarnicki.thomas.ringtimer.service.notification.TimerCountDown
import com.github.komarnicki.thomas.ringtimer.timerlist.TimerListActivity
import io.reactivex.disposables.Disposable


class TimerService : Service() {
    val ONGOING_NOTIFICATION_ID: Int = 89

    private val playIcon = R.drawable.ic_play_arrow_black_24dp
    private val pauseIcon = R.drawable.ic_pause_black_24dp

    private var binder: TimerServiceBinder = TimerServiceBinder()
    private var disposable: Disposable? = null;
    private var contentView: RemoteViews? = null
    private var notificationManager: NotificationManager? = null
    private var notification : Notification? = null
    private var soundPlayer: SoundPlayer? = null

    private var started = false

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null){
            return super.onStartCommand(intent, flags, startId)
        }
        if(intent.hasExtra("timer")) {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            showForegroundNotification(intent)
        }
        else if(intent.hasExtra("pause")){
            binder.timerCountDown!!.toggle()
        }
        else if(intent.hasExtra("stop")){
            stopForeground(true)
            binder.timerCountDown?.stop()
            disposable?.dispose()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showForegroundNotification(intent: Intent?){
        val notificationIntent = Intent(this, TimerListActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val stopIntent = Intent(this, TimerService::class.java)
        stopIntent.putExtra("stop", true)
        val stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0)

        val timer = intent!!.getParcelableExtra<Timer>("timer")

        if(timer.soundClipUri != null) {
            soundPlayer = SoundPlayer(timer, this)
        }

        contentView = RemoteViews(packageName, R.layout.notification_simple)

        var builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContent(contentView)
        builder.setContentIntent(pendingIntent)
        builder.setDeleteIntent(stopPendingIntent)

        notification = builder.build()

        if(started) {
            binder.timerCountDown?.timer = timer
        }else {
            binder.timerCountDown = TimerCountDown(timer)
            disposable?.dispose()

            binder.timerCountDown!!.timerObservable.subscribe {

                if(it.updateType == TimerUpdateType.PLAY || it.updateType == TimerUpdateType.PAUSE){
                    val playing = it.updateType == TimerUpdateType.PLAY
                    if (!playing) {
                        stopForeground(false)
                    } else {
                        startForeground(ONGOING_NOTIFICATION_ID, notification)
                    }
                    contentView?.setImageViewResource(R.id.notification_play_pause, if(playing) pauseIcon else playIcon)
                    notificationManager?.notify(ONGOING_NOTIFICATION_ID, notification)

                }else if(it.updateType == TimerUpdateType.PROGRESS){
                    contentView!!.setTextViewText(R.id.notification_time, it.progress.toString())
                    notificationManager?.notify(ONGOING_NOTIFICATION_ID, notification)

                }else if(it.updateType == TimerUpdateType.DONE){
                    stopSelf()
                }else if(it.updateType == TimerUpdateType.ALMOST_DONE || it.updateType == TimerUpdateType.LOOP || it.updateType == TimerUpdateType.BREAK){
                    soundPlayer?.playSoundClip()
                }
            }
            started = true
        }
        binder.timerCountDown!!.restart()

        configPlayPause(contentView)
    }

    private fun configPlayPause(remoteViews: RemoteViews?){
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra("pause", true)
        val pausePendingIntent = PendingIntent.getService(this, 4, intent, 0)
        remoteViews?.setOnClickPendingIntent(R.id.notification_play_pause, pausePendingIntent)
        remoteViews?.setImageViewResource(R.id.notification_play_pause, pauseIcon)
    }
}
