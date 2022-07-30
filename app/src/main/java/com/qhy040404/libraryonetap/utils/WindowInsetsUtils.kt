package com.qhy040404.libraryonetap.utils

import android.view.WindowInsets

object WindowInsetsUtils {
    fun getSystemBars(): Int {
        return if (OsUtils.atLeastR()) {
            WindowInsets.Type.systemBars()
        } else {
            0
        }
    }
}