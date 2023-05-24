package com.qhy040404.libraryonetap.utils.web

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

object CookieJarImpl : CookieJar {
    private val cookieStore = hashMapOf<String, HashSet<Cookie>>()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookieStore[url.host] == null) cookieStore[url.host] = hashSetOf()
        cookieStore[url.host]?.replaceIfExists(cookies)
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url.host]?.toList() ?: emptyList()
    }

    private fun <T> MutableSet<T>.replaceIfExists(newData: Collection<T>) {
        this.removeAll(newData.toSet())
        this.addAll(newData)
    }

    fun reset() {
        cookieStore.clear()
    }
}
