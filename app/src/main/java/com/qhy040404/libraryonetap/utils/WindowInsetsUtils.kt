package com.qhy040404.libraryonetap.utils

import android.view.WindowInsets
import androidx.core.view.WindowInsetsCompat
import com.qhy040404.libraryonetap.annotation.InsetsParams

@Suppress("DEPRECATION")
object WindowInsetsUtils {
    fun getSystemBars() = if (OsUtils.atLeastR()) {
        WindowInsets.Type.systemBars()
    } else {
        0
    }

    fun getInsetsParam(
        windowInsets: WindowInsetsCompat,
        typeMask: Int,
        @InsetsParams param: Int,
    ) = if (OsUtils.atLeastR()) {
        when (param) {
            InsetsParams.LEFT -> windowInsets.getInsets(typeMask).left
            InsetsParams.RIGHT -> windowInsets.getInsets(typeMask).right
            InsetsParams.TOP -> windowInsets.getInsets(typeMask).top
            InsetsParams.BOTTOM -> windowInsets.getInsets(typeMask).bottom
            else -> throw IllegalArgumentException()
        }
    } else {
        when (param) {
            InsetsParams.LEFT -> windowInsets.systemWindowInsetLeft
            InsetsParams.RIGHT -> windowInsets.systemWindowInsetRight
            InsetsParams.TOP -> windowInsets.systemWindowInsetTop
            InsetsParams.BOTTOM -> windowInsets.systemWindowInsetBottom
            else -> throw IllegalArgumentException()
        }
    }
}