package com.mitra.ui.screens.chat

import androidx.recyclerview.widget.DiffUtil
import com.mitra.ChatItem

class DiffDataCallback(
    private val old: List<ChatItem>,
    private val new: List<ChatItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = old.size

    override fun getNewListSize() = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] == new[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        old[oldItemPosition] == new[newItemPosition]

}