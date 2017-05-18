package com.github.komarnicki.thomas.ringtimer.TimerList

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.github.komarnicki.thomas.ringtimer.R

class TimerListActivity : LifecycleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_list)

        var viewModel = ViewModelProviders.of(this).get(TimerViewModel::class.java)
    }
}
