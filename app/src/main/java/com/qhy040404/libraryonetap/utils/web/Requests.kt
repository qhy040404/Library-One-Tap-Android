package com.qhy040404.libraryonetap.utils.web

import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.SessionDTO
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.extensions.decode
import com.qhy040404.libraryonetap.utils.extensions.getString
import com.qhy040404.libraryonetap.utils.extensions.substringBetween
import com.qhy040404.libraryonetap.utils.extensions.toJson
import com.qhy040404.libraryonetap.utils.lazy.resettableLazy
import com.qhy040404.libraryonetap.utils.lazy.resettableManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Suppress("SpellCheckingInspection")
object Requests {
    var libInitialized = false
    var eduInitialized = false

    val netLazyMgr = resettableManager()
    val client by resettableLazy(netLazyMgr) {
        OkHttpClient.Builder()
            .cookieJar(CookieJarImpl)
            .build()
    }

    private val EDU_HOST = URLManager.EDU_DOMAIN.toHttpUrl().host

    fun get(
        url: String,
        headers: Headers = mapOf<String, String>().toHeaders(),
        getUrl: Boolean = false,
    ): String {
        if (!AppUtils.hasNetwork()) {
            GlobalValues.netPrompt = R.string.glb_net_disconnected.getString()
            GlobalValues.netError = true
            return Constants.NET_DISCONNECTED
        }
        GlobalValues.netPrompt = Constants.STRING_NULL
        val request = Request.Builder()
            .url(url)
            .apply {
                headers.forEach { (name, value) ->
                    header(name, value)
                }
            }
            .get()
            .build()
        try {
            runBlocking(Dispatchers.IO) {
                if (url.contains(EDU_HOST)) {
                    delay(500L)
                }
                client.newCall(request).execute()
            }.use { response ->
                if (getUrl) {
                    return response.request.url.toString()
                }
                return runBlocking(Dispatchers.IO) {
                    response.body!!.string()
                }
            }
        } catch (s: SocketTimeoutException) {
            GlobalValues.netPrompt = R.string.glb_net_timeout.getString()
            GlobalValues.netError = true
            return Constants.NET_TIMEOUT
        } catch (h: UnknownHostException) {
            GlobalValues.netPrompt = R.string.glb_net_error.getString()
            GlobalValues.netError = true
            return Constants.NET_ERROR
        } catch (e: Exception) {
            GlobalValues.netPrompt = R.string.glb_unknown_error.getString()
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
            runBlocking(Dispatchers.IO) {
                client.newCall(request).execute()
            }.use { response ->
                return runBlocking(Dispatchers.IO) {
                    response.body!!.string()
                }
            }
        } catch (s: SocketTimeoutException) {
            return Constants.NET_TIMEOUT
        } catch (h: UnknownHostException) {
            return Constants.NET_ERROR
        } catch (e: Exception) {
            GlobalValues.netPrompt = R.string.glb_unknown_error.getString()
            return Constants.STRING_NULL
        }
    }

    fun post(
        url: String,
        form: String,
        mediaType: MediaType,
        headers: Headers = mapOf<String, String>().toHeaders(),
        getUrl: Boolean = false,
    ): String {
        if (!AppUtils.hasNetwork()) {
            GlobalValues.netPrompt = R.string.glb_net_disconnected.getString()
            GlobalValues.netError = true
            return Constants.NET_DISCONNECTED
        }
        GlobalValues.netPrompt = Constants.STRING_NULL
        val body = form.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .apply {
                headers.forEach { (name, value) ->
                    header(name, value)
                }
            }
            .post(body)
            .build()
        try {
            runBlocking(Dispatchers.IO) {
                if (url.contains(EDU_HOST)) {
                    delay(500L)
                }
                client.newCall(request).execute()
            }.use { response ->
                if (getUrl) {
                    return response.request.url.toString()
                }
                return runBlocking(Dispatchers.IO) {
                    response.body!!.string()
                }
            }
        } catch (s: SocketTimeoutException) {
            GlobalValues.netPrompt = R.string.glb_net_timeout.getString()
            GlobalValues.netError = true
            return Constants.NET_TIMEOUT
        } catch (h: UnknownHostException) {
            GlobalValues.netPrompt = R.string.glb_net_error.getString()
            GlobalValues.netError = true
            return Constants.NET_ERROR
        } catch (e: Exception) {
            GlobalValues.netPrompt = R.string.glb_unknown_error.getString()
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
            runBlocking(Dispatchers.IO) {
                client.newCall(request).execute()
            }.use { response ->
                return runBlocking(Dispatchers.IO) {
                    response.body!!.string()
                }
            }
        } catch (s: SocketTimeoutException) {
            return Constants.NET_TIMEOUT
        } catch (h: UnknownHostException) {
            return Constants.NET_ERROR
        } catch (e: Exception) {
            GlobalValues.netPrompt = R.string.glb_unknown_error.getString()
            return Constants.STRING_NULL
        }
    }

