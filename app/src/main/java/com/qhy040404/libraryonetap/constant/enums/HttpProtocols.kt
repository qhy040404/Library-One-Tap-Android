package com.qhy040404.libraryonetap.constant.enums

import java.util.Locale

@Suppress("unused")
enum class HttpProtocols {
    HTTP, HTTPS;

    override fun toString(): String {
        return super.toString().lowercase(Locale.getDefault())
    }
}
