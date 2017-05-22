package com.github.komarnicki.thomas.ringtimer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.service.notification.TimerServiceBinder
import com.github.komarnicki.thomas.ringtimer.timerlist.TimerListActivity

val ONGOING_NOTIFICATION_ID: Int = 89

class TimerService : Service() {

    private var binder: TimerServiceBinder = TimerServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TimerService", "Started Service")
        showForegroundNotification(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showForegroundNotification(intent: Intent?){
        val notificationIntent = Intent(this, TimerListActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val timer = intent!!.getParcelableExtra<Timer>("timer");

        var contentView = RemoteViews(packageName, R.layout.notification_simple);
        val bundle = Bundle()
        bundle.putParcelable("timer", timer)

//        contentView.setBundle(R.id.notification_timer_control, "setBundle", bundle)

        var builder = NotificationCompat.Builder(this)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContent(contentView)
        builder.setContentIntent(pendingIntent)

        val notification = builder.build();


        startForeground(ONGOING_NOTIFICATION_ID, notification)

        Log.d("TimerService", "Started foreground")
    }
}
