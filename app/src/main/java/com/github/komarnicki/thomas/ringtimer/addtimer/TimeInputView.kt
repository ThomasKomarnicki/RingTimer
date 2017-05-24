package com.github.komarnicki.thomas.ringtimer.addtimer

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.komarnicki.thomas.ringtimer.R


class TimeInputView : LinearLayout {

    private var numbers: ArrayList<Int> = ArrayList()

    private var minutesText: TextView? = null
    private var secondsText: TextView? = null

    private var timePadInputView: TimePadInputView? = null

    fun seconds(): Int {
        if (numbers.isEmpty())
            return 0
        else
            return numbers.reduceIndexed { index, acc, i ->
                var next: Int = 0
                if (index < 2)
                    next = i
                else
                    next = i * 60
                if(index % 2 == 1){
                    next *= 10
                }
                acc + next
            }
    }

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
        val view = View.inflate(context, R.layout.time_input, null) as ConstraintLayout
        addView(view)

        minutesText = view.findViewById(R.id.time_input_min_text) as TextView
        secondsText = view.findViewById(R.id.time_input_sec_text) as TextView

        view.setOnClickListener {

                timePadInputView?.visibility = View.VISIBLE

        }

        view.findViewById(R.id.time_input_delete).setOnClickListener {
            if(numbers.size > 0){
                numbers.removeAt(0)
            }

            displayTime()
        }

        timePadInputView = TimePadInputView(context)
        addView(timePadInputView)
        timePadInputView?.onNumberClickListener = object : TimePadInputView.OnNumberClickListener {
            override fun onNumberClicked(number: Int) {
                Log.d("TimeInputView", "number clicked $number")

                if(numbers.size < 4){
                    numbers.add(0, number)
                }
                displayTime()
            }
        }
        timePadInputView?.visibility = View.GONE
        displayTime()
    }

    private fun displayTime() {
        secondsText?.text = "${numberOrZero(1)}${numberOrZero(0)}"
        minutesText?.text = "${numberOrZero(3)}${numberOrZero(2)}"
        invalidate()
    }

    private fun numberOrZero(index:Int):Int = if(index < numbers.size) numbers[index] else 0

}