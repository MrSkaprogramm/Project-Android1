package com.mitra

import android.content.Context
import android.content.SharedPreferences

class FirstChatPreferences(application: Context) {

    private val preferences: SharedPreferences =
        application.getSharedPreferences("first_chat", Context.MODE_PRIVATE)

    fun getFirstChat() = preferences.getBoolean(FIRST_CHAT, true)

    fun setFirstChat(firstChat: Boolean) {
        preferences.edit().apply {
            putBoolean(FIRST_CHAT, firstChat)
            apply()
        }
    }

    companion object {
        const val FIRST_CHAT = "first_chat"
    }
}