    fun loginSso(
        ssoUrl: String,
        mt: MediaType,
        sessionUrl: String? = null,
        hasSessionJson: Boolean = false,
        shouldMiss: String = "统一身份",
        shouldHas: String = "",
    ): Boolean {
        val id = GlobalValues.id
        val passwd = GlobalValues.passwd

        GlobalValues.ssoPrompt = Constants.STRING_NULL

        var loginSuccess = false
        var timer = 0

        while (AppUtils.checkData(id, passwd)) {
            val ltResponse = get(ssoUrl)
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
                    ssoUrl,
                    loginPostData(id, passwd, ltData, rsa, ltExecution),
                    mt
                )
            } else {
                return false
            }

            if (sessionUrl != null) {
                if (hasSessionJson) {
                    val sessionCheck = post(sessionUrl, "", mt)
                    if (sessionCheck.decode<SessionDTO>()?.success == true) {
                        loginSuccess = true
                        break
                    } else {
                        timer++
                    }
                } else if (shouldHas.isNotEmpty()) {
                    val sessionCheck = get(sessionUrl)
                    if (sessionCheck.contains(shouldHas)) {
                        loginSuccess = true
                        break
                    } else {
                        timer++
                    }
                } else if (shouldMiss.isNotEmpty()) {
                    val sessionCheck = get(sessionUrl)
                    if (!sessionCheck.contains(shouldMiss)) {
                        loginSuccess = true
                        break
                    } else {
                        timer++
                    }
                }
                if (timer == 2) {
                    netLazyMgr.reset()
                    SPUtils.spLazyMgr.reset()
                    CookieJarImpl.reset()
                    GlobalValues.initBasic()
                }
                if (timer >= 3) {
                    break
                }
            } else {
                break
            }
        }
        return loginSuccess.also {
            if (it.not()) GlobalValues.ssoPrompt = R.string.glb_invalid_credentials.getString()
        }
    }

    private fun loginPostData(
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
            loginSso(
                URLManager.LIBRARY_SSO_URL,
                GlobalValues.ctSso,
                URLManager.LIBRARY_SESSION_URL,
                hasSessionJson = true
            ).also {
                libInitialized = it
                return it
            }
        }
    }

    fun initEdu(): Boolean {
        synchronized(client) {
            if (eduInitialized) {
                return true
            }
            loginSso(
                URLManager.EDU_LOGIN_SSO_URL,
                GlobalValues.ctSso,
                URLManager.EDU_CHECK_URL,
                shouldHas = "person"
            ).also {
                if (it) {
                    initEduEval()
                }
                eduInitialized = it
                return it
            }
        }
    }

    private fun initEduEval() {
        val initToken = get(URLManager.EDU_EVALUATION_URL).substringBetween("token=", "';")
        val finalToken = post(
            URLManager.EDU_EVALUATION_TOKEN_URL,
            mapOf("token" to initToken).toJson()!!,
            GlobalValues.ctJson
        ).let {
            JSONObject(it).optJSONObject("data")!!.optString("token")
        }
        val url = URLManager.EDU_DOMAIN.toHttpUrl()
        CookieJarImpl.saveFromResponse(
            url,
            listOf(
                Cookie.Builder().name("student_evaluation_login_way").value("SSO")
                    .hostOnlyDomain(url.host).build(),
                Cookie.Builder().name("student_evaluation_token").value(finalToken)
                    .hostOnlyDomain(url.host).build()
            )
        )
    }
}
