package com.mitra.utils

import android.graphics.drawable.Drawable
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.mitra.view.ThrottleFirstClickListener
import org.json.JSONObject
import java.util.*


public fun TextView.setCompoundDrawablesExt(
    left: Drawable? = null,
    top: Drawable? = null,
    right: Drawable? = null,
    bottom: Drawable? = null
) {
    this.setCompoundDrawables(left, top, right, bottom)
}

fun JSONObject.getLongExt(name: String, applyValue: (Long) -> Unit) {
    if (this.has(name)) {
        applyValue(this.getLong(name))
    }
}

fun JSONObject.getStringExt(name: String, applyValue: (String) -> Unit) {
    if (this.has(name)) {
        applyValue(this.getString(name))
    }
}

fun JSONObject.getIntegerExt(name: String, applyValue: (Int) -> Unit) {
    if (this.has(name)) {
        applyValue(this.getInt(name))
    }
}

fun JSONObject.getBooleanExt(name: String, applyValue: (Boolean) -> Unit) {
    if (this.has(name)) {
        applyValue(this.getBoolean(name))
    }
}

fun String.toHtml(): Spanned? {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun String.trimNewLines(): String {
    val lines = this.split("\n")
    return if (lines.size > 1) {
        val reversedLines = lines.reversed()
        val beginIndex = lines.indexOfFirst { it.isNotBlank() }
        val endIndex = lines.size - reversedLines.indexOfFirst { it.isNotBlank() }
        lines.subList(beginIndex, endIndex).joinToString("\n")
    } else {
        lines[0]
    }
}

fun CharSequence.trimNewLines(): String {
    return this.toString().trimNewLines()
}

fun Long.getStringTime(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return "${if (hour > 9) hour else "0$hour"}:" +
            "${if (minute > 9) minute else "0$minute"}"
}

fun Long.isNextMinutes(timestamp: Long): Boolean {

    val oldC = Calendar.getInstance()
    oldC.timeInMillis = this
    val newC = Calendar.getInstance()
    newC.timeInMillis = timestamp

    return if (newC.get(Calendar.MONTH) == oldC.get(Calendar.MONTH)) {
        if (newC.get(Calendar.DAY_OF_MONTH) == oldC.get(Calendar.DAY_OF_MONTH)) {
            when {
                newC.get(Calendar.HOUR_OF_DAY) > oldC.get(Calendar.HOUR_OF_DAY) -> {
                    true
                }
                newC.get(Calendar.HOUR_OF_DAY) == oldC.get(Calendar.HOUR_OF_DAY) -> {
                    newC.get(Calendar.MINUTE) > oldC.get(Calendar.MINUTE)
                }
                else -> {
                    false
                }
            }
        } else {
            true
        }
    } else {
        true
    }
}

fun View.setOnClickThrottleListener(listener: (View?) -> Unit) {
    this.setOnClickListener(ThrottleFirstClickListener(listener))
}

fun View.dp(value: Int) =
    TypedValue
        .applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics).toInt()
