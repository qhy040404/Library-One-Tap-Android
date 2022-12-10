package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.annotation.Parentheses
import com.qhy040404.libraryonetap.constant.Constants

@Suppress("unused")
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
     * @param globalValue   Current value
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

    /**
     * Replace all in a string
     *
     * @param oldVal    value to be replaced
     * @param newVal    value to replace
     * @param times     times to replace. 0 to infinite
     * @return replaced string
     */
    fun String.replaceAll(oldVal: String, newVal: String, times: Int = 0): String {
        var temp = this
        if (times <= 0) {
            temp = temp.split(oldVal).joinToString(separator = newVal)
        } else {
            for (i in 0 until times) {
                temp = temp.replace(oldVal, newVal)
            }
        }
        return temp
    }

    /**
     * Add parentheses for a string () [] {}
     *
     * @param size size of parentheses, Use Parentheses annotation class
     * @throws IllegalArgumentException
     * @return string with parentheses
     */
    fun String.addParentheses(@Parentheses size: Int): String {
        return when (size) {
            Parentheses.SMALL -> "(${this.trim()})"
            Parentheses.MEDIUM -> "[${this.trim()}]"
            Parentheses.LARGE -> "{${this.trim()}}"
            else -> throw IllegalArgumentException("Illegal size")
        }
    }

    /**
     * Substring between two delimiter.
     *
     * @param frontStr  front delimiter
     * @param behindStr behind delimiter
     * @return string
     */
    fun String.substringBetween(frontStr: String, behindStr: String): String {
        return this.substringAfter(frontStr).substringBefore(behindStr)
    }
}
