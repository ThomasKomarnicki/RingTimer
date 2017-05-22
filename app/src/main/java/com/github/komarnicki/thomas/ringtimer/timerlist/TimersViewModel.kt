package com.github.komarnicki.thomas.ringtimer.timerlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerDatabaseObject

class TimersViewModel : ViewModel() {

    private var _timers:  LiveData<List<Timer>>? = null
    var activeTimer: Timer? = null;


    var timers: LiveData<List<Timer>>? = null
        get() {
            if(_timers == null){
                loadTimers()
            }
            return _timers
        }

    private fun loadTimers() {
        _timers = TimerDatabaseObject.timerDatabase!!.timerDao().getTimers()

//        if(_timers!!.value == null || _timers!!.value?.size == 0){
//            Thread(Runnable {
//                TimerDatabaseObject.timerDatabase!!.timerDao().insert(Timer(180,30), Timer(120, 30), Timer(120, 60))
//            }).start()

//        }
    }

    fun addTimer(timer: Timer){
        Thread(Runnable {
            TimerDatabaseObject.timerDatabase!!.timerDao().insert(timer)
        }).start()
    }

}
