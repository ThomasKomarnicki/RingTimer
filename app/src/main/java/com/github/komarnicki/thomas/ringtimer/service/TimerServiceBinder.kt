package com.github.komarnicki.thomas.ringtimer.service

import android.os.Binder
import com.github.komarnicki.thomas.ringtimer.service.notification.TimerCountDown


class TimerServiceBinder : Binder() {

    var timerCountDown: TimerCountDown? = null
}