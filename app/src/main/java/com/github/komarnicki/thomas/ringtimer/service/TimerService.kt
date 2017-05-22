package com.github.komarnicki.thomas.ringtimer.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import com.github.komarnicki.thomas.ringtimer.service.notification.TimerCountDown
import com.github.komarnicki.thomas.ringtimer.timerlist.TimerListActivity
import io.reactivex.Observer
import io.reactivex.disposables.Disposable


class TimerService : Service() {
    val ONGOING_NOTIFICATION_ID: Int = 89

    private var binder: TimerServiceBinder = TimerServiceBinder()
    private var disposable: Disposable? = null;
    private var contentView: RemoteViews? = null
    private var notificationManager: NotificationManager? = null
    private var notification : Notification? = null

    private var timerObserver: Observer<TimerProgressUpdate> = object : Observer<TimerProgressUpdate> {
        override fun onComplete() {
            Log.d("TimerService", "Observer completed")
            disposable?.dispose()
            disposable = null
        }

        override fun onSubscribe(d: Disposable) {
            disposable = d;
        }

        override fun onNext(t: TimerProgressUpdate) {
            Log.d("TimerService", "Got Progress Update ${t.progress}")
            contentView!!.setTextViewText(R.id.notification_time, t.progress.toString())
            notificationManager?.notify(ONGOING_NOTIFICATION_ID, notification)
        }

        override fun onError(e: Throwable) {

        }

    }

    override fun onBind(intent: Intent?): IBinder {
        return binder;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimerService", "Started Service")
        if(intent!!.hasExtra("timer")) {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            showForegroundNotification(intent)
        }else if(intent.hasExtra("pause_play")){
            Log.d("TimerService", "paused_play")
            if(binder.timerCountDown?.running!!) {
                binder.timerCountDown?.pause()
            }else{
                binder.timerCountDown?.start()
            }
            configPlayPause(contentView)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun showForegroundNotification(intent: Intent?){
        val notificationIntent = Intent(this, TimerListActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val timer = intent!!.getParcelableExtra<Timer>("timer");

        contentView = RemoteViews(packageName, R.layout.notification_simple)

        val bundle = Bundle()
        bundle.putParcelable("timer", timer)

        var builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContent(contentView)
        builder.setContentIntent(pendingIntent)

        notification = builder.build()

        Log.d("TimerService", "Started foreground")

        binder.timerCountDown = TimerCountDown(timer)
        disposable?.dispose()
        binder.timerCountDown!!.restart()
        binder.timerCountDown!!.timerObservable.subscribe(timerObserver)

        configPlayPause(contentView)

        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun configPlayPause(remoteViews: RemoteViews?){
        val intent = Intent(this, TimerService::class.java)
        intent.putExtra("pause_play", true)
        val pausePendingIntent = PendingIntent.getService(this, 0, intent, 0)
        remoteViews?.setOnClickPendingIntent(R.id.notification_play_pause, pausePendingIntent)
        notificationManager?.notify(ONGOING_NOTIFICATION_ID, notification)
    }
}
