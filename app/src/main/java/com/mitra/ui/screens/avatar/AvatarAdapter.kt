package com.mitra.ui.screens.avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.mitra.R
import com.mitra.data.AvatarItem
import com.mitra.utils.dp


class AvatarAdapter(private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<AvatarItem>()
    private var selectedIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AvatarViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.vh_avatar, parent, false),
            itemClickListener
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as AvatarViewHolder
        holder.setItem(items[position])
        holder.setSelected(position == selectedIndex)
    }


    fun setList(items: MutableList<AvatarItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun setSelected(index: Int) {
        selectedIndex = index
        notifyDataSetChanged()
    }
}

class AvatarViewHolder(itemView: View, private val itemClickListener: ItemClickListener) :
    RecyclerView.ViewHolder(itemView) {

    private val avatar: AppCompatImageView = itemView.findViewById(R.id.avatar)
    private val avatarSelector: AppCompatImageView = itemView.findViewById(R.id.avatar_selector)

    fun setItem(item: AvatarItem) {
        val recyclerView = RecyclerView
            .ViewHolder::class.java.getDeclaredField("mOwnerRecyclerView").apply {
            isAccessible = true
        }
        val list = recyclerView.get(this) as RecyclerView
        val adapter = list.adapter as AvatarAdapter


        itemView.apply {
            setPadding(
                if (adapterPosition in 0 until adapter.itemCount step 3) {
                    itemView.dp(16)
                } else {
                    itemView.dp(0)
                },
                if (adapterPosition in 0..2) {
                    itemView.dp(20)
                } else {
                    itemView.dp(8)
                },
                if (adapterPosition in 2 until adapter.itemCount step 3) {
                    itemView.dp(16)
                } else {
                    itemView.dp(0)
                },
                if (adapterPosition in adapter.itemCount - 3 until adapter.itemCount) {
                    itemView.dp(20)
                } else {
                    itemView.dp(8)
                }
            )
        }
        avatar.setImageResource(item.resourceId)
        itemView.setOnClickListener {
            itemClickListener.onClick(item.index)
        }
    }

    fun setSelected(selected: Boolean) {
        avatarSelector.isSelected = selected
    }
}

interface ItemClickListener {
    fun onClick(id: Int)
}