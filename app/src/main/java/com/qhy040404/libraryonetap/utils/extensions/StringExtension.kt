package com.qhy040404.libraryonetap.utils.extensions

import com.qhy040404.libraryonetap.constant.Constants

object StringExtension {
    fun String.isValid(): Boolean {
        return this != Constants.GLOBAL_ERROR && this != Constants.NET_TIMEOUT && this != Constants.NET_ERROR && this != Constants.NET_DISCONNECTED
    }
}