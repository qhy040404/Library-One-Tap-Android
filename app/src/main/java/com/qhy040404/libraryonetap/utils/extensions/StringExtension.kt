package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.constant.enums.Parentheses

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
        (this == globalValue || globalValue == Constants.GLOBAL_ERROR && this == "").let {
            return if (isCustomTheme) {
                it && this != "random"
            } else {
                it
            }
        }
    }

    /**
     * Add parentheses for a string () [] {}
     *
     * @param size size of parentheses, Use Parentheses enum
     * @throws IllegalArgumentException
     * @return string with parentheses
     */
    fun String.addParentheses(size: Parentheses): String {
        return when (size) {
            Parentheses.SMALL -> "(${this.trim()})"
            Parentheses.MEDIUM -> "[${this.trim()}]"
            Parentheses.LARGE -> "{${this.trim()}}"
        }
    }

    /**
     * Substring between two delimiter.
     *
     * @param frontDelimiter    front delimiter, substringAfter
     * @param behindDelimiter   behind delimiter, substringBefore
     * @param includeDelimiter  return string with delimiter, default false
     * @param reverse           First substringBefore and then after, default false
     * @return string
     */
    fun String.substringBetween(
        frontDelimiter: String,
        behindDelimiter: String,
        includeDelimiter: Boolean = false,
        reverse: Boolean = false,
    ): String {
        val subStr = if (!reverse) {
            this.substringAfter(frontDelimiter).substringBefore(behindDelimiter)
        } else {
            this.substringBefore(behindDelimiter).substringAfter(frontDelimiter)
        }
        return if (includeDelimiter) {
            frontDelimiter + subStr + behindDelimiter
        } else {
            subStr
        }
    }

    /**
     * Returns true if this string is surrounded with the specified delimiter.
     *
     * @param delimiter Delimiter
     */
    fun String.surroundingWith(delimiter: String): Boolean {
        return this.startsWith(delimiter) && this.endsWith(delimiter)
    }

    /**
     * Returns true if this string is surrounded with the specified prefix and suffix.
     *
     * @param prefix Prefix
     * @param suffix Suffix
     */
    fun String.surroundingWith(prefix: String, suffix: String): Boolean {
        return this.startsWith(prefix) && this.endsWith(suffix)
    }

    /**
     * Returns data class from original string
     *
     * @param T Data class
     */
    inline fun <reified T> String.decode(): T? {
        return moshi.adapter(T::class.java).fromJson(this.trim())
    }
}
