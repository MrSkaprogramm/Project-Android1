package com.mitra.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat
import com.mitra.R


class CustomSwitchPreferenceCompat: SwitchPreferenceCompat {
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?) : super(context)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder?.itemView?.let {
            val switch = findSwitchInChildviews(it as ViewGroup)

            switch?.let {
                it.thumbDrawable = null
                it.trackDrawable = null
                it.buttonDrawable = ContextCompat.getDrawable(context, R.drawable.selector_switcher)
            }
        }
    }


    private fun findSwitchInChildviews(view: ViewGroup): SwitchCompat? {
        for (i in 0 until view.childCount) {
            val thisChildview: View = view.getChildAt(i)
            if (thisChildview is SwitchCompat) {
                return thisChildview
            } else if (thisChildview is ViewGroup) {
                val theSwitch =
                    findSwitchInChildviews(thisChildview as ViewGroup)
                if (theSwitch != null) return theSwitch
            }
        }

        return null
    }
}