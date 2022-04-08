package com.mitra.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.mitra.NotificationController
import com.mitra.SettingsPreferences

class SettingsViewModel(private val settingsPreferences: SettingsPreferences): ViewModel() {

    fun getVibration() = settingsPreferences.getVibration()

    fun getSound() = settingsPreferences.getSound()

    fun setVibration(value: Boolean, context: Context) {
        settingsPreferences.setVibration(value)
        NotificationController.createNewNotificationChannel(context)
    }

    fun setSound(value: Boolean, context: Context) {
        settingsPreferences.setSound(value)
        NotificationController.createNewNotificationChannel(context)
    }
}