package com.qhy040404.libraryonetap.annotation

import androidx.annotation.StringDef

@StringDef(HttpProtocols.HTTP, HttpProtocols.HTTPS)
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class HttpProtocols {
    companion object {
        const val HTTP = "http"
        const val HTTPS = "https"
    }
}
