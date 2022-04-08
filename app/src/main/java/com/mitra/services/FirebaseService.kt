package com.mitra.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mitra.ApiRetrofit
import com.mitra.NotificationController
import com.mitra.data.AppDatabase
import com.mitra.data.Message
import com.mitra.di.ProviderAndroidId
import com.mitra.di.inject
import com.mitra.network.EmptyEmitter
import com.mitra.network.NetworkRunner

class FirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        NotificationController.showNotification(
            message.data["name"],
            message.data["message"] ?: "",
            applicationContext
        )
        val db = inject<AppDatabase>()
        db?.messageDao()?.insert(
            Message(
                _id = message.data["_id"] ?: "",
                message = message.data["message"] ?: "",
                deviceId = message.data["deviceId"] ?: "",
                timestamp = message.data["timestamp"]?.toLong() ?: 0
            )
        )
    }

    override fun onNewToken(token: String) {
        NetworkRunner(EmptyEmitter) {
            inject<ProviderAndroidId>()?.get()?.let {
                inject<ApiRetrofit>()?.sendFirebaseToken(token, it)
            }
        }
    }
}