package com.mitra.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.mitra.R

class MitraAlternatriveButton : FrameLayout {

    private val button: AppCompatButton by lazy { findViewById(R.id.button) }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        LayoutInflater.from(context).inflate(R.layout.view_alternative_button, this)
        attrs?.let {
            val styleAttrs = context.obtainStyledAttributes(
                it,
                R.styleable.MitraAlternatriveButton,
                0,
                0)
            try {
                val text = styleAttrs.getString(R.styleable.MitraAlternatriveButton_android_text)
                button.text = text
            } finally {
                styleAttrs.recycle()
            }
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        button.setOnClickListener(l)
    }

    fun setOnClickListener(l: (View) -> Unit) {
        button.setOnClickListener(l)
    }
}