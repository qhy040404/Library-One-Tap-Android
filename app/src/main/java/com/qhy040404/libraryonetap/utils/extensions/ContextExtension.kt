package com.qhy040404.libraryonetap.utils.extensions

import android.content.Context
import androidx.annotation.AnyThread
import androidx.annotation.StringRes
import com.qhy040404.libraryonetap.utils.Toasty

/**
 * Show short-time toast with String
 *
 * @param message original string
 */
@AnyThread
fun Context.showToast(message: String) = Toasty.showShort(this, message)

/**
 * Show short-time toast with String
 *
 * @param res string's resource id
 */
@AnyThread
fun Context.showToast(@StringRes res: Int) = Toasty.showShort(this, res)

/**
 * Show long-time toast with String
 *
 * @param message original string
 */
@AnyThread
fun Context.showLongToast(message: String) = Toasty.showLong(this, message)

/**
 * Show long-time toast with String
 *
 * @param res string's resource id
 */
@AnyThread
fun Context.showLongToast(@StringRes res: Int) = Toasty.showLong(this, res)
