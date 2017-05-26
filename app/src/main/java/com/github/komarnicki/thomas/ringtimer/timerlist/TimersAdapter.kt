package com.github.komarnicki.thomas.ringtimer.timerlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class TimersAdapter(var timers: List<Timer>, var timerClickListener: TimerClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TIMER_ROW = 0
    private val TIMER_PLAYBACK = 1

    var timerPlaybackSubject: PublishSubject<Boolean>? = null
    var runningTimer: Timer? = null
    var runningTimerPos: Int = 0

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if(viewHolder is VH) {
            val t = timers[position]
            viewHolder.durationTime.text = displayDuration(t.duration)
            viewHolder.breakTime.text = displayDuration(t.breakTime)
            viewHolder.itemView.setOnClickListener {
                // todo connect to TimerPlaybackView
                timerPlaybackSubject = PublishSubject.create()

                timerClickListener.onTimerClicked(timers[position], it, timerPlaybackSubject!!)
            }
        }else if(viewHolder is TimerPlaybackViewHolder){
            viewHolder.timerPlaybackView.playPauseObservable.subscribe(timerPlaybackSubject)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TIMER_ROW) {
            return VH(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_timers, viewGroup, false))
        }else{
            return TimerPlaybackViewHolder(TimerPlaybackView(viewGroup.context))
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
        var timerPla ybackView : TimerPlaybackView = itemView.findViewById(0) as TimerPlaybackView
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
        fun onTimerClicked(timer: Timer, view: View, observer: Observable<Boolean>)
    }
}