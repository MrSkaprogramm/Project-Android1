package com.mitra.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
class Message(
    val _id: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val message: String,
    @SerializedName("deviceId")
    val deviceId: String,
    val timestamp: Long
)