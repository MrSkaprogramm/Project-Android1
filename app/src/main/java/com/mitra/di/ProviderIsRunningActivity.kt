package com.mitra.di

import javax.inject.Provider

class ProviderIsRunningActivity : Provider<Boolean> {
    private var isRunningActivity: Boolean = false

    override fun get() = isRunningActivity

    fun set(id: Boolean) {
        isRunningActivity = id
    }
}