package com.mitra.ui.screens.chat

import android.graphics.Rect
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.mitra.utils.dp

class DifferentMessageSpaceDecorator : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        parent.adapter?.let {
            if (itemPosition < (it.itemCount - 1)) {
                if (isDifferent(
                        it.getItemViewType(itemPosition),
                        it.getItemViewType(itemPosition + 1)
                    ) ||
                    it.getItemViewType(itemPosition) == ChatAdapter.TYPING_MESSAGE
                ) {
                    outRect.bottom += parent.dp(4)
                }
            }

            if ((itemPosition == it.itemCount - 1) &&
                it.getItemViewType(itemPosition) == ChatAdapter.TYPING_MESSAGE
            ) {
                outRect.bottom += parent.dp(4)
            }
        }
    }

    private fun isDifferent(typeA: Int, typeB: Int): Boolean {
        val types = arrayOf(ChatAdapter.ME_MESSAGE, ChatAdapter.COMPANION_MESSAGE)

        return types.contains(typeA) && types.contains(typeB) && typeA != typeB
    }
}