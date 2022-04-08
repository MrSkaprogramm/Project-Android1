package com.mitra

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.koin.core.context.KoinContextHandler

class SocketService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("chat: onStartCommand() connecting $this")
        KoinContextHandler.getOrNull()?.let {
            //socketServer.connect()
            println("chat: onStartCommand() connected $this")
        } ?: println("chat: onStartCommand() not connected $this")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}