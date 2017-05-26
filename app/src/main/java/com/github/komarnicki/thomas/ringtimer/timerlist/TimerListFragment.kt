package com.github.komarnicki.thomas.ringtimer.timerlist

import android.app.Service
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.service.TimerService
import com.github.komarnicki.thomas.ringtimer.addtimer.AddTimerFragment
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.service.TimerServiceBinder
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class TimerListFragment : LifecycleFragment(), TimersAdapter.TimerClickListener{

    private var binder: TimerServiceBinder? = null
//    private var disposable: Disposable? = null
    private var viewModel: TimersViewModel? = null

    private var adapter: TimersAdapter? = null

    private var playPauseObservable: Observable<Boolean>? = null
//    private var playPauseDisposable: Disposable? = null

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

        viewModel = ViewModelProviders.of(activity).get(TimersViewModel::class.java)

        viewModel?.timers?.observe(this, Observer {
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

    override fun onStart() {
        super.onStart()
        if(viewModel?.activeTimer != null){
            bindService(viewModel?.activeTimer)
        }
    }

    override fun onStop() {
        super.onStop()
        if(viewModel?.activeTimer != null){
            unbindService()
        }
    }

    override fun onTimerClicked(timer: Timer, view: View, observable: Observable<Boolean>) {
        viewModel?.activeTimer = timer
        playPauseObservable = observable

        val intent = Intent(activity, TimerService::class.java)
        intent.putExtra("timer", timer)
        activity.startService(intent)

        if(binder == null || binder?.isBinderAlive!!) {
            activity.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)
        }

    }

    private fun bindService(timer: Timer?){
        val intent = Intent(activity, TimerService::class.java)
        intent.putExtra("timer", timer)
        activity.bindService(intent,serviceConnection, Service.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        activity.unbindService(serviceConnection)
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder = service as TimerServiceBinder
//            disposable?.dispose()

            adapter?.progressObservable = binder!!.timerCountDown?.timerObservable
            adapter?.notifyItemChanged(adapter!!.runningTimerPos + 1)
            playPauseObservable?.subscribe(binder!!.timerCountDown?.running1)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
//            disposable?.dispose()
        }

    }
}
