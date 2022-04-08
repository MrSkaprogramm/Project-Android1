package com.mitra.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import com.mitra.R


class SwitchWithCustomStateView : FrameLayout {

    private var switchContainer: LinearLayout? = null
    private val statesView = mutableMapOf<Int, View>()
    private val states = mutableMapOf<Int, State<Any>>()
    var listener: ClickStateListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    )
            : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initView()
    }

    private fun initView() {
        val view = View.inflate(context,
            R.layout.view_switch_container, this)
        switchContainer = view.findViewById(R.id.container_switch)
    }

    private fun dropAllState() {
        statesView.forEach { pair -> pair.value.isSelected = false }
    }

    private fun chooseState(index: Int) {
        statesView[index]?.isSelected = true
    }

    fun chooseStateByIndex(id: Int) {
        val key = states.keys.toList()[id]
        dropAllState()
        chooseState(key)
    }

    private val clickListener = OnClickListener {
        if (states.containsKey(it.id)) {
            dropAllState()
            chooseState(it.id)
            val currentIndex = states.keys.toList().indexOf(it.id)
            listener?.onClickState(currentIndex, states[it.id]?.data)
        }
    }

    fun addState(state: State<Any>): SwitchWithCustomStateView {
        val stateContainer =
            LayoutInflater.from(context).inflate(R.layout.view_text_switch, this, false)
        val id = ViewCompat.generateViewId()
        stateContainer.id = id

        val textState = stateContainer.findViewById(R.id.text_state) as AppCompatTextView
        states[id] = state
        statesView[id] = stateContainer
        textState.text = state.name
        state.res?.let {
            textState.setCompoundDrawablesWithIntrinsicBounds(it, 0, 0, 0)
        }
        stateContainer.setOnClickListener(clickListener)
        switchContainer?.addView(
            stateContainer,
            LinearLayout
                .LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )
        )
        dropAllState()
        chooseState(states.keys.first())

        return this
    }
}

class State<T>(val name: String, val data: T, val res: Int? = null)

interface ClickStateListener {
    fun onClickState(index: Int, data: Any?)
}