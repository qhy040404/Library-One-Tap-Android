package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.constant.GlobalValues

object IntExtensions {
    /**
     * Get string from resource ID
     *
     * @param context Context
     */
    fun Int.getString(): String {
        val context = LibraryOneTapApp.app
        val conf = context.resources.configuration.apply {
            setLocale(GlobalValues.locale)
        }
        return context.createConfigurationContext(conf).getString(this)
    }
}
