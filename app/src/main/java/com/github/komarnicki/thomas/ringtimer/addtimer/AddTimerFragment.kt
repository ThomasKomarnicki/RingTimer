package com.github.komarnicki.thomas.ringtimer.addtimer

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.github.komarnicki.thomas.ringtimer.R
import com.github.komarnicki.thomas.ringtimer.model.Timer
import com.github.komarnicki.thomas.ringtimer.timerlist.TimersViewModel
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox


class AddTimerFragment : LifecycleFragment(){

//    val soundsList = listOf(
//            Pair("Beep","1"),
//            Pair("Ding","1"),
//            Pair("Ding Ding","1")
//    )

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_add_timer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val saveButton: Button = view!!.findViewById(R.id.add_timer_save) as Button;
        saveButton.setOnClickListener {
            addTimer()
        }

        (view?.findViewById(R.id.add_timer_duration) as TimeInputView).requestFocus()


//        val soundsSpinner = view?.findViewById(R.id.add_timer_sounds_spinner) as Spinner
//
//        val previewSoundButton = view?.findViewById(R.id.add_timer_preview_sound) as ImageButton
//        previewSoundButton.setOnClickListener {
//
//        }
    }

    fun addTimer(){
        val breakTimeEt = (view?.findViewById(R.id.add_timer_break) as TimeInputView)
        val duration = (view?.findViewById(R.id.add_timer_duration) as TimeInputView).seconds()
        val breakTime = breakTimeEt.seconds()

        val timer = Timer(duration, breakTime)
        val viewModel = ViewModelProviders.of(activity).get(TimersViewModel::class.java)

        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(breakTimeEt.windowToken, 0)

        val checkbox = view?.findViewById(R.id.add_timer_warning_check) as CheckBox
        if(checkbox.isChecked) {
            timer.warning = 30
        }
        viewModel.addTimer(timer)

        activity.onBackPressed()
    }

}
