package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.constant.Constants

object StringExtension {
    /**
     * Check if a string is equals to ERROR message
     *
     * @return true if the data is OK to use. Not equals to ERROR
     */
    fun String.isValid(): Boolean {
        return this != Constants.GLOBAL_ERROR && this != Constants.NET_TIMEOUT && this != Constants.NET_ERROR && this != Constants.NET_DISCONNECTED
    }

    /**
     * Check if new value is same as previous one
     *
     * @param globalValue Current value
     * @param isCustomTheme If the setting is custom theme
     * @return true if they are same
     */
    fun String.isDuplicateGV(globalValue: String, isCustomTheme: Boolean = false): Boolean {
        val a = this == globalValue || globalValue == Constants.GLOBAL_ERROR && this == ""
        return if (isCustomTheme) {
            a && this != "random"
        } else {
            a
        }
    }
}
