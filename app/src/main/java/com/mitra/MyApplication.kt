package com.mitra

import android.app.Application
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.mitra.di.dataModule
import com.mitra.di.networkModule
import com.mitra.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("application", "create")

        val analitycs = FirebaseAnalytics.getInstance(this)
        analitycs.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, null)

        stopKoin()
        startKoin {
            androidContext(applicationContext as MyApplication)
            modules(
                listOf(
                    networkModule,
                    viewModelsModule,
                    dataModule
                )
            )
        }
    }

    fun unloadModules() {
        stopKoin()
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d("application", "terminate")
    }
}