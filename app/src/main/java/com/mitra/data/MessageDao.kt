package com.mitra.data

import androidx.room.*

@Dao
interface MessageDao {

    @Query("SELECT * FROM message")
    fun getAll(): List<Message>

    @Query("SELECT * FROM message WHERE id = :id")
    fun getById(id: Long): Message

    @Insert
    fun insert(message: Message)

    @Insert
    fun insertAll(messages: List<Message>)

    @Delete
    fun delete(message: Message)

    @Delete
    fun deleteAllList(messages: List<Message>)

}