package com.mitra.ui.screens.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mitra.*
import com.mitra.utils.getStringTime
import com.mitra.utils.isNextMinutes

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: MutableList<ChatItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ME_MESSAGE ->
                TextHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.vh_message_me, parent, false)
                )
            TIME_MESSAGE_ME -> {
                TextHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.vh_time_me, parent, false)
                )
            }
            TIME_MESSAGE_OTHER -> {
                TextHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.vh_time_other, parent, false)
                )
            }
            TYPING_MESSAGE ->
                TextHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.vh_message_other, parent, false)
                )
            else ->
                TextHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.vh_message_other, parent, false)
                )
        }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].let {
            (holder as TextHolder).setValues(it.message)
        }
    }

    fun addItem(item: ChatItem) {
        if (items.size > 1) {
            if (items.last() !is TypingItem) {
                if (items.last().timestamp.isNextMinutes(item.timestamp)) {
                    items.add(item)
                    items.add(generateTimeItem(item))
                    notifyItemRangeInserted(items.lastIndex - 1, 2)
                } else {
                    items.removeAt(items.lastIndex)
                    notifyItemRemoved(items.lastIndex + 1)
                    items.add(item)
                    items.add(generateTimeItem(item))
                    notifyItemRangeInserted(items.lastIndex - 1, 2)
                }
            } else {
                if (items[items.lastIndex - 1].timestamp.isNextMinutes(item.timestamp)) {
                    items.add(items.lastIndex, item)
                    items.add(items.lastIndex, generateTimeItem(item))
                    notifyItemRangeInserted(items.lastIndex - 2, 2)
                } else {
                    items.removeAt(items.lastIndex - 1)
                    notifyItemRemoved(items.lastIndex)
                    items.add(items.lastIndex, item)
                    items.add(items.lastIndex, generateTimeItem(item))
                    notifyItemRangeInserted(items.lastIndex - 2, 2)
                }
            }
        } else {
            items.add(0, item)
            items.add(1, generateTimeItem(item))
            notifyItemRangeInserted(items.lastIndex - 2, 2)
        }
    }

    fun updateList(newList: List<ChatItem>) {
        val diff = DiffUtil.calculateDiff(DiffDataCallback(items, newList))
        items.clear()
        items.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    private fun generateTimeItem(item: ChatItem) =
        if (item is MeMessageItem) {
            MeTimeItem(
                item.timestamp.getStringTime(),
                item.timestamp
            )
        } else {
            OtherTimeItem(
                item.timestamp.getStringTime(),
                item.timestamp
            )
        }

    fun addTyping() {
        if (items.isNotEmpty() && items.last() !is TypingItem) {
            items.add(TypingItem())
            notifyItemInserted(items.lastIndex)
        } else if (items.isEmpty()) {
            items.add(TypingItem())
            notifyItemInserted(items.lastIndex)
        }
    }

    fun deleteTyping() {
        if (items.isNotEmpty() && items.last() is TypingItem) {
            items.removeAt(items.lastIndex)
            notifyItemRemoved(items.lastIndex + 1)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            items[position] is MeMessageItem -> ME_MESSAGE
            items[position] is MeTimeItem -> TIME_MESSAGE_ME
            items[position] is OtherTimeItem -> TIME_MESSAGE_OTHER
            items[position] is TypingItem -> TYPING_MESSAGE
            else -> COMPANION_MESSAGE
        }
    }

    companion object {
        const val ME_MESSAGE = 0
        const val COMPANION_MESSAGE = 1
        const val TYPING_MESSAGE = 2
        const val TIME_MESSAGE_ME = 3
        const val TIME_MESSAGE_OTHER = 4
    }
}

class TextHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textView: AppCompatTextView = itemView.findViewById(R.id.text)

    fun setValues(message: String) {
        textView.text = message
    }
}