package com.qhy040404.libraryonetap.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object OsUtils {
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    fun atLeastT(): Boolean {
        return Build.VERSION.SDK_INT >= 33
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun atLeastS(): Boolean {
        return Build.VERSION.SDK_INT >= 31
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    fun atLeastR(): Boolean {
        return Build.VERSION.SDK_INT >= 30
    }
}
