package com.mitra.di

import javax.inject.Provider

class ProviderAndroidId : Provider<String> {
    private var androidId: String = ""

    override fun get() = androidId

    fun set(id: String) {
        androidId = id
    }
}