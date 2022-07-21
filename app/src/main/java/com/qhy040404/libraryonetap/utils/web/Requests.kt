package com.qhy040404.libraryonetap.utils.web

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

@Suppress("SpellCheckingInspection")
object Requests {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .cookieJar(object : CookieJar {
            private val cookieStore = HashMap<String, List<Cookie>>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url.host] = cookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies: List<Cookie>? = cookieStore[url.host]
                return cookies ?: ArrayList()
            }
        })
        .build()

    fun strToMT(ori: String): MediaType {
        return ori.toMediaType()
    }

    @Throws(IOException::class)
    fun get(url: String): String {
        return get(url, false)
    }

    @Throws(IOException::class)
    fun get(url: String, getUrl: Boolean): String {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (getUrl) {
                return response.request.url.toString()
            }
            return response.body!!.string()
        }
    }

    @Throws(IOException::class)
    fun getVCard(url: String): String {
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .get()
            .build()
        client.newCall(request).execute().use { response -> return response.body!!.string() }
    }

    @Throws(IOException::class)
    fun post(url: String, form: String, FORM: MediaType): String {
        return post(url, form, FORM, false)
    }

    @Throws(IOException::class)
    fun post(url: String, form: String, FORM: MediaType, getUrl: Boolean): String {
        val body = form.toRequestBody(FORM)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            if (getUrl) {
                return response.request.url.toString()
            }
            return response.body!!.string()
        }
    }

    @Throws(IOException::class)
    fun postVCard(url: String, form: String, FORM: MediaType): String {
        val body = form.toRequestBody(FORM)
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .post(body)
            .build()
        client.newCall(request).execute().use { response -> return response.body!!.string() }
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