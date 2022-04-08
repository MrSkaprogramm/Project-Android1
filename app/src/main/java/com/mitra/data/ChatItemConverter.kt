package com.mitra.data

import com.mitra.ChatItem
import com.mitra.MeMessageItem
import com.mitra.MeTimeItem
import com.mitra.OtherTimeItem
import com.mitra.utils.getStringTime
import com.mitra.utils.isNextMinutes

class ChatItemConverter(private val androidId: String) {

    fun convert(list: List<Message>) =
        if (list.isNotEmpty()) {
            val newList = mutableListOf<ChatItem>()
            var lastTimestamp = list.first().timestamp

            for (index in list.indices) {
                val msg = list[index]
                if (msg.deviceId == androidId) {
                    newList.add(MeMessageItem(msg.message, msg.timestamp))
                } else {
                    newList.add(ChatItem(msg.message, msg.timestamp))
                }

                if (lastTimestamp.isNextMinutes(msg.timestamp)) {
                    newList.add(
                        newList.lastIndex,
                        if (newList[newList.lastIndex - 1] is MeMessageItem) {
                            MeTimeItem(
                                lastTimestamp.getStringTime(),
                                lastTimestamp
                            )
                        } else {
                            OtherTimeItem(
                                lastTimestamp.getStringTime(),
                                lastTimestamp
                            )
                        }
                    )
                    lastTimestamp = msg.timestamp
                }

                if (index == list.lastIndex) {
                    newList.add(
                        if (newList.last() is MeMessageItem) {
                            MeTimeItem(
                                lastTimestamp.getStringTime(),
                                lastTimestamp
                            )
                        } else {
                            OtherTimeItem(
                                lastTimestamp.getStringTime(),
                                lastTimestamp
                            )
                        }
                    )
                }
            }

            newList
        } else {
            emptyList()
        }

}