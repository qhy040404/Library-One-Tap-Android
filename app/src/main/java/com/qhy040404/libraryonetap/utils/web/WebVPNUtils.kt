package com.qhy040404.libraryonetap.utils.web

import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.encrypt.AesEncryptUtils
import java.util.Locale

object WebVPNUtils {
    fun init() {
        if (!AppUtils.checkData(GlobalValues.id, GlobalValues.passwd)) {
            return
        }
        val apiPostData =
            "schoolcode=dlut&username=${GlobalValues.id}&password=${GlobalValues.passwd}&ssokey="
        Requests.post(URLManager.WEBVPN_INIT_URL, apiPostData, GlobalValues.ctVCard)
    }

    fun encryptUrl(url: String): String {
        val protocol: HttpProtocols
        var port = ""

        var mUrl = if (url.startsWith("http://")) {
            protocol = HttpProtocols.HTTP
            url.removePrefix("http://")
        } else if (url.startsWith("https://")) {
            protocol = HttpProtocols.HTTPS
            url.removePrefix("https://")
        } else {
            throw IllegalArgumentException("Illegal url")
        }

        var v6 = ""
        Regex("\\[[0-9a-fA-F:]+?]").find(mUrl).let {
            if (it != null) {
                v6 = it.value
                mUrl = mUrl.substring(v6.length)
            }
        }
        val segments = mUrl.substringBefore('?').split(':')
        if (segments.size > 1) {
            port = segments[1].substringBefore('/')
            mUrl = mUrl.substring(0, segments[0].length) +
                mUrl.substring(segments[0].length + port.length + 1)
        }

        val i = mUrl.indexOf('/')
        if (i == -1) {
            if (v6 != "") {
                mUrl = v6
            }
            mUrl = AesEncryptUtils.vpnEncrypt(mUrl, "wrdvpnisthebest!", "wrdvpnisthebest!")
        } else {
            var host = mUrl.substring(0, i)
            val path = mUrl.substring(i)
            if (v6 != "") {
                host = v6
            }
            mUrl = AesEncryptUtils.vpnEncrypt(host, "wrdvpnisthebest!", "wrdvpnisthebest!") + path
        }
        return if (port != "") {
            "${URLManager.WEBVPN_INSTITUTION_URL}/$protocol-$port/$mUrl"
        } else {
            "${URLManager.WEBVPN_INSTITUTION_URL}/$protocol/$mUrl"
        }
    }

    enum class HttpProtocols {
        HTTP, HTTPS;

        override fun toString(): String {
            return super.toString().lowercase(Locale.getDefault())
        }
    }
}
