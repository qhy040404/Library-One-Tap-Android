package com.qhy040404.libraryonetap.utils.web

import com.qhy040404.libraryonetap.annotation.HttpProtocols
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.runner.JsRunner
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.encrypt.AESEncryptUtils

object WebVPNUtils {
    fun init() {
        if (!AppUtils.checkData(GlobalValues.id, GlobalValues.passwd)) return
        val apiPostData =
            "schoolcode=dlut&username=${GlobalValues.id}&password=${GlobalValues.passwd}&ssokey="
        Requests.post(URLManager.WEBVPN_INIT_URL, apiPostData, GlobalValues.ctVCard)
    }

    @Suppress("SpellCheckingInspection")
    fun encrypt(url: String, @HttpProtocols protocol: String): String {
        if (!AESEncryptUtils.initialized) throw IllegalStateException("Call AES init() first!")
        return URLManager.WEBVPN_INSTITUTION_URL + JsRunner.callFunc("encrypUrl", protocol, url)
            .toString()
    }
}
