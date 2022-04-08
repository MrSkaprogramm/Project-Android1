package com.mitra.ui.screens.chat

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mitra.ChatItem
import com.mitra.NotificationController
import com.mitra.Preferences
import com.mitra.data.*
import com.mitra.di.ProviderAndroidId
import com.mitra.di.ProviderIsRunningActivity
import com.mitra.di.inject
import com.mitra.network.SocketClient
import com.mitra.utils.trimNewLines
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.context.KoinContextHandler
import kotlin.coroutines.CoroutineContext

class ChatViewModel(
    private val socketClient: SocketClient,
    private val db: AppDatabase,
    private val userPrefrence: Preferences
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val message: MutableLiveData<List<ChatItem>> = MutableLiveData()
    private val leaveRoomLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val leaveHostRoomLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val typingMessageLiveData: MutableLiveData<Typing> = MutableLiveData()
    private val banUser: MutableLiveData<Boolean> = MutableLiveData()
    private val updateRoomData: MutableLiveData<Room> = MutableLiveData()
    private val reportUser: MutableLiveData<Boolean> = MutableLiveData()
    var roomId: String = ""
    private val provider = KoinContextHandler.get().inject<ProviderAndroidId>().value
    private val converter = ChatItemConverter(provider.get())
    val newMessageForNotification: MutableLiveData<Message> = MutableLiveData()
    var user: User = userPrefrence.getUser()

    init {
        if (!socketClient.isConnected()) {
            socketClient.connect()

            socketClient.init(provider.get())
            socketClient.initResponseServer = {

            }
        }

        initSocketListeners()

        socketClient.connectListenerUi = {
            socketClient.init(provider.get())
            socketClient.initResponseServer = {

            }

            initSocketListeners()
        }
    }

    private fun initSocketListeners() {
        socketClient.connectCompanionOnline = {
            socketClient.connectToRoom()
            updateRoomData.value = it
        }
        socketClient.connectCompanionOffline = {
            updateRoomData.value = it
        }

        socketClient.banCurrentUserListener = {
            banUser.value = it
        }
        socketClient.reportCurrentUserListener = {
            reportUser.value = it
        }
        socketClient.leaveRoomListener = {
            leaveRoomLiveData.value = it
        }
        socketClient.messageListener = { msg ->
            if (inject<ProviderIsRunningActivity>()?.get() == false) {

                inject<Context>()?.let {
                    NotificationController.showNotification(
                        msg.second,
                        msg.first.message,
                        it
                    )
                }
            }
            launch {
                val list = withContext(Dispatchers.IO) {
                    db.messageDao().getAll()
                }
                message.value = converter.convert(list)
            }
        }
        socketClient.typingMessageListener = {
            typingMessageLiveData.value = it
            Log.d("typing_chat", "${it.typing}")
        }
        socketClient.leaveHostRoomListener = {
            leaveHostRoomLiveData.value = it
        }
        if (userPrefrence.getUser().companionId.isNotEmpty()) {
            socketClient.continueChatCompanion(userPrefrence.getUser().companionId)
        }
    }

    fun reconnect() {
        if (!socketClient.isConnected()) {
            socketClient.connect()

            socketClient.init(provider.get())
            socketClient.initResponseServer = {

            }
        }

        if (userPrefrence.getUser().companionId.isNotEmpty()) {
            socketClient.continueChatCompanion(userPrefrence.getUser().companionId)
        }
    }

    fun getCacheMessages(listener: (List<ChatItem>) -> Unit) {
        launch {
            val list = withContext(Dispatchers.IO) {
                db.messageDao().getAll()
            }

            listener.invoke(converter.convert(list))
        }
    }

    fun subscribeBanUser(): LiveData<Boolean> = banUser

    fun subscribeReportUser(): LiveData<Boolean> = reportUser

    fun subscribeMessage(): LiveData<List<ChatItem>> = message

    fun subscribeTypingLiveData(): LiveData<Typing> = typingMessageLiveData

    fun subscribeLeaveRoom(): LiveData<Boolean> = leaveRoomLiveData

    fun subscribeLeaveHostRoom(): LiveData<Boolean> = leaveHostRoomLiveData

    fun subscribeUpdateRoomData(): LiveData<Room> = updateRoomData

    fun typingMessage(typing: Typing) {
        socketClient.typingMessage(typing)
    }


    fun connectToRoom() {
        socketClient.connectToRoom()
    }

    fun getRoom(): Room? {
        return socketClient.companionData
    }

    fun sendMessage(message: String) {
        if (message.isNotBlank()) {
            socketClient.sendMessage(message.trimNewLines(), user.name)
        }
    }

    override fun onCleared() {
        super.onCleared()

        socketClient.leaveRoomListener = null
        socketClient.messageListener = null
        socketClient.typingMessageListener = null
        socketClient.leaveHostRoomListener = null
        socketClient.banCurrentUserListener = null
        socketClient.reportCurrentUserListener = null
    }
}