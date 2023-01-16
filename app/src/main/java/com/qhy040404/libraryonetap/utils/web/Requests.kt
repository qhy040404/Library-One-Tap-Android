package com.qhy040404.libraryonetap.utils.web

import androidx.appcompat.widget.AppCompatTextView
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.SessionData
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getString
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.substringBetween
import com.qhy040404.libraryonetap.utils.lazy.resettableLazy
import com.qhy040404.libraryonetap.utils.lazy.resettableManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

@Suppress("SpellCheckingInspection")
object Requests {
    var libInitialized = false

    val netLazyMgr = resettableManager()
    val client by resettableLazy(netLazyMgr) {
        OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .cookieJar(CookieJarImpl)
            .build()
    }
    private val toolsClient = OkHttpClient.Builder()
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

    fun get(
        url: String,
        textView: AppCompatTextView? = null,
        getUrl: Boolean = false,
        toolsInit: Boolean = false,
    ): String {
        if (!AppUtils.hasNetwork()) {
            textView?.post { textView.text = R.string.glb_net_disconnected.getString() }
            GlobalValues.netError = true
            return Constants.NET_DISCONNECTED
        }
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            (if (toolsInit) {
                toolsClient
            } else {
                client
            }).newCall(request).execute().use { response ->
                if (getUrl) {
                    return response.request.url.toString()
                }
                return response.body!!.string()
            }
        } catch (s: SocketTimeoutException) {
            textView?.post { textView.text = R.string.glb_net_timeout.getString() }
            GlobalValues.netError = true
            return Constants.NET_TIMEOUT
        } catch (h: UnknownHostException) {
            textView?.post { textView.text = R.string.glb_net_error.getString() }
            GlobalValues.netError = true
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun getVCard(url: String): String {
        if (!AppUtils.hasNetwork()) {
            return Constants.NET_DISCONNECTED
        }
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .get()
            .build()
        try {
            client.newCall(request).execute().use { response ->
                return response.body!!.string()
            }
        } catch (s: SocketTimeoutException) {
            return Constants.NET_TIMEOUT
        } catch (h: UnknownHostException) {
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun post(
        url: String,
        form: String,
        mediaType: MediaType,
        textView: AppCompatTextView? = null,
        getUrl: Boolean = false,
        toolsInit: Boolean = false,
    ): String {
        if (!AppUtils.hasNetwork()) {
            textView?.post { textView.text = R.string.glb_net_disconnected.getString() }
            GlobalValues.netError = true
            return Constants.NET_DISCONNECTED
        }
        val body = form.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        try {
            (if (toolsInit) {
                toolsClient
            } else {
                client
            }).newCall(request).execute().use { response ->
                if (getUrl) {
                    return response.request.url.toString()
                }
                return response.body!!.string()
            }
        } catch (s: SocketTimeoutException) {
            textView?.post { textView.text = R.string.glb_net_timeout.getString() }
            GlobalValues.netError = true
            return Constants.NET_TIMEOUT
        } catch (h: UnknownHostException) {
            textView?.post { textView.text = R.string.glb_net_error.getString() }
            GlobalValues.netError = true
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun postVCard(url: String, form: String, mediaType: MediaType): String {
        if (!AppUtils.hasNetwork()) {
            return Constants.NET_DISCONNECTED
        }
        val body = form.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .post(body)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                return response.body!!.string()
            }
        } catch (s: SocketTimeoutException) {
            return Constants.NET_TIMEOUT
        } catch (h: UnknownHostException) {
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun loginSso(
        sso: String,
        mt: MediaType,
        session: String? = null,
        noJsonString: String = "统一身份",
        hasSessionJson: Boolean = false,
        toolsInit: Boolean = false,
    ): Boolean {
        val id = GlobalValues.id
        val passwd = GlobalValues.passwd

        var loginSuccess = false
        var timer = 0

        while (AppUtils.checkData(id, passwd)) {
            val ltResponse = get(sso, toolsInit = toolsInit)
            val ltData = runCatching {
                ltResponse.substringBetween("LT", "cas", includeDelimiter = true)
            }.getOrDefault(Constants.STRING_NULL)
            val ltExecution = runCatching {
                ltResponse.substringBetween("name=\"execution\" value=\"", "\"")
            }.getOrDefault(Constants.STRING_NULL)

            if (ltExecution.isNotEmpty()) {
                val rawData = "$id$passwd$ltData"
                val rsa = DesEncryptUtils.strEnc(rawData, "1", "2", "3")

                Thread.sleep(200L)

                post(
                    sso,
                    loginPostData(id, passwd, ltData, rsa, ltExecution),
                    mt,
                    toolsInit = toolsInit
                )
            }

            if (session != null) {
                if (hasSessionJson) {
                    val sessionCheck = post(session, "", mt, toolsInit = toolsInit)
                    if (SessionData.isSuccess(sessionCheck)) {
                        loginSuccess = true
                        break
                    } else {
                        timer++
                    }
                } else {
                    val sessionCheck = get(session, toolsInit = toolsInit)
                    if (!sessionCheck.contains(noJsonString)) {
                        loginSuccess = true
                        break
                    } else {
                        timer++
                    }
                }
                if (timer == 2) {
                    netLazyMgr.reset()
                    SPUtils.spLazyMgr.reset()
                    GlobalValues.initBasic()
                }
                if (timer >= 3) {
                    break
                }
            } else {
                break
            }
        }
        return loginSuccess
    }

    fun loginPostData(
        id: String,
        passwd: String,
        ltData: String,
        rsa: String,
        execution: String,
    ) =
        "none=on&rsa=$rsa&ul=${id.length}&pl=${passwd.length}&sl=0&lt=$ltData&execution=$execution&_eventId=submit"

    fun initLib(): Boolean {
        synchronized(client) {
            if (libInitialized) {
                return true
            }
            loginSso(URLManager.LIBRARY_SSO_URL,
                GlobalValues.ctSso,
                URLManager.LIBRARY_SESSION_URL,
                hasSessionJson = true).also {
                libInitialized = it
                return it
            }
        }
    }
}
