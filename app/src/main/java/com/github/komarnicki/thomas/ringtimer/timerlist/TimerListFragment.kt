package com.github.komarnicki.thomas.ringtimer.timerlist

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.TimerService
import com.github.komarnicki.thomas.ringtimer.addtimer.AddTimerFragment
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerDatabaseObject

class TimerListFragment : LifecycleFragment(), TimersAdapter.TimerClickListener{


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_timer_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val recycler = view?.findViewById(R.id.timer_list_recycler) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(activity)

        val addTimerButton = view?.findViewById(R.id.add_timer_button) as FloatingActionButton
        addTimerButton.setOnClickListener({
            addTimer()
        })

        var adapter: TimersAdapter? = null

        val viewModel = ViewModelProviders.of(activity).get(TimersViewModel::class.java)

        viewModel.timers?.observe(this, Observer {
            Log.d("TimerListFragment","timers updated, count = ${it?.size}");
            if(adapter == null){
                adapter = TimersAdapter(it!!, this)
                recycler.adapter = adapter
            }else{
                adapter?.timers = it!!
                adapter?.notifyDataSetChanged()
            }
        })
    }

    fun addTimer(){
        if(lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)){
            activity.supportFragmentManager.beginTransaction()
                    .add(R.id.addTimerContainer, AddTimerFragment())
                    .addToBackStack("add_timer")
                    .commit()
        }
    }

    override fun onTimerClicked(timer: Timer, view: View) {
        val intent = Intent(activity, TimerService::class.java)
        intent.putExtra("timer", timer)
        activity.startService(intent)
    }

}