package com.mitra.view

import android.view.View

class ThrottleFirstClickListener : View.OnClickListener {

    var delay: Int
    var startThrottleTime = 0L
    var clickListener: ((View?) -> Unit)? = null

    constructor() : this(DEFAULT_THROTTLE_TIME, null)
    constructor(clickListener: OnClickListener?)
            : this(DEFAULT_THROTTLE_TIME, { v -> clickListener?.onClick(v) })

    constructor(clickListener: ((View?) -> Unit)?) : this(DEFAULT_THROTTLE_TIME, clickListener)
    constructor(delay: Int, clickListener: ((View?) -> Unit)?) {
        this.delay = delay
        this.clickListener = clickListener
    }

    override fun onClick(v: View?) {
        val currentTime = System.currentTimeMillis()

        if ((currentTime - startThrottleTime) > delay) {
            startThrottleTime = currentTime
            clickListener?.invoke(v)
        }
    }

    companion object {
        const val DEFAULT_THROTTLE_TIME = 1000
    }

    interface OnClickListener {
        fun onClick(v: View?)
    }
}
