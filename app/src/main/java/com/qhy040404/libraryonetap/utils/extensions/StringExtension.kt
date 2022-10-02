package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.constant.Constants

object StringExtension {
    fun String.isValid(): Boolean {
        return this != Constants.GLOBAL_ERROR && this != Constants.NET_TIMEOUT && this != Constants.NET_ERROR && this != Constants.NET_DISCONNECTED
    }

    fun String.isDuplicateGV(globalValue: String, isCustomTheme: Boolean = false): Boolean {
        val a = this == globalValue || globalValue == Constants.GLOBAL_ERROR && this == ""
        return if (isCustomTheme) {
            a && this != "random"
        } else {
            a
        }
    }

    fun String.replaceAll(old: String, new: String): String {
        var temp = this
        while (temp.contains(old)) {
            temp = temp.replace(old, new)
        }
        return temp
    }
}
