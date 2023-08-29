package com.qhy040404.libraryonetap.recyclerview.simplepage

import android.content.Context
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.absinthe.libraries.utils.extensions.dp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.utils.extensions.getColor
import com.qhy040404.libraryonetap.utils.extensions.getDrawable

class CardView(context: Context) : LinearLayout(context) {
  val content = AppCompatTextView(context).apply {
    layoutParams = LayoutParams(
      LayoutParams.WRAP_CONTENT,
      LayoutParams.WRAP_CONTENT
    )
    setPadding(paddingLeft, paddingTop, paddingRight, 8.dp)
    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
    setTextColor(R.color.simple_page_card_text_color.getColor(context))
    setTextIsSelectable(true)
  }

  init {
    layoutParams = LayoutParams(
      LayoutParams.MATCH_PARENT,
      LayoutParams.WRAP_CONTENT
    )
    setPadding(16.dp, 16.dp, 12.dp, 16.dp)
    background = R.drawable.simplepage_card.getDrawable(context)
    elevation = 1.dp.toFloat()

    addView(content)
  }
}
