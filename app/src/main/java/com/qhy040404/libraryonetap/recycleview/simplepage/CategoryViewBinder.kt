package com.qhy040404.libraryonetap.recycleview.simplepage

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewBinder
import com.qhy040404.libraryonetap.base.BaseViewHolder

class CategoryViewBinder : ItemViewBinder<Category, BaseViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder {
        return BaseViewHolder(
            CategoryView(inflater.context)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, item: Category) {
        (holder.itemView as CategoryView).title.text = item.title
    }

    override fun getItemId(item: Category): Long {
        return item.hashCode().toLong()
    }
}
