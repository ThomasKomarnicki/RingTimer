package com.github.komarnicki.thomas.ringtimer.TimerList

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer

class TimersAdapter(var timers: List<Timer>) : RecyclerView.Adapter<TimersAdapter.VH>() {

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        val t = timers[position]
        viewHolder.durationTime.text = t.duration.toString()
        viewHolder.breakTime.text = t.breakTime.toString()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(viewGroup.context).inflate(R.layout.row_timers, viewGroup, false));
    }

    override fun getItemCount(): Int = timers.size


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var durationTime : TextView = itemView.findViewById(R.id.row_timer_duration) as TextView
        var breakTime : TextView = itemView.findViewById(R.id.row_timers_break) as TextView
    }
}