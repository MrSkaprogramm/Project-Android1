package com.mitra.view

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView

class PaddingDecorator(
    private val left: ((type: Int, position: Int, count: Int) -> Int)? = null,
    private val top: ((type: Int, position: Int, count: Int) -> Int)? = null,
    private val right: ((type: Int, position: Int, count: Int) -> Int)? = null,
    private val bottom: ((type: Int, position: Int, count: Int) -> Int)? = null,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        parent.adapter?.let { adapter ->
            left?.apply {
                outRect.left =
                    invoke(adapter.getItemViewType(itemPosition), itemPosition, adapter.itemCount)
            }
            top?.apply {
                outRect.top =
                    invoke(adapter.getItemViewType(itemPosition), itemPosition, adapter.itemCount)
            }
            right?.apply {
                outRect.right =
                    invoke(adapter.getItemViewType(itemPosition), itemPosition, adapter.itemCount)
            }
            bottom?.apply {
                outRect.bottom =
                    invoke(adapter.getItemViewType(itemPosition), itemPosition, adapter.itemCount)
            }
        }

    }
}