package com.mitra.data

import com.mitra.data.dtos.UserDto

class User(
    var name: String = "",
    var age: Int = 0,
    var gender: Int = 0,
    var firebaseId: String = "",
    var deviceId: String = "",
    var avatar: Int = 0,
    var forWhat: Int = 0,
    var showMe: Int = 0,
    var ageMinCompanion: Int = 0,
    var ageMaxCompanion: Int = 0,
    var countLock: Int = 0,
    var lastDateLock: Int = 0,
    var lastReport: Long = 0,
    var block: Boolean = false,
    var licenseApprove: Boolean = false,
    var companionId: String = ""
) {

    constructor(userDto: UserDto) : this(
        userDto.name,
        userDto.age,
        userDto.gender,
        userDto.firebaseId ?: "",
        userDto.deviceId,
        userDto.avatar,
        userDto.forWhat,
        userDto.showMe,
        userDto.ageMinCompanion,
        userDto.ageMaxCompanion,
        userDto.countLock,
        userDto.lastDateLock,
        userDto.lastReport,
        userDto.block,
        userDto.licenseApprove,
        userDto.companionId ?: ""
    )

    companion object {
        const val USER_NAME = "name"
        const val USER_AGE = "age"
        const val USER_GENDER = "gender"
        const val FIREBASE_ID = "firebaseId"
        const val DEVICE_ID = "deviceId"
        const val AVATAR = "avatar"
        const val FOR_WHAT = "for_what"
        const val SHOW_ME = "show_me"
        const val AGE_MIN_COMPANION = "age_min_companion"
        const val AGE_MAX_COMPANION = "age_max_companion"
        const val COUNT_LOCK = "count_lock"
        const val LAST_DATE_LOCK = "last_date_lock"
        const val LAST_REPORT = "last_report"
        const val BLOCK = "block"
        const val LICENSE_APPROVE = "license_approve"
        const val COMPANION_ID = "companion_id"
    }
}