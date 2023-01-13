package com.qhy040404.libraryonetap.recycleview.simplepage

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItemViewBinder.CIViewHolder

class ClickableItemViewBinder : ItemViewBinder<ClickableItem, CIViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): CIViewHolder {
        return CIViewHolder(
            inflater.inflate(R.layout.simplepage_item_clickable, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CIViewHolder, item: ClickableItem) {
        holder.name.text = item.name
        holder.desc.text = item.desc
        holder.data = item
    }

    override fun getItemId(item: ClickableItem): Long {
        return item.hashCode().toLong()
    }

    class CIViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val name: AppCompatTextView
        val desc: AppCompatTextView
        lateinit var data: ClickableItem

        init {
            name = itemView.findViewById(R.id.name)
            desc = itemView.findViewById(R.id.desc)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (data.url != null) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(data.url)
                try {
                    v.context.startActivity(intent)
                } catch (ignored: ActivityNotFoundException) {
                }
            }
        }
    }
}
