package com.qhy040404.libraryonetap.utils.web

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

object CookieJarImpl : CookieJar {
    private val cookieStore = mutableListOf<Cookie>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore.addAll(cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore
    }

    fun reset() {
        cookieStore.clear()
    }
}
