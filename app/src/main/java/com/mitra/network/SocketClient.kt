package com.mitra.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.Gson
import com.mitra.BuildConfig
import com.mitra.data.*
import com.mitra.di.ProviderAndroidId
import com.mitra.di.inject
import com.mitra.utils.getBooleanExt
import com.mitra.utils.getIntegerExt
import com.mitra.utils.getLongExt
import com.mitra.utils.getStringExt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import kotlin.concurrent.timer
import kotlin.coroutines.CoroutineContext


class SocketClient(private val db: AppDatabase) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    var isNeedStartFinder: Boolean = false
    var isNeedShowReportOther = false
    var isNeedShowSuccessReport = false
    private val gson = Gson()
    private var socketId: String = ""
    private var socket: Socket? = null
    private val androidId: String? by lazy {
        inject<ProviderAndroidId>()?.get()
    }

    //информация о нахождении в комнате
    var companionData: Room? = null

    //таймер на поиск, необходимый для запуска периодичного опроса доступных пользователей

    var serverIsNotAvailable: (() -> Unit)? = null

    var disconnectListener: (() -> Unit)? = null

    var connectListenerUi: (() -> Unit)? = null

    var needAuthorizedListener: MutableList<(() -> Unit)> = mutableListOf()

    var connectCompanionOnline: ((Room) -> Unit)? = null

    var connectCompanionOffline: ((Room) -> Unit)? = null

    var roomListener: ((Room) -> Unit)? = null

    var responseUnreadedMessagesListener: ((List<Message>) -> Unit)? = null

    var leaveRoomListener: ((Boolean) -> Unit)? = null

    var leaveHostRoomListener: ((Boolean) -> Unit)? = null

    var messageListener: ((Pair<Message, String?>) -> Unit)? = null

    var typingMessageListener: ((Typing) -> Unit)? = null

    var responseUserListener: ((User) -> Unit)? = null

    var reportCurrentUserListener: ((Boolean) -> Unit)? = null

    var banCurrentUserListener: ((Boolean) -> Unit)? = null

    var initResponseServer: (() -> Unit)? = null

    var updateProgressFinder: ((Int) -> Unit)? = null

    var finishProgressFinder: (() -> Unit)? = null

    var countClientsListener: ((Int) -> Unit)? = null

    fun connect() {
        try {

            socket = IO.socket(URI.create(BuildConfig.WEB_SOCKET_BASE_URL))
            //"http://88.87.69.246:3000")) //мой комп
            //socket = IO.socket(URI.create("http://10.0.2.79:3000")) //на работе
            //socket = IO.socket(URI.create("http://10.0.2.16:3000")) //на ноуте
            //socket = IO.socket(URI.create("http://5.63.158.246:3000")) //реальный сервер
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            serverIsNotAvailable?.invoke()
        }

        socket?.connect()

        Handler(Looper.getMainLooper()).postDelayed({
            if (socket?.connected() == false) {
                serverIsNotAvailable?.invoke()
            }
        }, TIMEOUT)

        listen(
            EventsFromClient.FINISH_INIT_CONNECT,
            initResponse
        )

        listen(
            Socket.EVENT_ERROR,
            disconnect
        )
        /*listen(
            Socket.EVENT_CONNECT,
            connectListener
        )*/
        listen(
            Socket.EVENT_RECONNECT,
            connectListener
        )
        listen(
            EventsFromServer.REQUEST_SERVER_CONNECT_CLIENT_TO_ROOM,
            requestServerConnectClientToRoom
        )
        listen(
            EventsFromServer.REQUEST_SERVER_LEAVE_CLIENT_FROM_ROOM,
            requestServerLeaveClientToRoom
        )
        listen(
            EventsFromClient.LEAVE_CLIENT_FROM_HOST_ROOM,
            requireLeaveHostRoom
        )

        listen(
            EventsFromServer.SERVER_NOTIFY_UPDATE_ROOM,
            updateRoom
        )

        listen(
            EventsFromServer.SERVER_SEND_COUNTS_CLIENTS,
            serverSendCountClients
        )

        listen(
            EventsFromServer.SERVER_REPORT_COMPANION,
            serverRequestReportCurrentUser
        )
        listen(
            EventsFromServer.SERVER_BAN_COMPANION,
            serverRequestBanCurrentUser
        )

        listen(EventsFromServer.REQUEST_AUTHORIZED, requestAuthorized)
        listen(
            EventsFromClient.CONTINUE_CONNECT_ROOM_COMPANION_ONLINE,
            requestConnectCompanionOnline
        )
        listen(
            EventsFromClient.CONTINUE_CONNECT_ROOM_COMPANION_OFFLINE,
            requestConnectCompanionOffline
        )
        listen(EventsFromServer.SERVER_NEW_MESSAGE, serverNewMessage)

        listen(EventsFromClient.RESPONSE_CHECK_USER, requestServerCheckUser)
        listen(EventsFromClient.TYPING_MESSAGE, typingMessageSocketListener)
        listen(EventsFromClient.GET_UNREADED_MESSAGES, responseGetUnreadedMessages)
    }

    fun isConnected() = socket?.connected() == true

    fun disconnect() {
        if (socket?.connected() == true) {
            socket?.off()
            socket?.disconnect()?.close()
            socket = null
        }
    }

    fun continueChatCompanion(companionId: String) {
        socket?.emit(EventsFromClient.CHECK_COMPANION_ONLINE, JSONObject().apply {
            put("companionId", companionId)
        })
    }

    fun listen(event: String, listener: Emitter.Listener) {
        if (socket?.hasListeners(event) != true) {
            socket?.on(event, listener)
        }
    }

    fun findCompanion(max: Int) {
        val startTime = System.currentTimeMillis()
        val maxTime = 60000L
        val delay = maxTime / max
        var time = 0L
        var timeTry = 1
        timer = timer(period = delay, action = {
            if ((System.currentTimeMillis() - startTime) >= maxTime) {
                this.cancel()
                emitUi {
                    finishProgressFinder?.invoke()
                }
            }

            if (time != 0L && time % ((timeTry * 5000L / delay).toInt() * delay) == 0L) {
                socket?.emit(EventsFromClient.REQUEST_FIND_COMPANION, JSONObject().apply {
                    put("deviceId", androidId)
                })
                timeTry++
            }

            emitUi {
                updateProgressFinder?.invoke((time * max / maxTime).toInt())
            }

            time += delay
        })
    }

    fun cancelTimerFindCompnion() {
        socket?.emit(EventsFromClient.REQUEST_DONT_FIND_COMPANION)
        timer?.cancel()
    }

    fun updateFirebase(token: String) {
        socket?.emit(EventsFromClient.CLIENT_UPDATE_FIREBASE_TOKEN, JSONObject().apply {
            put("token", token)
        })
    }

    fun updateUserInfo(user: User) {
        socket?.emit(EventsFromClient.UPDATE_USER_INFO, JSONObject().apply {
            put("name", user.name)
            put("age", user.age)
            put("gender", user.gender)
            put("avatar", user.avatar)
            put("for_what", user.forWhat)
            put("show_me", user.showMe)
            put("new_version", true)
            put("age_min_companion", user.ageMinCompanion)
            put("age_max_companion", user.ageMaxCompanion)
        })
    }

    fun requestCountClientsOnline() {
        socket?.emit(EventsFromClient.CLIENT_GET_COUNT_CLIENTS)
    }

    fun sendMessage(message: String, name: String) {
        socket?.emit(EventsFromServer.SERVER_NEW_MESSAGE, JSONObject().apply {
            put("message", message)
            put("room", companionData?.room)
            put("senderDeviceId", androidId)
            put("companionDeviceId", companionData?.companionDeviceId)
            put("name", name)
        })
        Log.d("socket", "sendMessage ${this}")
    }

    fun connectToRoom() {
        socket?.emit(EventsFromClient.CONNECT_CLIENT_TO_ROOM, JSONObject().apply {
            put("room", companionData?.room)
            put("deviceId", companionData?.companionDeviceId)
        })
    }

    fun checkUser(deviceId: String) {
        socket?.emit(EventsFromClient.CHECK_USER, JSONObject().apply {
            put("deviceId", deviceId)
        })
    }

    fun reportChatCompanion() {
        socket?.emit(EventsFromClient.CLIENT_REPORT_COMPANION, JSONObject().apply {
            put("deviceId", companionData?.companionDeviceId)
            put("currentDeviceId", androidId)
            put("room", companionData?.room)
        })
    }

    fun licenseApprove() {
        socket?.emit(EventsFromClient.LICENSE_APPROVE)
    }

    fun getUnreadedMessages() {
        socket?.emit(EventsFromClient.GET_UNREADED_MESSAGES)
    }

    private val requestServerCheckUser = Emitter.Listener { args ->
        val data = args[0] as JSONObject

        val user = User()

        data.getStringExt("name") {
            user.name = it
        }

        data.getIntegerExt("gender") {
            user.gender = it
        }

        data.getIntegerExt("age") {
            user.age = it
        }

        data.getStringExt("firebaseId") {
            user.firebaseId = it
        }

        data.getStringExt("deviceId") {
            user.deviceId = it
        }

        data.getIntegerExt("avatar") {
            user.avatar = it
        }

        data.getIntegerExt("for_what") {
            user.forWhat = it
        }

        data.getIntegerExt("show_me") {
            user.showMe = it
        }

        data.getIntegerExt("age_min_companion") {
            user.ageMinCompanion = it
        }

        data.getIntegerExt("age_max_companion") {
            user.ageMaxCompanion = it
        }

        data.getIntegerExt("count_lock") {
            user.countLock = it
        }

        data.getIntegerExt("last_date_lock") {
            user.lastDateLock = it
        }

        data.getBooleanExt("block") {
            user.block = it
        }

        data.getBooleanExt("license_approve") {
            user.licenseApprove = it
        }

        data.getStringExt("companion_id") {
            user.companionId = it
        }

        data.getLongExt("last_report") {
            user.lastReport = it
        }

        val socketId = data.getString("socketId")

        Handler(Looper.getMainLooper()).post {
            responseUserListener?.invoke(user)
            this.socketId = socketId
        }
    }

    private val requestAuthorized = Emitter.Listener {
        emitUi {
            needAuthorizedListener.forEach {
                //it.invoke()
            }
            Log.d("uebskiysoket", "$this auth")
//            companionData = null
//            timer = null
//            connectCompanionOnline = null
//            connectCompanionOffline = null
//            roomListener = null
//            responseUnreadedMessagesListener = null
//            leaveRoomListener = null
//            messageListener = null
//            typingMessageListener = null
//            responseUserListener = null
        }
    }

    private val requestConnectCompanionOnline = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val roomId = data.getString("room")
        val name = data.getString("name")
        val age = data.getInt("age")
        val deviceId = data.getString("deviceId")
        val avatar = data.getInt("avatar")
        emitUi {
            companionData = Room(roomId, name, age, deviceId, avatar)
            companionData?.let {
                connectCompanionOnline?.invoke(it)
            }
        }
    }

    private val requestConnectCompanionOffline = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val roomId = data.getString("room")
        val name = data.getString("name")
        val age = data.getInt("age")
        val deviceId = data.getString("deviceId")
        val avatar = data.getInt("avatar")
        emitUi {
            companionData = Room(roomId, name, age, deviceId, avatar)
            companionData?.let {
                connectCompanionOffline?.invoke(it)
            }
        }

    }

    private val updateRoom = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val roomId = data.getString("room")
        val name = data.getString("name")
        val age = data.getInt("age")
        val deviceId = data.getString("deviceId")
        val avatar = data.getInt("avatar")
        emitUi {
            Room(roomId, name, age, deviceId, avatar).let {
                companionData = it
                roomListener?.invoke(it)
            }
        }
    }

    private val disconnect = Emitter.Listener {
        emitUi {
            disconnectListener?.invoke()
        }
    }

    private val connectListener = Emitter.Listener {
        emitUi {
            connectListenerUi?.invoke()
        }
    }

    private val serverSendCountClients = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val count = data.getInt("count")
        emitUi {
            countClientsListener?.invoke(count)
        }
    }

    private val requestServerConnectClientToRoom = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val roomId = data.getString("room")
        val name = data.getString("name")
        val age = data.getInt("age")
        val deviceId = data.getString("deviceId")
        val avatar = data.getInt("avatar")
        emitUi {
            Room(roomId, name, age, deviceId, avatar).let {
                companionData = it
                roomListener?.invoke(it)
            }
        }
    }


    private val requestServerLeaveClientToRoom = Emitter.Listener {
        emitUi {
            leaveRoomListener?.invoke(true)
        }
    }

    private val requireLeaveHostRoom = Emitter.Listener {
        emitUi {
            leaveHostRoomListener?.invoke(true)
        }
    }

    private val responseGetUnreadedMessages = Emitter.Listener { args ->
        val json = getJson(args)
        val messages: ListMessages = gson.fromJson(json, ListMessages::class.java)
        emitUi {
            responseUnreadedMessagesListener?.invoke(messages.messages)
        }
    }


    /**
     * Слушатель серверного запроса на отображение диалога репорта текущего пользователя
     **/
    private val serverRequestReportCurrentUser = Emitter.Listener {
        emitUi {
            reportCurrentUserListener?.invoke(true)
        }
    }

    /**
     * Слушатель серверного запроса на отображение диалога блокирования текущего пользователя
     **/
    private val serverRequestBanCurrentUser = Emitter.Listener {
        emitUi {
            banCurrentUserListener?.invoke(true)
        }
    }

    private val initResponse = Emitter.Listener {
        emitUi {
            initResponseServer?.invoke()
        }
    }

    private val typingMessageSocketListener = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val typing = data.getBoolean("typing")
        val deviceId = data.getString("deviceId")
        emitUi {
            typingMessageListener?.invoke(Typing(deviceId, typing))
        }
    }

    fun init(deviceId: String) {
        socket?.emit(EventsFromClient.INIT_CONNECT, JSONObject().apply {
            put("deviceId", deviceId)
        })
    }

    fun typingMessage(typing: Typing) {
        socket?.emit(EventsFromClient.TYPING_MESSAGE, JSONObject().apply {
            put("deviceId", typing.deviceId)
            put("typing", typing.typing)
            put("room", companionData?.room)
        })
    }

    private val serverNewMessage = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val message = data.getString("message")
        val deviceId = data.getString("senderDeviceId")
        val timestamp = data.getString("timestamp").toLong()
        val name = data.getString("name")
        val item = Message(
            message = message,
            deviceId = deviceId,
            timestamp = timestamp,
            _id = "0"
        )
        db.messageDao().insert(item)

        Handler(Looper.getMainLooper()).post {
            messageListener?.invoke(item to name)
        }
        Log.d("socket", "readMessage ${this}")
    }

    private fun emitUi(emitter: () -> Unit) {
        Handler(Looper.getMainLooper()).post {
            emitter.invoke()
        }
    }

    fun leaveRoom() {
        launch {
            withContext(Dispatchers.IO) {
                db.messageDao().deleteAllList(db.messageDao().getAll())
            }
        }

        socket?.emit(EventsFromClient.LEAVE_CLIENT_TO_ROOM, JSONObject().apply {
            put("room", companionData?.room)
        })
        companionData = null
    }

    fun runFinder() {
        this.isNeedStartFinder = true
    }

    private fun getJson(args: Array<Any>): String = (args[0] as JSONObject).toString()

    companion object {
        var timer: Timer? = null
        private const val SECOND = 1000L
        const val TIMEOUT = 30 * SECOND
    }

    private object EventsFromServer {
        const val SERVER_REPORT_COMPANION = "server_report_companion"
        const val SERVER_BAN_COMPANION = "server_ban_companion"

        const val SERVER_NOTIFY_UPDATE_ROOM = "server_notify_update_room"

        const val SERVER_SEND_COUNTS_CLIENTS = "server_send_counts_clients"

        const val REQUEST_SERVER_CONNECT_CLIENT_TO_ROOM = "request_connect_room"
        const val REQUEST_SERVER_LEAVE_CLIENT_FROM_ROOM = "request_leave_room"
        const val SERVER_NEW_MESSAGE = "new_message"
        const val REQUEST_AUTHORIZED = "request_authorized"
    }

    private object EventsFromClient {
        const val GET_UNREADED_MESSAGES = "get_unreaded_messages"
        const val TYPING_MESSAGE = "typing_message"
        const val REQUEST_FIND_COMPANION = "find_companion"
        const val REQUEST_DONT_FIND_COMPANION = "dont_find_companion"
        const val UPDATE_USER_INFO = "update_user_info"
        const val CONNECT_CLIENT_TO_ROOM = "connect_client_room"
        const val LEAVE_CLIENT_TO_ROOM = "leave_client_room"
        const val LEAVE_CLIENT_FROM_HOST_ROOM = "request_leave_host_room"
        const val CHECK_USER = "check_user"
        const val RESPONSE_CHECK_USER = "response_check_user"
        const val LICENSE_APPROVE = "license_approve"
        const val CHECK_COMPANION_ONLINE = "check_companion_online"
        const val CONTINUE_CONNECT_ROOM_COMPANION_ONLINE = "continue_connect_room_companion_online"
        const val CONTINUE_CONNECT_ROOM_COMPANION_OFFLINE =
            "continue_connect_room_companion_offline"
        const val CLIENT_REPORT_COMPANION = "client_report_companion"

        const val CLIENT_UPDATE_FIREBASE_TOKEN = "client_update_firebase_token"
        const val CLIENT_GET_COUNT_CLIENTS = "client_get_count_clients"

        const val INIT_CONNECT = "init_connect"
        const val FINISH_INIT_CONNECT = "init_connect"
    }

}