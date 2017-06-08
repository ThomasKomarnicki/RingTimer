package com.github.komarnicki.thomas.ringtimer.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.PendingIntent
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerUpdateType
import com.github.komarnicki.thomas.ringtimer.service.notification.TimerControlView
import com.github.komarnicki.thomas.ringtimer.service.notification.TimerCountDown
import com.github.komarnicki.thomas.ringtimer.timerlist.TimerListActivity
import io.reactivex.disposables.Disposable


class TimerService : Service() {
    val ONGOING_NOTIFICATION_ID: Int = 89

    private var binder: TimerServiceBinder = TimerServiceBinder()
    private var disposable: Disposable? = null;
    private var timerControlView: TimerControlView? = null
//    private var contentView: RemoteViews? = null
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
            if(notificationManager == null) {
                notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            }
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

        soundPlayer = SoundPlayer(timer, this)

        val remoteViews = RemoteViews(packageName, R.layout.notification_simple)

        var builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContent(remoteViews)
        builder.setContentIntent(pendingIntent)
        builder.setDeleteIntent(stopPendingIntent)

        notification = builder.build()
        timerControlView = TimerControlView(remoteViews, {
            notificationManager?.notify(ONGOING_NOTIFICATION_ID, notification)
        })

        if(started) {
            binder.timerCountDown?.timer = timer

        }else {
            binder.timerCountDown = TimerCountDown(timer)
            disposable?.dispose()

            binder.timerCountDown!!.timerObservable.subscribe {

                if(it.updateType == TimerUpdateType.PLAY || it.updateType == TimerUpdateType.PAUSE){
                    val playing = it.updateType == TimerUpdateType.PLAY
                    Log.d("TimerService", "playing = $playing")
                    if (!playing) {
                        stopForeground(false)
                    } else {
                        startForeground(ONGOING_NOTIFICATION_ID, notification)
                    }
                    timerControlView?.setPlaying(playing)
                    notificationManager?.notify(ONGOING_NOTIFICATION_ID, notification)

                }else if(it.updateType == TimerUpdateType.PROGRESS){
                    timerControlView?.setProgress(it)
                    notificationManager?.notify(ONGOING_NOTIFICATION_ID, notification)

                }else if(it.updateType == TimerUpdateType.DONE){
                    timerControlView?.destroyNotification()
                    stopSelf()
                }else if(it.updateType == TimerUpdateType.ALMOST_DONE || it.updateType == TimerUpdateType.LOOP || it.updateType == TimerUpdateType.BREAK){
                    soundPlayer?.playSoundClip()
                }
            }
            started = true
        }
        binder.timerCountDown!!.restart()

        timerControlView?.configPlayPause(this, timer)
    }


}
