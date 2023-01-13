package com.qhy040404.libraryonetap.recycleview.simplepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.recycleview.simplepage.CardViewBinder.CViewHolder

class CardViewBinder : ItemViewBinder<Card, CViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): CViewHolder {
        return CViewHolder(inflater.inflate(R.layout.simplepage_item_card, parent, false))
    }

    override fun onBindViewHolder(holder: CViewHolder, item: Card) {
        holder.content.setLineSpacing(
            item.lineSpacingExtra.toFloat(),
            holder.content.lineSpacingMultiplier
        )
        holder.content.text = item.content
    }

    override fun getItemId(item: Card): Long {
        return item.hashCode().toLong()
    }

    class CViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: AppCompatTextView

        init {
            content = itemView.findViewById(R.id.simple_content)
        }
    }
}
