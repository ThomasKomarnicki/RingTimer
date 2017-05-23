package com.github.komarnicki.thomas.ringtimer.addtimer

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.github.komarnicki.thomas.ringtimer.R

class TimeInputView : FrameLayout {

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
        var view = View.inflate(context, R.layout.time_input, this)
    }


}