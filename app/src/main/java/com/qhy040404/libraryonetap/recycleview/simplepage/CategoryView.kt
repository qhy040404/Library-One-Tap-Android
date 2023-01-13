package com.qhy040404.libraryonetap.recycleview.simplepage

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import com.absinthe.libraries.utils.extensions.dp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getColor

class CategoryView(context: Context) : LinearLayout(context) {
    val title = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            0.dp,
            LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(1.dp, marginTop, 1.dp, marginBottom)
            weight = 1f
        }
        setPadding(
            16.dp,
            12.dp,
            12.dp,
            12.dp
        )
        setTextColor(R.color.simple_page_category_text_color.getColor(context))
    }

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL

        addView(title)
    }
}
