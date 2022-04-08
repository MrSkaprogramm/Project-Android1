package com.mitra.ui.screens.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mitra.ApiRetrofit
import com.mitra.Preferences
import com.mitra.data.AppDatabase
import com.mitra.data.User
import com.mitra.data.dtos.MessageResponse
import com.mitra.data.dtos.UserDto
import com.mitra.di.ProviderAndroidId
import com.mitra.di.inject
import com.mitra.network.EmitBuilder
import com.mitra.network.MainThreadEmitBuilder
import com.mitra.network.NetworkRunner
import com.mitra.utils.AgeAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SplashViewModel(
    private val preferences: Preferences,
    private val db: AppDatabase,
    private val providerAndroidId: ProviderAndroidId
) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private val showApproveLicenseButton: MediatorLiveData<Boolean> = MediatorLiveData()
    private val showBlockedUserDialog: MutableLiveData<Boolean> = MutableLiveData()
    private val temporaryLockedUser: MutableLiveData<Boolean> = MutableLiveData()
    private val startChat: MutableLiveData<Boolean> = MutableLiveData()
    private val api: ApiRetrofit? = inject()

    private fun getUnreadedMessages() {
        NetworkRunner(
            EmitBuilder<MessageResponse?>()
                .onSuccess {
                    val list = db.messageDao().getAll()
                    it?.let {
                        val writableList = it.messages.filter { message ->
                            !list.any { dbMessage -> message._id == dbMessage._id }
                        }

                        if (writableList.isNotEmpty()) {
                            db.messageDao().insertAll(it.messages)
                        }
                    }
                    MainThreadEmitBuilder<Unit>()
                        .onSuccess { startChat.value = true }
                        .build()
                        .onSuccess(Unit)

                }
                .onError {
                    it.printStackTrace()
                }
                .build()
        ) {
            api?.getUnreadedMessages(providerAndroidId.get())
        }
    }

    fun subscribeShowBlockedUserDialog(): LiveData<Boolean> = showBlockedUserDialog

    fun subscribeTemporaryLockedUser(): LiveData<Boolean> = temporaryLockedUser

    fun subscribeShowApproveLicenseButton(): LiveData<Boolean> = showApproveLicenseButton

    fun subscribeStartChat(): LiveData<Boolean> = startChat

    fun convertGender(gender: Int) =
        when {
            gender > 1 -> 1
            gender < 0 -> 0
            else -> gender
        }

    fun getUser() {
        NetworkRunner(
            MainThreadEmitBuilder<UserDto?>()
                .onSuccess {
                    val user = it?.run {
                        gender = convertGender(gender)
                        showMe = convertGender(showMe)
                        User(this)
                    }

                    user?.let {
                        if (user.block) {
                            showBlockedUserDialog.value = user.block

                            return@onSuccess
                        }

                        user.ageMinCompanion = AgeAdapter.getAgeIndex(user.ageMinCompanion)
                        user.ageMaxCompanion = AgeAdapter.getAgeIndex(user.ageMaxCompanion)
                        val previousLastDateLock = preferences.getUser().lastDateLock
                        val lastReport = preferences.getUser().lastReport
                        preferences.setUser(user)

                        if (previousLastDateLock < user.lastDateLock) {
                            showBlockedUserDialog.value = true

                            return@onSuccess
                        }

                        if (lastReport < user.lastReport) {
                            temporaryLockedUser.value = true

                            return@onSuccess
                        }

                        if (!user.licenseApprove) {
                            showApproveLicenseButton.value = user.licenseApprove
                        } else {
                            if (user.companionId.isNotEmpty()) {
                                getUnreadedMessages()
                            } else {
                                launch {
                                    withContext(Dispatchers.IO) {
                                        db.messageDao().deleteAllList(db.messageDao().getAll())
                                    }
                                }

                                showApproveLicenseButton.value = true
                            }
                        }
                    }
                }
                .build()
        ) {
            val item = inject<ApiRetrofit>()?.checkUser(providerAndroidId.get())
            println(item)
            item
        }
    }

    fun licenseApprove() {
        NetworkRunner(
            EmitBuilder<Boolean>()
                .onSuccess {

                }
                .build()
        ) {
            api?.approveLicense(providerAndroidId.get()) == true
        }
    }
}