package com.qhy040404.libraryonetap.recyclerview.simplepage

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.drakeet.multitype.MultiTypeAdapter

class DividerItemDecoration(private val adapter: MultiTypeAdapter) : ItemDecoration() {
  private val dividerClasses = arrayOf(Card::class.java, Clickable::class.java)
  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    if (adapter.itemCount == 0) {
      outRect.set(0, 0, 0, 0)
      return
    }
    val items: List<*> = adapter.items
    val position = parent.getChildAdapterPosition(view)
    val should = position + 1 < items.size &&
      dividerClasses.contains(items[position]!!.javaClass) &&
      dividerClasses.contains(items[position + 1]!!.javaClass)
    if (should) {
      outRect.set(0, 0, 0, 1)
    } else {
      outRect.set(0, 0, 0, 0)
    }
  }
}
