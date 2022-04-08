package com.mitra.ui.screens.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mitra.Preferences
import com.mitra.data.AvatarArray
import com.mitra.data.Room
import com.mitra.di.ProviderAndroidId
import com.mitra.di.inject
import com.mitra.network.SocketClient
import com.mitra.utils.AgeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class FilterViewModel(
    private val socketClient: SocketClient,
    private val preferences: Preferences
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    val avatarMutableLiveData: MutableLiveData<Int?> = MutableLiveData()
    val genderLiveData: MutableLiveData<Int> = MutableLiveData()
    val nameLiveData: MutableLiveData<String> = MutableLiveData()
    val ageLiveData: MutableLiveData<Int> = MutableLiveData()
    val showMeLiveData: MutableLiveData<Int> = MutableLiveData()
    val rangeAgeCompanionLiveData: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val startFindCompanion: MutableLiveData<Boolean> = MutableLiveData()
    val reInit: MutableLiveData<Unit> = MutableLiveData()

    private val room: MutableLiveData<Room> = MutableLiveData()
    private val countClients: MutableLiveData<Int> = MutableLiveData()

    val startChatCompanion: MutableLiveData<Boolean> = MutableLiveData()

    private var temporaryAgeMin = 0
    private var temporaryAgeMax = 0

    init {
        if (!socketClient.isConnected()) {
            socketClient.connect()
        }

        initSocketListeners()

        socketClient.connectListenerUi = {
            initSocketListeners()
        }

    }

    private fun initSocketListeners() {
        socketClient.initResponseServer = {
            reInit.value = Unit
        }
        socketClient.countClientsListener = { count ->
            countClients.value = count
        }
        socketClient.roomListener = { room ->
            this.room.value = room
        }
        socketClient.companionData?.let {
            startChatCompanion.value = true
        }
        inject<ProviderAndroidId>()?.get()?.let {
            socketClient.init(it)
        }
    }

    fun sendFilter(name: String, age: Int) {
        val user = preferences.getUser()
        preferences.setName(name)
        preferences.setAge(age)
        user.name = name
        user.age = age
        user.ageMinCompanion = AgeAdapter.getAge(user.ageMinCompanion)
        user.ageMaxCompanion = AgeAdapter.getAge(user.ageMaxCompanion)
        socketClient.updateUserInfo(user)
    }

    fun updateUserInfo() {
        val user = preferences.getUser()
        avatarMutableLiveData.value = AvatarArray().findAvatar(user.avatar)?.resourceId
        nameLiveData.value = user.name
        genderLiveData.value = user.gender
        ageLiveData.value = user.age
        showMeLiveData.value = user.showMe
        rangeAgeCompanionLiveData.value = user.ageMinCompanion to user.ageMaxCompanion
        temporaryAgeMin = user.ageMinCompanion
        temporaryAgeMax = user.ageMaxCompanion
        startFindCompanion.value = socketClient.isNeedStartFinder
        socketClient.isNeedStartFinder = false
    }

    fun subscribeCountClients(): LiveData<Int> = countClients

    fun setGender(gender: Int) {
        preferences.setGender(gender)
    }

    //May be will need it in the future
    /*fun setAvatar(avatar: Int) {
        preferences.setAvatar(avatar)
    }

    fun setName(name: String) {
        preferences.setName(name)
    }

    fun setAge(age: Int) {
        preferences.setAge(age)
    }*/

    fun setShowMe(showMe: Int) {
        preferences.setShowMe(showMe)
    }

    fun setAgeMin(ageMin: Int) {
        if (temporaryAgeMin != ageMin) {
            preferences.setAgeMin(ageMin)
        }
    }

    fun setAgeMax(ageMax: Int) {
        if (temporaryAgeMax != ageMax) {
            preferences.setAgeMax(ageMax)
        }
    }

    override fun onCleared() {
        super.onCleared()

        socketClient.countClientsListener = null
        socketClient.roomListener = null
    }

    fun getCountClients() {
        socketClient.requestCountClientsOnline()
    }
}