package com.github.komarnicki.thomas.ringtimer.timerlist

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import com.github.komarnicki.thomas.ringtimer.model.TimerUpdateType
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class TimersAdapter(var timers: List<Timer>, var timerClickListener: TimerClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TIMER_ROW = 0
    private val TIMER_PLAYBACK = 1

    private var runningTimer: Timer? = null
    var runningTimerPos: Int = Int.MAX_VALUE

    var progressObservable: BehaviorSubject<TimerProgressUpdate>? = null
    private var disposable: Disposable? = null;
    private var switched = true

    private var timerPlaybackVH: TimerPlaybackViewHolder? = null

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if(viewHolder is VH) {
            val timerIndex = if(position <= runningTimerPos) position else position - 1
            val t = timers[timerIndex]
            viewHolder.durationTime.text = displayDuration(t.duration)
            viewHolder.breakTime.text = displayDuration(t.breakTime)
            viewHolder.itemView.setOnClickListener {
                if(progressObservable == null){
                    Log.d("TimersAdapter", "adding timer progress")
                    runningTimerPos = timerIndex
                    addTimerPlaybackRow(timerIndex, t, viewHolder.itemView)
                    notifyItemInserted(position + 1)

                }else {
                    if(runningTimer != null && runningTimerPos == timerIndex){
                        // do nothing?
                        Log.d("TimersAdapter", "clicked on timer already running")
                    }else{
                        Log.d("TimersAdapter", "adding new  progress")
                        removePlaybackRow(position)
                        val oldTimerIndex = timerIndex
                        runningTimerPos = timerIndex
                        addTimerPlaybackRow(timerIndex, t, viewHolder.itemView)
                        notifyItemMoved(oldTimerIndex+1, timerIndex+1)
                        notifyDataSetChanged()
                    }
                }
        }
        }else if(viewHolder is TimerPlaybackViewHolder){
            timerPlaybackVH = viewHolder
            
            if(progressObservable != null && progressObservable?.value != null) {
                if(switched) {
                    Log.d("TimerAdapter","set initial progress observable")
                    viewHolder.timerPlaybackView.setTimerProgressObservable(progressObservable!!)
                }
                disposable?.dispose()
                disposable = progressObservable?.subscribe {
                    if(it.updateType == TimerUpdateType.PLAY && switched){
                        viewHolder.timerPlaybackView.setTimerProgressObservable(progressObservable!!)
                        Log.d("TimerAdapter","set timer progress observable after switch: ${progressObservable!!.value}")
                        viewHolder.timerPlaybackView.visibility = View.VISIBLE
                        switched = false
                    }
                    else if(it.updateType == TimerUpdateType.SWITCH_TIMER){
                        viewHolder.timerPlaybackView.visibility = View.INVISIBLE
                        switched = true
                    }
                    if(it.updateType == TimerUpdateType.DONE){
                        Log.d("TimersAdapter", "DONE message received")
                        progressObservable = null
                        removePlaybackRow(position)
                        notifyItemRemoved(position)
                    }
                }
                viewHolder.itemView.setOnClickListener(null)
            }
        }
    }

    private fun removePlaybackRow(position: Int){
        runningTimer = null
        runningTimerPos = Int.MAX_VALUE
//        notifyItemRemoved(position)
    }

    private fun addTimerPlaybackRow(position: Int, timer: Timer, view: View){
        runningTimer = timer
        timerClickListener.onTimerClicked(timer, view)
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TIMER_ROW) {
            return VH(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_timers, viewGroup, false))
        }else{
            return TimerPlaybackViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_timer_playback, viewGroup, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(runningTimer != null && (position == (runningTimerPos + 1))){
            return TIMER_PLAYBACK
        }
        return TIMER_ROW
    }

    override fun getItemCount(): Int = timers.size + if(runningTimer != null) 1 else 0

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var durationTime : TextView = itemView.findViewById(R.id.row_timer_duration) as TextView
        var breakTime : TextView = itemView.findViewById(R.id.row_timers_break) as TextView
    }

    class TimerPlaybackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var timerPlaybackView : TimerPlaybackView = itemView.findViewById(R.id.row_timer_timer_playback) as TimerPlaybackView
    }

    fun displayDuration(seconds: Int): String {
        if(seconds < 60){
            return "$seconds sec"
        }
        val remainder = seconds % 60
        val minutes = seconds / 60

        if(remainder == 0){
            return "$minutes min"
        }else{
            return "$minutes:$remainder min"
        }
    }

    interface TimerClickListener{
        fun onTimerClicked(timer: Timer, view: View)
    }
}