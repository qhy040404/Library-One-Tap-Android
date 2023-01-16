package com.qhy040404.libraryonetap.view

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import coil.load
import com.absinthe.libraries.utils.extensions.dp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getColor
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getString

class FullScreenDialogView(context: Context) : ConstraintLayout(context) {
    private val viewId = View.generateViewId()
    private val containerId = View.generateViewId()
    private val logoId = View.generateViewId()
    private val sloganId = View.generateViewId()
    private val detailId = View.generateViewId()

    private val container = AppCompatImageView(context).apply {
        id = containerId
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            150.dp
        ).apply {
            setMargins(marginLeft, 20.dp, marginRight, marginBottom)
            marginStart = 20.dp
            marginEnd = 20.dp
            topToTop = viewId
            startToStart = viewId
            endToEnd = viewId
        }
        load(R.drawable.bg_fs_dialog)
    }

    private val logo = AppCompatImageView(context).apply {
        id = logoId
        layoutParams = LayoutParams(
            40.dp,
            40.dp
        ).apply {
            setMargins(marginLeft, 8.dp, marginRight, marginBottom)
            marginStart = 8.dp
            startToStart = containerId
            topToTop = containerId
        }
        load(R.drawable.pic_splash)
    }

    private val slogan = AppCompatTextView(context).apply {
        id = sloganId
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            40.dp
        ).apply {
            marginStart = 10.dp
            bottomToBottom = logoId
            startToEnd = logoId
            topToTop = logoId
        }
        gravity = Gravity.CENTER
        text = R.string.about_slogan.getString()
        setTextColor(R.color.fullscreen_text.getColor(context))
    }

    val detail = AppCompatTextView(context).apply {
        id = detailId
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            85.dp
        ).apply {
            setMargins(marginLeft, 10.dp, marginRight, marginBottom)
            marginStart = 28.dp
            marginEnd = 28.dp
            endToEnd = containerId
            startToStart = containerId
            topToBottom = logoId
        }
        setTextColor(R.color.fullscreen_text.getColor(context))
    }

    init {
        id = viewId
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(R.color.common_transparent_20.getColor(context))

        addView(container)
        addView(logo)
        addView(slogan)
        addView(detail)
    }
}
