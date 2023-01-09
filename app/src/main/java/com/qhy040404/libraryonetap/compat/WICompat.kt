package com.qhy040404.libraryonetap.compat

import android.view.WindowInsets
import androidx.core.view.WindowInsetsCompat
import com.qhy040404.libraryonetap.constant.enums.InsetsParams
import com.qhy040404.libraryonetap.utils.OsUtils

@Suppress("DEPRECATION")
/**
 * Modified Window Insets implementations
 */
object WICompat {
    fun getSystemBars() = if (OsUtils.atLeastR()) {
        WindowInsets.Type.systemBars()
    } else {
        0
    }

    fun getInsetsParam(
        windowInsets: WindowInsetsCompat,
        typeMask: Int,
        param: InsetsParams,
    ) = if (OsUtils.atLeastR()) {
        when (param) {
            InsetsParams.LEFT -> windowInsets.getInsets(typeMask).left
            InsetsParams.RIGHT -> windowInsets.getInsets(typeMask).right
            InsetsParams.TOP -> windowInsets.getInsets(typeMask).top
            InsetsParams.BOTTOM -> windowInsets.getInsets(typeMask).bottom
        }
    } else {
        when (param) {
            InsetsParams.LEFT -> windowInsets.systemWindowInsetLeft
            InsetsParams.RIGHT -> windowInsets.systemWindowInsetRight
            InsetsParams.TOP -> windowInsets.systemWindowInsetTop
            InsetsParams.BOTTOM -> windowInsets.systemWindowInsetBottom
        }
    }
}
