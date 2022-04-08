package com.mitra.ui.screens.reported_current_user

import androidx.lifecycle.ViewModel
import com.mitra.data.AppDatabase
import com.mitra.network.SocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ReportedCurrentUserViewModel(
    val socketClient: SocketClient,
    private val db: AppDatabase
) : ViewModel(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    fun leaveRoom() {
        launch {
            withContext(Dispatchers.IO) {
                db.messageDao().deleteAllList(db.messageDao().getAll())
            }
        }
    }
}