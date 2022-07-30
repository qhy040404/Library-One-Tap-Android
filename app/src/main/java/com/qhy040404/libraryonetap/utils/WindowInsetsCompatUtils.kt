package com.qhy040404.libraryonetap.utils

import androidx.core.view.WindowInsetsCompat

@Suppress("DEPRECATION")
object WindowInsetsCompatUtils {
    fun getInsetsParam(windowInsets: WindowInsetsCompat, typeMask: Int, param: String): Int {
        return if (OsUtils.atLeastR()) {
            when (param) {
                "left" -> windowInsets.getInsets(typeMask).left
                "right" -> windowInsets.getInsets(typeMask).right
                "top" -> windowInsets.getInsets(typeMask).top
                "bottom" -> windowInsets.getInsets(typeMask).bottom
                else -> throw IllegalArgumentException()
            }
        } else {
            when (param) {
                "left" -> windowInsets.systemWindowInsetLeft
                "right" -> windowInsets.systemWindowInsetRight
                "top" -> windowInsets.systemWindowInsetTop
                "bottom" -> windowInsets.systemWindowInsetBottom
                else -> throw IllegalArgumentException()
            }
        }
    }
}