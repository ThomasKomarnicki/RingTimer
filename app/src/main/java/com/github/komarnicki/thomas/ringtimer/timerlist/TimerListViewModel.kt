package com.github.komarnicki.thomas.ringtimer.timerlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.komarnicki.thomas.ringtimer.model.Timer

class TimerListViewModel : ViewModel() {

    var _timers: MutableLiveData<List<Timer>>? = null

    fun getTimers(): LiveData<List<Timer>>{
        if(_timers == null){
            _timers = MutableLiveData<List<Timer>>()
            loadTimers()
        }
        return _timers as MutableLiveData<List<Timer>>
    }

    fun loadTimers() {
        _timers?.value = listOf(Timer(120, 30), Timer(180, 60), Timer(180, 30))
    }
}