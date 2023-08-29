package com.qhy040404.libraryonetap.recyclerview.simplepage

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewBinder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseViewHolder
import com.qhy040404.libraryonetap.utils.extensions.getColor

class ClickableViewBinder : ItemViewBinder<Clickable, BaseViewHolder>() {
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder {
    return BaseViewHolder(
      ClickableView(inflater.context)
    )
  }

  override fun onBindViewHolder(holder: BaseViewHolder, item: Clickable) {
    (holder.itemView as ClickableView).apply {
      name.text = item.name
      desc.text = item.desc
      url = item.url
      if (!item.passed) {
        name.setTextColor(R.color.red_500.getColor(context))
      }
    }
  }

  override fun getItemId(item: Clickable): Long {
    return item.hashCode().toLong()
  }
}
