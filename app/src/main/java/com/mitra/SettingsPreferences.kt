package com.mitra

import android.content.Context
import android.content.SharedPreferences

class SettingsPreferences(application: Context) {

    private val preferences: SharedPreferences =
        application.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    fun getVibration(): Boolean {
        return preferences.getBoolean(VIBRATION, true)
    }

    fun getSound(): Boolean {
        return preferences.getBoolean(SOUND, true)
    }

    fun setVibration(value: Boolean) {
        preferences.edit().apply {
            this.putBoolean(VIBRATION, value)
            apply()
        }
    }

    fun setSound(value: Boolean) {
        preferences.edit().apply {
            this.putBoolean(SOUND, value)
            apply()
        }
    }

    companion object {
        const val VIBRATION = "vibration"
        const val SOUND = "sound"
    }
}