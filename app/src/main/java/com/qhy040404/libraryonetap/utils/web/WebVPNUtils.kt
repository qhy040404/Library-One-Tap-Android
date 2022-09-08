package com.qhy040404.libraryonetap.utils.web

import com.qhy040404.libraryonetap.annotation.HttpProtocols
import com.qhy040404.libraryonetap.runner.JsRunner

@Suppress("unused") // Remove when call encrypt()
object WebVPNUtils {
    private const val institution = "https://webvpn.dlut.edu.cn"

    @Suppress("SpellCheckingInspection")
    fun encrypt(url: String, @HttpProtocols protocol: String): String {
        return institution + JsRunner.callFunc("encrypUrl", protocol, url).toString()
    }
}
