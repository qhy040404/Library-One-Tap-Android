package com.qhy040404.libraryonetap.utils.extensions

/**
 * Check if a element of a collection of char or string is in the target string
 *
 * @param string
 */
fun Collection<CharSequence>.hasElementIn(string: String): Boolean {
    this.forEach {
        return string.contains(it)
    }
    return false
}
