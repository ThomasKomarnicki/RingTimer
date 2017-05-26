package com.github.komarnicki.thomas.ringtimer.timerlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.model.TimerProgressUpdate
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class TimersAdapter(var timers: List<Timer>, var timerClickListener: TimerClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TIMER_ROW = 0
    private val TIMER_PLAYBACK = 1

    private var timerPlaybackSubject: PublishSubject<Boolean>? = null
    private var runningTimer: Timer? = null
    var runningTimerPos: Int = Int.MAX_VALUE

    var progressObservable: Observable<TimerProgressUpdate>? = null

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if(viewHolder is VH) {
            val t = timers[if(position < runningTimerPos) position else position + 1]
            viewHolder.durationTime.text = displayDuration(t.duration)
            viewHolder.breakTime.text = displayDuration(t.breakTime)
            viewHolder.itemView.setOnClickListener {
                // todo connect to TimerPlaybackView
                runningTimerPos = position
                runningTimer = t
                timerPlaybackSubject = PublishSubject.create()
                timerClickListener.onTimerClicked(timers[position], it, timerPlaybackSubject!!)
                notifyItemInserted(position +1)
            }
        }else if(viewHolder is TimerPlaybackViewHolder){
            if(progressObservable != null) {
                viewHolder.timerPlaybackView.playPauseObservable.subscribe(timerPlaybackSubject)
//                viewHolder.timerPlaybackView.setTimerProgressObservable(progressObservable)
            }
        }
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
        fun onTimerClicked(timer: Timer, view: View, observer: Observable<Boolean>)
    }
}