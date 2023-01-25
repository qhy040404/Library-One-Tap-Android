package com.qhy040404.libraryonetap.recyclerview.simplepage

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewBinder
import com.qhy040404.libraryonetap.base.BaseViewHolder

class CardViewBinder : ItemViewBinder<Card, BaseViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): BaseViewHolder {
        return BaseViewHolder(
            CardView(inflater.context)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, item: Card) {
        (holder.itemView as CardView).content.apply {
            setLineSpacing(
                item.lineSpacingExtra.toFloat(),
                (holder.itemView as CardView).content.lineSpacingMultiplier
            )
            text = item.content
        }
    }

    override fun getItemId(item: Card): Long {
        return item.hashCode().toLong()
    }
}
