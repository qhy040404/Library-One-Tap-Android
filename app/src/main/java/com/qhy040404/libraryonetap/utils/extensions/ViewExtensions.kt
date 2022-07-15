package com.qhy040404.libraryonetap.utils.extensions

import android.content.res.Resources

object ViewExtensions {
    val Number.dp: Int get() = (toInt() * Resources.getSystem().displayMetrics.density).toInt()
}