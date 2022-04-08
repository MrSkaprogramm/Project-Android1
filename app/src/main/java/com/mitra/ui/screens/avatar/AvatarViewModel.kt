package com.mitra.ui.screens.avatar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mitra.Preferences
import com.mitra.data.AvatarArray
import com.mitra.data.AvatarList

class AvatarViewModel(private val preferences: Preferences): ViewModel() {

    private val avatars = AvatarArray()
    private val choosenAvatarId: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    private val listAvatars: MutableLiveData<MutableList<AvatarList>> =
        MutableLiveData()


    fun updateView() {
        val user = preferences.getUser()
        val index = avatars.findIndex(user.avatar)
        listAvatars.value = avatars.array
        choosenAvatarId.value = index
    }

    fun updateId(id: Int) {
        val user = preferences.getUser()
        user.avatar = id
        preferences.setUser(user)
        val index = avatars.findIndex(user.avatar)
        choosenAvatarId.value = index
    }

    fun subscribeChoosenAvatarId(): LiveData<Pair<Int, Int>> = choosenAvatarId

    fun subscribeListAvatars(): LiveData<MutableList<AvatarList>> = listAvatars
}