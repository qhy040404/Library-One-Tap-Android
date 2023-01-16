package com.qhy040404.libraryonetap.recycleview.simplepage

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.net.toUri
import com.absinthe.libraries.utils.extensions.dp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getColor
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getDrawable

class ClickableView(context: Context) : LinearLayout(context), View.OnClickListener {
    val name = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        setTextColor(R.color.simple_page_name_color.getColor(context))
    }

    val desc = AppCompatTextView(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        setTextColor(R.color.simple_page_desc_color.getColor(context))
    }

    var url: String? = null

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        orientation = VERTICAL
        setPadding(16.dp, 16.dp, 12.dp, 16.dp)
        background = R.drawable.simplepage_card.getDrawable(context)
        elevation = 1.dp.toFloat()

        addView(name)
        addView(desc)
    }

    override fun onClick(v: View) {
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = it.toUri()
            runCatching {
                context.startActivity(intent)
            }
        }
    }
}
