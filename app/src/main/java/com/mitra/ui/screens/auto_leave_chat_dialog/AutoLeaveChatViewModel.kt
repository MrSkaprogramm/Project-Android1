package com.mitra.ui.screens.auto_leave_chat_dialog

import androidx.lifecycle.ViewModel
import com.mitra.FirstChatPreferences
import com.mitra.data.AppDatabase
import com.mitra.network.SocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.context.KoinContextHandler
import kotlin.coroutines.CoroutineContext

class AutoLeaveChatViewModel(
    private val socketClient: SocketClient,
    private val firstChatPreferences: FirstChatPreferences
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun checkFirstChat(): Boolean = firstChatPreferences.getFirstChat()

    fun setFirstChat(firstChat: Boolean) {
        firstChatPreferences.setFirstChat(firstChat)
    }

    fun leaveRoom() {
        launch {
            withContext(Dispatchers.IO) {
                val db = KoinContextHandler.getOrNull()?.inject<AppDatabase>()?.value
                db?.messageDao()?.deleteAllList(db.messageDao().getAll())
            }
        }

        socketClient.leaveRoom()
    }

    fun runFinder() {
        socketClient.runFinder()
    }
}