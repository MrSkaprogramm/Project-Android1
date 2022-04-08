package com.mitra.data.dtos

class UserDto(
    val name: String,
    val age: Int,
    var gender: Int,
    val firebaseId: String?,
    val deviceId: String,
    val avatar: Int,
    val forWhat: Int,
    var showMe: Int,
    val ageMinCompanion: Int,
    val ageMaxCompanion: Int,
    val countLock: Int,
    val lastDateLock: Int,
    val block: Boolean,
    val licenseApprove: Boolean,
    val companionId: String?,
    val lastReport: Long
)