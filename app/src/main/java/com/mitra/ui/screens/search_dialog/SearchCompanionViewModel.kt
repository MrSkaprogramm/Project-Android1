package com.mitra.ui.screens.search_dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mitra.data.AppDatabase
import com.mitra.data.Room
import com.mitra.network.SocketClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SearchCompanionViewModel(
    private val socketClient: SocketClient,
    private val db: AppDatabase): ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val room: MutableLiveData<Room> = MutableLiveData()
    private val progress: MutableLiveData<Int> = MutableLiveData()
    private val finishProgress: MutableLiveData<Boolean> = MutableLiveData()

    init {
        socketClient.roomListener = {room ->
            this.room.value = room
        }

        socketClient.updateProgressFinder = { progress ->
            this.progress.value = progress
        }

        socketClient.finishProgressFinder = {
            this.finishProgress.value = true
        }
        launch {
            withContext(Dispatchers.IO) {
                db.messageDao().deleteAllList(db.messageDao().getAll())
            }
        }
    }

    fun findCompanion(max: Int) {
        socketClient.findCompanion(max)
    }

    fun cancelTimerFindCompanion() {
        socketClient.cancelTimerFindCompnion()
    }

    fun subscribeRoomConnect(): LiveData<Room> = room

    fun subscribeUpdateProgress(): LiveData<Int> = progress

    fun subscribeFinishProgress(): LiveData<Boolean> = finishProgress

    override fun onCleared() {
        super.onCleared()

        socketClient.finishProgressFinder = null
        socketClient.updateProgressFinder = null
        socketClient.roomListener = null
    }

}