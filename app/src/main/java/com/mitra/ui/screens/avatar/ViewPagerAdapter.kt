package com.mitra.ui.screens.avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mitra.R
import com.mitra.data.AvatarItem
import com.mitra.utils.dp
import com.mitra.view.PaddingDecorator

class ViewPagerAdapter(private val itemClickListener: ItemClickListener) :
    RecyclerView.Adapter<Page>() {
    val items = mutableListOf<List<AvatarItem>>()
    private var selectedPage = -1
    private var selectedIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Page(
            LayoutInflater.from(parent.context).inflate(R.layout.vh_avatar_list, parent, false),
            itemClickListener
        )

    override fun onBindViewHolder(holder: Page, position: Int) {
        holder.setItem(items[position])

        if (position == selectedPage) {
            holder.setSelected(selectedIndex)
        } else {
            holder.setSelected(-1)
        }
    }

    override fun getItemCount() = items.size

    fun setItems(list: List<List<AvatarItem>>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun setSelected(indexPage: Int, indexAvatar: Int) {
        selectedIndex = indexAvatar
        selectedPage = indexPage
        notifyDataSetChanged()
    }
}

class Page(view: View, itemClickListener: ItemClickListener) : RecyclerView.ViewHolder(view) {

    private val listAvatars: RecyclerView = itemView.findViewById(R.id.list_avatars)
    private val adapter = AvatarAdapter(itemClickListener)

    init {
        val layoutManager = GridLayoutManager(itemView.context, 3)
        listAvatars.layoutManager = layoutManager
        listAvatars.adapter = adapter
        ViewCompat.setNestedScrollingEnabled(listAvatars, false)
    }

    fun setItem(item: List<AvatarItem>) {
        adapter.setList(item.toMutableList())
    }

    fun setSelected(index: Int) {
        adapter.setSelected(index)
    }
}