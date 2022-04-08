package com.mitra

import android.content.Context
import android.content.SharedPreferences

class NotificationChannelPreferences(application: Context) {

    private val preferences: SharedPreferences =
        application.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    fun getChannelId(): String {
        return preferences.getString(CHANNEL_ID, "") ?: ""
    }

    fun setChannelId(value: String) {
        preferences.edit().apply {
            this.putString(CHANNEL_ID, value)
            apply()
        }
    }

    companion object {
        const val CHANNEL_ID = "channel_id"
    }
}