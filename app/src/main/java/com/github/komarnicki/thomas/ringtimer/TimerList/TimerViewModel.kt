package com.github.komarnicki.thomas.ringtimer.TimerList

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.komarnicki.thomas.ringtimer.model.Timer
class TimerViewModel : ViewModel() {

    var _timers: LiveData<List<Timer>>? = null

    fun getTimers(): LiveData<List<Timer>>?{
        if(_timers == null){
            _timers = MutableLiveData<List<Timer>>()
            loadTimers()
        }
        return _timers;
    }

    fun loadTimers() {

    }
}