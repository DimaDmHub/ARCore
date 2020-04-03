package com.example.arcore.ui.main.fragment.list.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PaddingItemDecoration constructor(
    private val top: Int,
    private val bottom: Int,
    private val start: Int,
    private val end: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = if (parent.getChildAdapterPosition(view) == 0) top else 0
        outRect.bottom = bottom
        outRect.right = end
        outRect.left = start
    }
}