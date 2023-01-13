package com.qhy040404.libraryonetap.recycleview.simplepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.recycleview.simplepage.CategoryViewBinder.CGViewHolder

class CategoryViewBinder : ItemViewBinder<Category, CGViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): CGViewHolder {
        return CGViewHolder(inflater.inflate(R.layout.simplepage_item_category, parent, false))
    }

    override fun onBindViewHolder(holder: CGViewHolder, item: Category) {
        holder.category.text = item.title
    }

    override fun getItemId(item: Category): Long {
        return item.hashCode().toLong()
    }

    class CGViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: AppCompatTextView

        init {
            category = itemView.findViewById(R.id.category)
        }
    }
}
