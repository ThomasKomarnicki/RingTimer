package com.github.komarnicki.thomas.ringtimer.addtimer

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.timerlist.TimersViewModel
import android.view.inputmethod.InputMethodManager


class AddTimerFragment : LifecycleFragment(){

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_add_timer, container, false);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val toolbar: Toolbar = view!!.findViewById(R.id.add_timer_toolbar) as Toolbar
        toolbar.setNavigationOnClickListener { activity.onBackPressed() }
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)

        val saveButton: Button = view!!.findViewById(R.id.add_timer_save) as Button;
        saveButton.setOnClickListener {
            addTimer()
        }
    }

    fun addTimer(){
        val breakTimeEt = (view?.findViewById(R.id.add_timer_break) as TimeInputView)
        val duration = (view?.findViewById(R.id.add_timer_duration) as TimeInputView).seconds()
        val breakTime = breakTimeEt.seconds()

        val timer = Timer(duration, breakTime)
        val viewModel = ViewModelProviders.of(activity).get(TimersViewModel::class.java)
        viewModel.addTimer(timer)

        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(breakTimeEt.windowToken, 0)

        activity.onBackPressed()
    }

}
