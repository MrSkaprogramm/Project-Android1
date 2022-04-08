package com.mitra

open class ChatItem(val message: String, val timestamp: Long) {

    override fun equals(other: Any?): Boolean {
        other as ChatItem
        return this.message == other.message && this.timestamp == other.timestamp
                && other.javaClass == javaClass
    }

    override fun hashCode(): Int {
        return ChatItem::class.java.hashCode() + message.hashCode() + timestamp.hashCode()
    }
}

class MeMessageItem(message: String, timestamp: Long) : ChatItem(message, timestamp)

class MeTimeItem(time: String, timestamp: Long) : ChatItem(time, timestamp)

class OtherTimeItem(time: String, timestamp: Long) : ChatItem(time, timestamp)

class TypingItem : ChatItem("typing...", 0)