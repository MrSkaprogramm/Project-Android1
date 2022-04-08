package com.mitra.data

import androidx.room.*

@Database(entities = [Message::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun messageDao(): MessageDao
}