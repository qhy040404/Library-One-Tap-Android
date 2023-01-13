package com.qhy040404.libraryonetap.utils.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.constant.GlobalValues

object IntExtensions {
    /**
     * Get string from resource ID
     */
    fun Int.getString(): String {
        val context = LibraryOneTapApp.app
        val conf = context.resources.configuration.apply {
            setLocale(GlobalValues.locale)
        }
        return context.createConfigurationContext(conf).getString(this)
    }

    /**
     * Get color from resource ID
     */
    fun Int.getColor(context: Context): Int {
        return context.getColor(this)
    }

    /**
     * Get drawable from resource ID
     */
    fun Int.getDrawable(context: Context): Drawable? {
        return context.getDrawable(this)
    }

    /**
     * Get dimen from resource ID
     */
    fun Int.getDimen(): Float {
        return LibraryOneTapApp.app.resources.getDimension(this)
    }
}
