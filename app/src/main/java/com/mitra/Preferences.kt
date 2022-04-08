package com.mitra

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.mitra.data.User

class Preferences(application: Context) {

    private val preferences: SharedPreferences =
        application.getSharedPreferences("user_config", MODE_PRIVATE)

    fun getUser(): User {
        val user = User()
        user.name = preferences.getString(User.USER_NAME, "") ?: ""
        user.age = preferences.getInt(User.USER_AGE, 18)
        user.gender = preferences.getInt(User.USER_GENDER, 0)
        user.firebaseId = preferences.getString(User.FIREBASE_ID, "") ?: ""
        user.deviceId = preferences.getString(User.DEVICE_ID, "") ?: ""
        user.avatar = preferences.getInt(User.AVATAR, 0)
        user.forWhat = preferences.getInt(User.FOR_WHAT, 0)
        user.showMe = preferences.getInt(User.SHOW_ME, 0)
        user.ageMinCompanion = preferences.getInt(User.AGE_MIN_COMPANION, 0)
        user.ageMaxCompanion = preferences.getInt(User.AGE_MAX_COMPANION, 0)
        user.countLock = preferences.getInt(User.COUNT_LOCK, 0)
        user.block = preferences.getBoolean(User.BLOCK, false)
        user.licenseApprove = preferences.getBoolean(User.LICENSE_APPROVE, false)
        user.companionId = preferences.getString(User.COMPANION_ID, "") ?: ""
        user.lastReport = preferences.getLong(User.LAST_REPORT, 0)

        return user
    }

    fun setAvatar(avatar: Int) {
        val editor =  preferences.edit()
        editor.putInt(User.AVATAR, avatar)
        editor.apply()
    }

    fun setName(name: String) {
        val editor =  preferences.edit()
        editor.putString(User.USER_NAME, name)
        editor.apply()
    }


    fun setGender(gender: Int) {
        val editor = preferences.edit()
        editor.putInt(User.USER_GENDER, gender)
        editor.apply()
    }


    fun setAge(age: Int) {
        val editor = preferences.edit()
        editor.putInt(User.USER_AGE, age)
        editor.apply()
    }

    fun setShowMe(showMe: Int) {
        val editor = preferences.edit()
        editor.putInt(User.SHOW_ME, showMe)
        editor.apply()
    }

    fun setForWhat(forWhat: Int) {
        val editor = preferences.edit()
        editor.putInt(User.FOR_WHAT, forWhat)
        editor.apply()
    }

    fun setAgeMin(ageMin: Int) {
        val editor = preferences.edit()
        editor.putInt(User.AGE_MIN_COMPANION, ageMin)
        editor.apply()
    }

    fun setAgeMax(ageMax: Int) {
        val editor = preferences.edit()
        editor.putInt(User.AGE_MAX_COMPANION, ageMax)
        editor.apply()
    }

    fun setUser(user: User) {
        val editor = preferences.edit()
        editor.putString(User.USER_NAME, user.name)
        editor.putInt(User.USER_AGE, user.age)
        editor.putInt(User.USER_GENDER, user.gender)
        editor.putString(User.FIREBASE_ID, user.firebaseId)
        editor.putString(User.DEVICE_ID, user.deviceId)
        editor.putInt(User.AVATAR, user.avatar)
        editor.putInt(User.FOR_WHAT, user.forWhat)
        editor.putInt(User.SHOW_ME, user.showMe)
        editor.putInt(User.AGE_MIN_COMPANION, user.ageMinCompanion)
        editor.putInt(User.AGE_MAX_COMPANION, user.ageMaxCompanion)
        editor.putInt(User.COUNT_LOCK, user.countLock)
        editor.putBoolean(User.BLOCK, user.block)
        editor.putBoolean(User.LICENSE_APPROVE, user.licenseApprove)
        editor.putString(User.COMPANION_ID, user.companionId)
        editor.putLong(User.LAST_REPORT, user.lastReport)
        editor.apply()
    }
}