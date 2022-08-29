package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.constant.Constants

object StringExtension {
    fun String.isValid(): Boolean {
        return this != Constants.GLOBAL_ERROR && this != Constants.NET_TIMEOUT && this != Constants.NET_ERROR && this != Constants.NET_DISCONNECTED
    }

    fun String.isDuplicateGV(globalValue: String): Boolean {
        return this.isDuplicateGV(globalValue, false)
    }

    fun String.isDuplicateGV(globalValue: String, isCustomTheme: Boolean): Boolean {
        val a = this == globalValue || (globalValue == Constants.GLOBAL_ERROR && this == "")
        return if (isCustomTheme) {
            a && this != "random"
        } else a
    }
}
