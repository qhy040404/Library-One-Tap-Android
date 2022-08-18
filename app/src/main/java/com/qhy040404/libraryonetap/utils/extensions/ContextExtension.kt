package com.qhy040404.libraryonetap.utils.extensions

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.StringRes
import com.qhy040404.libraryonetap.utils.Toasty

@Suppress("unused")
object ContextExtension {
    @AnyThread
    fun Context.showToast(message: String) = Toasty.showShort(this, message)

    @AnyThread
    fun Context.showToast(@StringRes res: Int) = Toasty.showShort(this, res)

    @AnyThread
    fun Context.showLongToast(message: String) = Toasty.showLong(this, message)

    @AnyThread
    fun Context.showLongToast(@StringRes res: Int) = Toasty.showLong(this, res)
}
