package com.qhy040404.libraryonetap.utils.web

import android.widget.TextView
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.lazy.ResettableLazyUtils
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

@Suppress("SpellCheckingInspection")
object Requests {
    val netLazyMgr = ResettableLazyUtils.resettableManager()
    val client by ResettableLazyUtils.resettableLazy(netLazyMgr) {
        OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .cookieJar(object : CookieJar {
                private val cookieStore = mutableListOf<Cookie>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore.addAll(cookies)
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore
                }
            })
            .build()
    }

    fun get(url: String, textView: TextView? = null) = get(url, textView, false)

    fun get(url: String, textView: TextView? = null, getUrl: Boolean): String {
        if (!AppUtils.hasNetwork()) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_disconnected) }
            GlobalValues.netError = true
            return Constants.NET_DISCONNECTED
        }
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (getUrl) return response.request.url.toString()
                return response.body!!.string()
            }
        } catch (socket: SocketTimeoutException) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_timeout) }
            GlobalValues.netError = true
            return Constants.NET_TIMEOUT
        } catch (host: UnknownHostException) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_error) }
            GlobalValues.netError = true
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun getVCard(url: String): String {
        if (!AppUtils.hasNetwork()) return Constants.NET_DISCONNECTED
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .get()
            .build()
        try {
            client.newCall(request).execute()
                .use { response -> return response.body!!.string() }
        } catch (socket: SocketTimeoutException) {
            return Constants.NET_TIMEOUT
        } catch (host:UnknownHostException) {
            return Constants.NET_ERROR
        } catch (e:Exception) {
            return Constants.STRING_NULL
        }
    }

    fun post(url: String, form: String, FORM: MediaType, textView: TextView? = null) =
        post(url, form, FORM, textView, false)

    fun post(
        url: String,
        form: String,
        FORM: MediaType,
        textView: TextView? = null,
        getUrl: Boolean,
    ): String {
        if (!AppUtils.hasNetwork()) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_disconnected) }
            GlobalValues.netError = true
            return Constants.NET_DISCONNECTED
        }
        val body = form.toRequestBody(FORM)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (getUrl) return response.request.url.toString()
                return response.body!!.string()
            }
        } catch (socket:SocketTimeoutException) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_timeout) }
            GlobalValues.netError = true
            return Constants.NET_TIMEOUT
        } catch (host:UnknownHostException) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_error) }
            GlobalValues.netError = true
            return Constants.NET_ERROR
        } catch (e:Exception) {
            return Constants.STRING_NULL
        }
    }

    fun postVCard(url: String, form: String, FORM: MediaType): String {
        if (!AppUtils.hasNetwork()) return Constants.NET_DISCONNECTED
        val body = form.toRequestBody(FORM)
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .post(body)
            .build()
        try {
            client.newCall(request).execute()
                .use { response -> return response.body!!.string() }
        } catch (socket:SocketTimeoutException) {
            return Constants.NET_TIMEOUT
        } catch (host:UnknownHostException) {
            return Constants.NET_ERROR
        } catch (e:Exception) {
            return Constants.STRING_NULL
        }
    }

    fun loginPostData(
        id: String,
        passwd: String,
        ltData: String,
        rsa: String,
        execution: String,
    ) =
        "none=on&rsa=$rsa&ul=${id.length}&pl=${passwd.length}&sl=0&lt=$ltData&execution=$execution&_eventId=submit"
}