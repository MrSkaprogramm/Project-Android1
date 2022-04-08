package com.mitra.ui.screens.banned_current_user

import androidx.lifecycle.ViewModel
import com.mitra.data.AppDatabase
import com.mitra.network.SocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.context.KoinContextHandler
import kotlin.coroutines.CoroutineContext

class BannedCurrentUserViewModel(val socketClient: SocketClient) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun leaveRoom() {
        launch {
            withContext(Dispatchers.IO) {
                val db = KoinContextHandler.getOrNull()?.inject<AppDatabase>()?.value
                db?.messageDao()?.deleteAllList(db.messageDao().getAll())
            }
        }
        socketClient.leaveRoom()
    }
}