package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.content.SharedPreferences
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.constant.SP_NAME

object SPUtils {
    private val sp: SharedPreferences =
        LibraryOneTapApp.app.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    fun <T> getValue(name: String, default: T): T = with(sp) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default).orEmpty()
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw java.lang.IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        res as T
    }
}