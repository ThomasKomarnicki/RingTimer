package com.github.komarnicki.thomas.ringtimer.service.notification

import android.os.Binder


class TimerServiceBinder : Binder() {

    var timerCountDown: TimerCountDown? = null
}