package com.github.komarnicki.thomas.ringtimer.addtimer

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.komarnicki.thomas.ringtimer.R

class TimePadInputView : FrameLayout {

    interface OnNumberClickListener{
        fun onNumberClicked(number: Int)
    }

    var onNumberClickListener: OnNumberClickListener? = null

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
        val view = View.inflate(context, R.layout.time_pad_input, this) as ViewGroup
        val constraintLayout = view.getChildAt(0) as ConstraintLayout;
        for (i in 0 until constraintLayout.childCount){
            constraintLayout.getChildAt(i).setOnClickListener {
                onNumberClickListener?.onNumberClicked(it.tag.toString().toInt())
            }
        }

    }

}