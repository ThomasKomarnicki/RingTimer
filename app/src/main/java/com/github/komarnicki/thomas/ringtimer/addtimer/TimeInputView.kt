package com.github.komarnicki.thomas.ringtimer.addtimer

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.github.komarnicki.thomas.ringtimer.R


class TimeInputView : LinearLayout {

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init(){
        orientation = VERTICAL
        val view = View.inflate(context, R.layout.time_pad_input, null) as ConstraintLayout
        addView(view)

        view.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){

            }
        }

        val timePadInputView = TimePadInputView(context)
        addView(timePadInputView)
        timePadInputView.onNumberClickListener = object : TimePadInputView.OnNumberClickListener{

            override fun onNumberClicked(number: Int) {

            }

        }
    }
}