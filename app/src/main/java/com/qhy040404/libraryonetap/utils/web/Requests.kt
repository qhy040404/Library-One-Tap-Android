package com.qhy040404.libraryonetap.utils.web

import com.qhy040404.libraryonetap.utils.lazy.ResettableLazyUtils
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@Suppress("SpellCheckingInspection")
object Requests {
    val netLazyMgr = ResettableLazyUtils.resettableManager()
    val client by ResettableLazyUtils.resettableLazy(netLazyMgr) {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
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

    fun get(url: String): String {
        return get(url, false)
    }

    fun get(url: String, getUrl: Boolean): String {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (getUrl) {
                    return response.request.url.toString()
                }
                return response.body!!.string()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun getVCard(url: String): String {
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .get()
            .build()
        try {
            client.newCall(request).execute()
                .use { response -> return response.body!!.string() }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun post(url: String, form: String, FORM: MediaType): String {
        return post(url, form, FORM, false)
    }

    fun post(url: String, form: String, FORM: MediaType, getUrl: Boolean): String {
        val body = form.toRequestBody(FORM)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (getUrl) {
                    return response.request.url.toString()
                }
                return response.body!!.string()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun postVCard(url: String, form: String, FORM: MediaType): String {
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
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun loginPostData(
        id: String,
        passwd: String,
        ltData: String,
        rsa: String,
        execution: String,
    ): String {
        return "none=on&rsa=$rsa&ul=${id.length}&pl=${passwd.length}&sl=0&lt=$ltData&execution=$execution&_eventId=submit"
    }
}